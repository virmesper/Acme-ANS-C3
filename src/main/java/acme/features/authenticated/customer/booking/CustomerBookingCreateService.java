
package acme.features.authenticated.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
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
		TravelClass travelClass = null;

		// Obtener el vuelo enviado por el cliente
		if (super.getRequest().hasData("flightId"))
			try {
				int flightId = super.getRequest().getData("flightId", int.class);
				flight = this.flightRepository.findFlightById(flightId);

				// Validar que el vuelo esté en modo "publicado"
				if (flight == null || flight.getDraftMode())
					throw new IllegalArgumentException("Invalid flightId: " + flightId);
			} catch (final Throwable t) {
				// Lanza error 500 si el flightId es manipulado
				throw new RuntimeException("Internal Server Error: Invalid flightId", t);
			}

		// Validar que el travelClass no se pueda modificar ilegalmente
		if (super.getRequest().hasData("travelClass"))
			try {
				travelClass = super.getRequest().getData("travelClass", TravelClass.class);
				// Aquí puedes verificar si el travelClass está permitido para el cliente (según tu lógica)
			} catch (final Throwable t) {
				// Lanza error 500 si el travelClass es manipulado
				throw new RuntimeException("Internal Server Error: Invalid travelClass", t);
			}

		booking.setFlightId(flight);
		booking.setTravelClass(travelClass);
		booking.setDraftMode(false);

	}

	@Override
	public void validate(final Booking booking) {
		// Verificación de flightId y travelClass
		if (booking.getFlightId() == null || booking.getTravelClass() == null)
			throw new RuntimeException("Internal Server Error: Missing flightId or travelClass");

		// Asegúrate de que el vuelo es válido y no esté en borrador
		Flight flight = booking.getFlightId();
		super.state(flight != null && !flight.getDraftMode(), "flightId", "booking.form.error.flight.invalid");

		// Validación del travelClass, según tus reglas de negocio
		super.state(booking.getTravelClass() != null, "travelClass", "booking.form.error.travelClass.invalid");
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
