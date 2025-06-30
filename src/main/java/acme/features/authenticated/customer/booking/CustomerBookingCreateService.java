
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
import acme.entities.S2.Booking;
import acme.entities.S2.TravelClass;
import acme.entities.student1.Flight;
import acme.entities.student1.FlightRepository;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	repository;

	@Autowired
	private FlightRepository			flightRepository;

	// AbstractGuiService interface -------------------------------------------


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

		// Validación segura del flightId
		if (super.getRequest().hasData("flightId"))
			try {
				int flightId = super.getRequest().getData("flightId", int.class);
				flight = this.flightRepository.findFlightById(flightId);
			} catch (final Throwable t) {
				// No hacemos nada, dejamos flight como null para validarlo más adelante
			}

		super.bindObject(booking, "locatorCode", "lastCardDigits", "travelClass");

		booking.setFlightId(flight);
		booking.setDraftMode(false);

		// Asignar precio si hay vuelo, si no se valida en validate()
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
			super.state(false, "locatorCode", "acme.validation.confirmation.message.booking.locator-code");
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

		// Validación: si no hay vuelos cargados, se considera error (opcional)
		if (flights == null || flights.isEmpty())
			throw new IllegalArgumentException("No hay vuelos disponibles");

		flightChoices = SelectChoices.from(flights, "tag", booking.getFlightId());
		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "lastCardDigits", "price");
		dataset.put("travelClass", choices);
		dataset.put("flightId", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);
		dataset.put("purchaseMoment", booking.getPurchaseMoment());

		super.getResponse().addData(dataset);
	}

}
