
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student1.Flight;
import acme.entities.student2.Booking;
import acme.entities.student2.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository customerBookingRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		Integer bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.customerBookingRepository.findBookingById(bookingId);

		Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = status && booking.getCustomer().getId() == customerId && !booking.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", int.class);
		Booking booking = this.customerBookingRepository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flightId", "locatorCode", "travelClass", "lastCardDigits");
	}

	@Override
	public void validate(final Booking booking) {
		Collection<Booking> bookings = this.customerBookingRepository.findBookingsByLocatorCode(booking.getLocatorCode());
		boolean isUnique;

		isUnique = bookings.isEmpty() || bookings.stream().allMatch(b -> b.getId() == booking.getId());
		super.state(isUnique, "locatorCode", "customer.booking.form.error.locatorCode");
	}

	@Override
	public void perform(final Booking booking) {
		this.customerBookingRepository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<Flight> flights = this.customerBookingRepository.findAllPublishedFlights();
		SelectChoices flightChoices = SelectChoices.from(flights, "tag", booking.getFlightId());

		Boolean hasPassengers;
		hasPassengers = !this.customerBookingRepository.findPassengersByBooking(booking.getId()).isEmpty();
		super.getResponse().addGlobal("hasPassengers", hasPassengers);

		Dataset dataset = super.unbindObject(booking, "flightId", "customer", "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardDigits", "draftMode", "id");
		dataset.put("travelClass", travelClasses);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
