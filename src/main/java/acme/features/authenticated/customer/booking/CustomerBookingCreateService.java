
package acme.features.authenticated.customer.booking;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

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
		// 1) solo comprobamos rol
		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		// 2) Anti-F12 únicamente en POST y solo si los selects traen valor real
		if (authorised && "POST".equalsIgnoreCase(super.getRequest().getMethod())) {

			// Lectura segura como String (no fallará si no existe o viene vacío)
			String rawFlightId = null;
			String rawTravel = null;
			try {
				rawFlightId = super.getRequest().getData(CustomerBookingCreateService.FLIGHT_ID_FIELD, String.class);
			} catch (Throwable ignored) {
			}
			try {
				rawTravel = super.getRequest().getData("travelClass", String.class);
			} catch (Throwable ignored) {
			}

			// ─ flightId: si viene con valor distinto de "0"/vacío => debe ser numérico y publicado
			if (rawFlightId != null && !rawFlightId.isBlank() && !"0".equals(rawFlightId.trim())) {
				final String t = rawFlightId.trim();
				if (!t.chars().allMatch(Character::isDigit))
					authorised = false;                           // manipulado
				else {
					final int id = Integer.parseInt(t);
					final Flight f = this.flightRepository.findFlightById(id);
					authorised = f != null && !f.getDraftMode();  // debe existir y no estar en borrador
				}
			}

			// ─ travelClass: si viene con valor distinto de "0"/vacío => debe ser un Enum válido
			if (authorised && rawTravel != null && !rawTravel.isBlank() && !"0".equals(rawTravel.trim()))
				try {
					TravelClass.valueOf(rawTravel.trim());        // ok si es un valor del enum
				} catch (Throwable ex) {
					authorised = false;                           // manipulado
				}
		}

		// Si los selects vienen a "0" o vacíos, no se bloquea: pasará a validate() y verás los mensajes por campo.
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
		Objects.requireNonNull(booking, "booking");

		super.state(booking.getFlightId() != null, "flightId", "customer.booking.form.error.flight-required");

		if (booking.getFlightId() != null)
			super.state(!booking.getFlightId().getDraftMode(), "flightId", "customer.booking.form.error.flight-not-published");

		if (!super.getBuffer().getErrors().hasErrors(CustomerBookingCreateService.LOCATOR_CODE_FIELD)) {
			final String locator = booking.getLocatorCode();
			if (locator != null && !locator.isBlank()) {
				final boolean isUnique = this.repository.findBookingsByLocatorCode(locator.trim()).isEmpty();
				super.state(isUnique, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "customer.booking.form.error.locatorCode");
			}
		}
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

		Collection<Flight> flights = this.flightRepository.findAllPublishedFlights();

		flightChoices = SelectChoices.from(flights, "tag", booking.getFlightId());
		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, CustomerBookingCreateService.LOCATOR_CODE_FIELD, "lastCardDigits", "price");
		dataset.put("travelClass", choices);

		dataset.put(CustomerBookingCreateService.FLIGHT_ID_FIELD, flightChoices.getSelected() != null ? flightChoices.getSelected().getKey() : null);
		dataset.put("flights", flightChoices);

		dataset.put("purchaseMoment", booking.getPurchaseMoment());

		super.getResponse().addData(dataset);
	}

}
