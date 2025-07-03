
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
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
		// ✅ Eliminar campos que pueden estar manipulados
		super.getRequest().getData().remove("price");
		super.getRequest().getData().remove("purchaseMoment");

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("id"))
			try {
				int bookingId = super.getRequest().getData("id", int.class);
				Booking booking = this.customerBookingRepository.findBookingById(bookingId);
				status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !booking.isDraftMode();
			} catch (final Throwable e) {
				status = false;
			}
		else
			status = false;

		if (status && super.getRequest().hasData("flightId"))
			try {
				int flightId = super.getRequest().getData("flightId", int.class);
				Flight flight = this.customerBookingRepository.findFlightById(flightId);
				status = flightId == 0 || flight != null && !flight.getDraftMode();
			} catch (final Throwable e) {
				status = false;
			}

		if (status && super.getRequest().hasData("travelClass"))
			try {
				TravelClass travelClass = super.getRequest().getData("travelClass", TravelClass.class);
				status = travelClass != null;
			} catch (final Throwable e) {
				status = false;
			}

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

		// ✅ Paso 2: Obtener datos legítimos y bindear los editables
		Flight flight = null;
		TravelClass travelClass = null;

		if (super.getRequest().hasData("flightId")) {
			int flightId = super.getRequest().getData("flightId", int.class);
			flight = this.customerBookingRepository.findFlightById(flightId);
		}

		if (super.getRequest().hasData("travelClass"))
			travelClass = super.getRequest().getData("travelClass", TravelClass.class);

		booking.setFlightId(flight);
		booking.setTravelClass(travelClass);

		// ✅ Bind solo de campos permitidos
		super.bindObject(booking, "locatorCode", "lastCardDigits");

		// ✅ Reasignar los valores correctos y fiables desde el servidor
		if (flight != null)
			booking.setPrice(flight.getCost());

		Booking original = this.customerBookingRepository.findBookingById(booking.getId());
		if (original != null)
			booking.setPurchaseMoment(original.getPurchaseMoment());
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
		Dataset dataset;

		// ⚠️ NO incluir 'price' ni 'purchaseMoment' en el bind principal
		dataset = super.unbindObject(booking, "flightId", "customer", "locatorCode", "travelClass", "lastCardDigits", "draftMode", "id");

		// ✅ Añadir price como string simple
		Money price = booking.getPrice();
		String priceFormatted = price.getAmount() + " " + price.getCurrency();
		dataset.put("priceDisplay", priceFormatted);

		// ✅ Añadir purchaseMoment como string simple
		String purchaseMomentFormatted = booking.getPurchaseMoment().toString(); // o usa un formateador
		dataset.put("purchaseMomentDisplay", purchaseMomentFormatted);

		// ✅ Choices para travelClass y flights
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<Flight> flights = this.customerBookingRepository.findAllPublishedFlights();
		SelectChoices flightChoices = SelectChoices.from(flights, "tag", booking.getFlightId());

		dataset.put("travelClass", travelClasses);
		dataset.put("flights", flightChoices);

		// ✅ Control de pasajeros
		Boolean hasPassengers = !this.customerBookingRepository.findPassengersByBooking(booking.getId()).isEmpty();
		super.getResponse().addGlobal("hasPassengers", hasPassengers);

		// ✅ Enviar todo al frontend
		super.getResponse().addData(dataset);
	}
}
