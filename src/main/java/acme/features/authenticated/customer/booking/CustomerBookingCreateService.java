
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
import acme.entities.S1.Flight;
import acme.entities.S1.FlightRepository;
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
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		boolean authorised = isCustomer;

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

		if (super.getRequest().hasData(CustomerBookingCreateService.FLIGHT_ID_FIELD))
			try {
				int flightId = super.getRequest().getData(CustomerBookingCreateService.FLIGHT_ID_FIELD, int.class);
				flight = this.flightRepository.findFlightById(flightId);
			} catch (final Throwable t) {
				// Ignorado: se valida después
			}

		super.bindObject(booking, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "lastCardDigits", "travelClass");

		booking.setFlightId(flight);
		booking.setDraftMode(false);

		if (flight != null) {
			Money basePrice = this.flightRepository.findCostByFlight(flight.getId());
			booking.setPrice(basePrice);
		} else
			booking.setPrice(null);
	}

	@Override
	public void validate(final Booking booking) {
		Booking b = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		if (b != null)
			super.state(false, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "acme.validation.confirmation.message.booking.locator-code");
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

		if (flights == null || flights.isEmpty())
			throw new IllegalArgumentException("No hay vuelos disponibles");

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
