
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
	public void unbind(final BookingRecord bookingRecord) {
		Dataset dataset;
		SelectChoices passengerChoices;
		SelectChoices bookingChoices;

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.bookingRepository.findBookingById(bookingId);

		// Crear una lista con la única booking encontrada
		Collection<Booking> singleBookingList = List.of(booking);

		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		Collection<Passenger> allPassengers = this.passengerRepository.findPassengerByCustomer(customerId);
		Collection<Passenger> assignedPassengers = this.customerBookingPassengerRepository.getPassengersInBooking(bookingId);

		// Filtrar los pasajeros ya asignados de manera correcta
		// Filtrar los pasajeros ya asignados de manera correcta
		Collection<Passenger> availablePassengers = allPassengers.stream().filter(passenger -> assignedPassengers.stream().noneMatch(assigned -> assigned.getId() == passenger.getId())).toList();

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
