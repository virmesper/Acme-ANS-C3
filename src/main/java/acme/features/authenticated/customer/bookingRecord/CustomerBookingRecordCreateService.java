
package acme.features.authenticated.customer.bookingRecord;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S2.Booking;
import acme.entities.S2.BookingRecord;
import acme.entities.S2.Passenger;
import acme.features.authenticated.customer.booking.CustomerBookingRepository;
import acme.features.authenticated.customer.passenger.CustomerPassengerRepository;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository	customerBookingPassengerRepository;

	@Autowired
	private CustomerBookingRepository		bookingRepository;

	@Autowired
	private CustomerPassengerRepository		passengerRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);
	}

	@Override
	public void load() {
		BookingRecord booking;

		booking = new BookingRecord();

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		Integer bookingId;
		Integer passengerId;
		Booking booking;
		Passenger passenger;

		// Los nombres deben coincidir con los campos del formulario: booking y passenger
		bookingId = super.getRequest().getData("booking", Integer.class);
		passengerId = super.getRequest().getData("passenger", Integer.class);

		// Verificar que los valores obtenidos no sean nulos
		System.out.println("Booking ID recibido: " + bookingId);
		System.out.println("Passenger ID recibido: " + passengerId);

		// Recuperar las entidades desde la base de datos
		booking = this.bookingRepository.findBookingById(bookingId);
		passenger = this.passengerRepository.findPassengerById(passengerId);

		// Comprobación adicional para asegurar que no sean nulos
		if (booking == null)
			System.out.println("Error: No se encontró la reserva con ID: " + bookingId);
		if (passenger == null)
			System.out.println("Error: No se encontró el pasajero con ID: " + passengerId);

		// Realizar la vinculación habitual
		super.bindObject(bookingRecord);
		bookingRecord.setBooking(booking);
		bookingRecord.setPassenger(passenger);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {

		if (bookingRecord.getBooking() == null && bookingRecord.getPassenger() == null) {
			super.state(false, "booking", "acme.validation.confirmation.message.booking-record.create.booking");
			super.state(false, "passenger", "acme.validation.confirmation.message.booking-record.create.passenger");

		} else if (bookingRecord.getPassenger() == null)
			super.state(false, "passenger", "acme.validation.confirmation.message.booking-record.create.passenger");
		else if (bookingRecord.getBooking() == null)
			super.state(false, "booking", "acme.validation.confirmation.message.booking-record.create.booking");
		else {
			BookingRecord br = this.customerBookingPassengerRepository.findBookingRecordBybookingIdpassengerId(bookingRecord.getBooking().getId(), bookingRecord.getPassenger().getId());
			if (br != null)
				super.state(false, "*", "acme.validation.confirmation.message.booking-record.create");
		}

	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.customerBookingPassengerRepository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;
		SelectChoices passengerChoices;
		SelectChoices bookingChoices;

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.bookingRepository.findBookingById(bookingId);

		// Crear una lista con la única booking encontrada
		Collection<Booking> singleBookingList = List.of(booking);

		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		// Obtener todos los pasajeros del cliente
		Collection<Passenger> allPassengers = this.passengerRepository.findPassengerByCustomer(customerId);
		// Obtener los pasajeros ya asignados a la reserva
		Collection<Passenger> assignedPassengers = this.customerBookingPassengerRepository.findPassengersByBookingId(bookingId);

		// Log para depuración
		System.out.println("Total pasajeros del cliente: " + allPassengers.size());
		System.out.println("Total pasajeros asignados: " + assignedPassengers.size());

		// Filtrar los pasajeros disponibles (no asignados a la reserva actual)
		Collection<Passenger> availablePassengers = allPassengers.stream().filter(passenger -> assignedPassengers.stream().noneMatch(assigned -> assigned.getId() == passenger.getId())).toList();

		// Log para verificar los pasajeros disponibles
		System.out.println("Pasajeros disponibles: " + availablePassengers.size());

		// Crear las opciones de selección
		bookingChoices = SelectChoices.from(singleBookingList, "locatorCode", bookingRecord.getBooking());
		passengerChoices = SelectChoices.from(availablePassengers, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord);
		dataset.put("booking", bookingChoices.getSelected().getKey());
		dataset.put("bookings", bookingChoices);
		dataset.put("passenger", passengerChoices.getSelected().getKey());
		dataset.put("passengers", passengerChoices);

		super.getResponse().addData(dataset);
	}

}
