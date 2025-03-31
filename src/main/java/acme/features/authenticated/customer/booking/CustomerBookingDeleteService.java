
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Flight;
import acme.entities.S1.FlightRepository;
import acme.entities.S2.Booking;
import acme.entities.S2.BookingRecord;
import acme.entities.S2.TravelClass;
import acme.features.authenticated.customer.bookingRecord.CustomerBookingRecordRepository;
import acme.realms.Customer;

@GuiService
public class CustomerBookingDeleteService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository		repository;

	@Autowired
	private FlightRepository				flightRepository;

	@Autowired
	private CustomerBookingRecordRepository	bookingRecordRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id;
		Booking booking;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		boolean status = booking.getCustomer().getUserAccount().getId() == customerId && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id;
		Booking booking;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.flightRepository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "purchaseMoment", "lastCardDigits", "price", "travelClass", "draftMode");
		booking.setFlightId(flight);

	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		Collection<BookingRecord> br = this.repository.findBookingRecordByBooking(booking.getId());
		for (BookingRecord bookingRecord : br)
			this.bookingRecordRepository.delete(bookingRecord);
		this.repository.delete(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;

		Collection<Flight> flights = this.flightRepository.findAllFlight();
		flightChoices = SelectChoices.from(flights, "description", booking.getFlightId());
		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "lastCardDigits", "price", "draftMode");
		dataset.put("travelClass", choices);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}
}
