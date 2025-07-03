
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

		if (authorised && super.getRequest().hasData("bookingId"))
			try {
				int bookingId = super.getRequest().getData("bookingId", int.class);
				Booking booking = this.customerBookingRecordRepository.findBookingById(bookingId);
				authorised = booking != null && booking.getCustomer().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
			} catch (final Throwable e) {
				authorised = false;
			}

		if (authorised && super.getRequest().hasData("booking"))
			try {
				int bookingIdFromForm = super.getRequest().getData("booking", int.class);
				Booking bookingFromForm = this.customerBookingRecordRepository.findBookingById(bookingIdFromForm);
				authorised = bookingFromForm != null && bookingFromForm.getCustomer().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
			} catch (final Throwable e) {
				authorised = false;
			}

		if (authorised && super.getRequest().hasData("passenger"))
			try {
				int passengerId = super.getRequest().getData("passenger", int.class);
				Passenger passenger = this.customerBookingRecordRepository.getPassengerFromBookingRecord(passengerId);
				authorised = passenger != null && passenger.getCustomer().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
			} catch (final Throwable e) {
				authorised = false;
			}

		super.getResponse().setAuthorised(authorised);

		if (!authorised)
			throw new AssertionError("Access is not authorised");
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
	public void validate(final BookingRecord bookingRecord) {
		Passenger passenger = bookingRecord.getPassenger();
		Booking booking = bookingRecord.getBooking();

		BookingRecord bookingRecordCompare = null;
		if (passenger != null && booking != null)
			bookingRecordCompare = this.customerBookingRecordRepository.getBookingRecordByPassengerIdAndBookingId(passenger.getId(), booking.getId());

		boolean status1 = bookingRecordCompare == null || bookingRecordCompare.getId() == bookingRecord.getId();
		super.state(status1, "*", "customer.bookingRecord.form.error.alreadyCreated");
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
