
package acme.features.authenticated.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishedService extends AbstractGuiService<Customer, Booking> {

	// Constants --------------------------------------------------------------

	private static final String			DRAFT_MODE_FIELD	= "draftMode";

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);

		boolean authorised = booking != null && !booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void validate(final Booking booking) {
		String lastNibble = booking.getLastCardDigits();
		boolean lastNibbleNotNull = !lastNibble.isBlank();

		int passengerCount = this.repository.countNumberOfPassengersOfBooking(booking.getId());
		boolean hasPassengers = passengerCount > 0;

		super.state(hasPassengers, CustomerBookingPublishedService.DRAFT_MODE_FIELD, "customer.booking.publish.error.noPassengers");
		super.state(lastNibbleNotNull, CustomerBookingPublishedService.DRAFT_MODE_FIELD, "customer.booking.publish.error.LastCardDigits");
	}

	@Override
	public void bind(final Booking booking) {
		// No binding necesario para publicación
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(true);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices flights = SelectChoices.from(this.repository.findAllFlights(), "id", booking.getFlightId());
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "flightId", CustomerBookingPublishedService.DRAFT_MODE_FIELD, "purchaseMoment");
		dataset.put("flights", flights);
		dataset.put("travelClasses", travelClasses);
		dataset.put("bookingId", booking.getId());
		super.getResponse().addData(dataset);
	}
}
