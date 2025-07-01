
package acme.features.authenticated.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student1.FlightRepository;
import acme.entities.student2.Booking;
import acme.entities.student2.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	repository;

	@Autowired
	private FlightRepository			flightRepository;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);

		boolean authorised = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !booking.isDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		assert booking != null;

		super.bindObject(booking, "travelClass", "lastCardDigits", "locatorCode", "flightId");

		// Ignorar campos que pueden haber sido manipulados
		int id = super.getRequest().getData("id", int.class);
		Booking original = this.repository.findById(id);

		booking.setPrice(original.getPrice());
		booking.setPurchaseMoment(original.getPurchaseMoment());
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		boolean Notpublished = !booking.isDraftMode();
		super.state(Notpublished, "draftMode", "customer.booking.form.error.alreadyPublished");

	}
	@Override
	public void perform(final Booking booking) {
		assert booking != null;

		Money pricePerPassenger = this.flightRepository.findCostByFlight(booking.getFlightId().getId());

		int passengerCount = this.repository.countNumberOfPassengersOfBooking(booking.getId());

		Money newPrice = new Money();
		if (passengerCount == 0)
			newPrice.setAmount(pricePerPassenger.getAmount());
		else
			newPrice.setAmount(pricePerPassenger.getAmount() * passengerCount);
		newPrice.setCurrency(pricePerPassenger.getCurrency());

		booking.setPrice(newPrice);

		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices flights = SelectChoices.from(this.repository.findAllFlights(), "id", booking.getFlightId());
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "purchaseMoment", "lastCardDigits");
		dataset.put("travelClasses", travelClasses);
		dataset.put("flights", flights);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
