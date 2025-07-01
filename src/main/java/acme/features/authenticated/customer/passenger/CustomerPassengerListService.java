
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.Passenger;
import acme.features.authenticated.customer.booking.CustomerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	// Constants --------------------------------------------------------------

	private static final String				BOOKING_ID	= "bookingId";

	// Internal state ---------------------------------------------------------

	private final CustomerBookingRepository	bookingRepository;

	// Constructor ------------------------------------------------------------


	public CustomerPassengerListService(final CustomerBookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		int bookingId = super.getRequest().getData(CustomerPassengerListService.BOOKING_ID, int.class);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();

		boolean authorised = false;
		Booking booking = this.bookingRepository.findBookingById(bookingId);
		if (booking != null && booking.getCustomer().getUserAccount().getId() == userAccountId)
			authorised = true;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData(CustomerPassengerListService.BOOKING_ID, int.class);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();

		Booking booking = this.bookingRepository.findBookingById(bookingId);
		boolean isMine = booking.getCustomer().getUserAccount().getId() == userAccountId;

		super.getResponse().addGlobal(CustomerPassengerListService.BOOKING_ID, bookingId);
		super.getResponse().addGlobal("draftMode", booking.isDraftMode());

		if (isMine) {
			Collection<Passenger> passengers = this.bookingRepository.findPassengersByBooking(bookingId);
			super.getBuffer().addData(passengers);
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "specialNeeds");
		super.getResponse().addData(dataset);
	}
}
