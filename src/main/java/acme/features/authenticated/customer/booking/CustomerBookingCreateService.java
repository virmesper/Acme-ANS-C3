
package acme.features.authenticated.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student1.Flight;
import acme.entities.student1.FlightRepository;
import acme.entities.student2.Booking;
import acme.entities.student2.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Constants ---------------------------------------------------------------

	private static final String			FLIGHT_ID_FIELD		= "flightId";
	private static final String			LOCATOR_CODE_FIELD	= "locatorCode";

	// Internal state ----------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	repository;

	@Autowired
	private FlightRepository			flightRepository;

	// AbstractGuiService interface --------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (authorised && super.getRequest().hasData(CustomerBookingCreateService.FLIGHT_ID_FIELD))
			try {
				int flightId = super.getRequest().getData(CustomerBookingCreateService.FLIGHT_ID_FIELD, int.class);
				Flight flight = this.flightRepository.findFlightById(flightId);
				authorised = flightId == 0 || flight != null && !flight.getDraftMode();
			} catch (final Throwable e) {
				authorised = false; // si alguien intenta manipular el valor
			}

		if (authorised && super.getRequest().hasData("travelClass"))
			try {
				TravelClass travelClass = super.getRequest().getData("travelClass", TravelClass.class);
				authorised = travelClass != null;
			} catch (final Throwable e) {
				authorised = false; // enum inválido = manipulación
			}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Booking booking;
		AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		int customerId = principal.getId();
		Customer customer = this.repository.findCustomerById(customerId);
		Date today = MomentHelper.getCurrentMoment();

		booking = new Booking();
		booking.setCustomer(customer);
		booking.setPurchaseMoment(today);
		booking.setDraftMode(false);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		Flight flight = null;
		TravelClass travelClass = null;

		if (super.getRequest().hasData(CustomerBookingCreateService.FLIGHT_ID_FIELD)) {
			int flightId = super.getRequest().getData(CustomerBookingCreateService.FLIGHT_ID_FIELD, int.class);
			flight = this.flightRepository.findFlightById(flightId);
		}

		if (super.getRequest().hasData("travelClass"))
			travelClass = super.getRequest().getData("travelClass", TravelClass.class);

		booking.setFlightId(flight);
		booking.setTravelClass(travelClass);
		booking.setDraftMode(false);

		super.bindObject(booking, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "lastCardDigits");

		if (flight != null) {
			Money basePrice = this.flightRepository.findCostByFlight(flight.getId());
			booking.setPrice(basePrice);
		}
	}

	@Override
	public void validate(final Booking booking) {
		Collection<Booking> bookings = this.repository.findBookingsByLocatorCode(booking.getLocatorCode());
		boolean status1 = bookings.isEmpty();
		super.state(status1, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "customer.booking.form.error.locatorCode");
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;

		Collection<Flight> flights = this.flightRepository.findAllFlight();

		flightChoices = SelectChoices.from(flights, "tag", booking.getFlightId());
		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "lastCardDigits", "price");
		dataset.put("travelClass", choices);
		dataset.put(CustomerBookingCreateService.FLIGHT_ID_FIELD, flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);
		dataset.put("purchaseMoment", booking.getPurchaseMoment());

		super.getResponse().addData(dataset);
	}

}
