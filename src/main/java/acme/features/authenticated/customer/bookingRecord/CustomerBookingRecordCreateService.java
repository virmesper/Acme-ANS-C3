
package acme.features.authenticated.customer.bookingRecord;

import java.util.Collection;

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
		int BookingId;
		Booking booking;

		int PassengerId;
		Passenger passenger;

		BookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.bookingRepository.findBookingById(BookingId);

		PassengerId = super.getRequest().getData("passenger", int.class);
		passenger = this.passengerRepository.findById(PassengerId);

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
	public void unbind(final BookingRecord bookingPassenger) {
		assert bookingPassenger != null;

		Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Integer bookingId = super.getRequest().getData("bookingId", int.class);

		// Obtener los pasajeros ya añadidos a la reserva
		Collection<Passenger> alreadyAddedPassengers = this.customerBookingPassengerRepository.getPassengersInBooking(bookingId);
		// Obtener todos los pasajeros del cliente
		Collection<Passenger> allPassengers = this.customerBookingPassengerRepository.getAllPassengersByCustomerId(customerId);

		// Filtrar los pasajeros que aún no están en la reserva
		// Filtrar los pasajeros ya asignados de manera correcta
		Collection<Passenger> noAddedPassengers = allPassengers.stream().filter(p -> assignedPassengers.stream().noneMatch(ap -> ap.getId() == p.getId())).toList();

		// Depuración: Mostrar en consola los pasajeros disponibles
		System.out.println("🚀 Pasajeros no añadidos: " + noAddedPassengers);

		// Crear las opciones de selección
		SelectChoices passengerChoices = SelectChoices.from(noAddedPassengers, "fullName", bookingPassenger.getPassenger());

		Dataset dataset = super.unbindObject(bookingPassenger, "passenger", "booking");
		dataset.put("passengers", passengerChoices);

		super.getResponse().addData(dataset);
	}

}
