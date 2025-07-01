
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
		Flight flight = null;
		TravelClass travelClass = null;

		// Obtener el flightId enviado por el cliente
		if (super.getRequest().hasData("flightId"))
			try {
				int flightId = super.getRequest().getData("flightId", int.class);
				flight = this.customerBookingRepository.findFlightById(flightId);

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

		// Asignar los valores validados
		booking.setFlightId(flight);
		booking.setTravelClass(travelClass);
		booking.setDraftMode(false);

		// Establece otros atributos como locatorCode, lastCardDigits, etc.
		super.bindObject(booking, "locatorCode", "lastCardDigits");
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

		// Validar que el locatorCode sea único
		Collection<Booking> bookings = this.customerBookingRepository.findBookingsByLocatorCode(booking.getLocatorCode());
		boolean isUnique = bookings.isEmpty() || bookings.stream().allMatch(b -> b.getId() == booking.getId());
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
