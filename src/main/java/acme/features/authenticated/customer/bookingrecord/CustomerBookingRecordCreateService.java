
package acme.features.authenticated.customer.bookingrecord;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.BookingRecord;
import acme.entities.student2.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository customerBookingRecordRepository;


	@Override
	public void authorise() {
		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		final int activeCustomerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (authorised && super.getRequest().hasData("bookingId"))
			try {
				int bookingId = super.getRequest().getData("bookingId", int.class);
				Booking booking = this.customerBookingRecordRepository.findBookingById(bookingId);
				authorised = booking != null && booking.getCustomer().getId() == activeCustomerId; // <- sin draftMode aquí
			} catch (Throwable e) {
				authorised = false;
			}

		// Si viene "booking" desde el form (POST)
		if (authorised && super.getRequest().hasData("booking"))
			try {
				int bookingIdFromForm = super.getRequest().getData("booking", int.class);
				Booking bookingFromForm = this.customerBookingRecordRepository.findBookingById(bookingIdFromForm);
				authorised = bookingFromForm != null && bookingFromForm.getCustomer().getId() == activeCustomerId; // <- sin draftMode aquí
			} catch (Throwable e) {
				authorised = false;
			}

		// Si viene "passenger" desde el form (POST) — leer Passenger por id directo
		if (authorised && super.getRequest().hasData("passenger"))
			try {
				int passengerId = super.getRequest().getData("passenger", int.class);
				Passenger passenger = this.customerBookingRecordRepository.findPassengerById(passengerId);
				authorised = passenger != null && passenger.getCustomer().getId() == activeCustomerId;
			} catch (Throwable e) {
				authorised = false;
			}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		final int activeCustomerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Passenger passenger = bookingRecord.getPassenger();
		Booking booking = bookingRecord.getBooking();

		super.state(passenger != null, "passenger", "customer.bookingRecord.form.error.passenger-required");
		super.state(booking != null, "booking", "customer.bookingRecord.form.error.booking-required");

		if (passenger != null)
			super.state(passenger.getCustomer().getId() == activeCustomerId, "passenger", "customer.bookingRecord.form.error.passenger-not-owned");

		if (booking != null) {
			super.state(booking.getCustomer().getId() == activeCustomerId, "booking", "customer.bookingRecord.form.error.booking-not-owned");
			super.state(!booking.isDraftMode(), "booking", "customer.bookingRecord.form.error.booking-published");
		}

		if (passenger != null && booking != null) {
			boolean exists = this.customerBookingRecordRepository.existsByBookingIdAndPassengerId(booking.getId(), passenger.getId());
			super.state(!exists, "passenger", "customer.bookingRecord.form.error.alreadyCreated");
		}
	}

	@Override
	public void load() {
		BookingRecord bookingRecord = new BookingRecord();
		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		super.bindObject(bookingRecord, "passenger", "booking");
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.customerBookingRecordRepository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Integer bookingId = super.getRequest().getData("bookingId", int.class);

		Booking booking = this.customerBookingRecordRepository.findBookingById(bookingId);
		Collection<Passenger> availablePassengers = this.customerBookingRecordRepository.getAvailablePassengersByCustomerAndBooking(customerId, bookingId);

		SelectChoices passengerChoices = SelectChoices.from(availablePassengers, "fullName", bookingRecord.getPassenger());
		SelectChoices bookingChoices = SelectChoices.from(List.of(booking), "locatorCode", bookingRecord.getBooking());

		Dataset dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		dataset.put("passengers", passengerChoices);
		dataset.put("bookings", bookingChoices);
		dataset.put("bookingId", bookingId);

		super.getResponse().addData(dataset);
	}
}
