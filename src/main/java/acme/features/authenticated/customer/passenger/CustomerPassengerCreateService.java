
package acme.features.authenticated.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.BookingRecord;
import acme.entities.student2.Passenger;
import acme.features.authenticated.customer.booking.CustomerBookingRepository;
import acme.features.authenticated.customer.bookingrecord.CustomerBookingRecordRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	// Constants ---------------------------------------------------------------
	private static final String				BOOKING_ID	= "bookingId";

	// Repositories ------------------------------------------------------------
	@Autowired
	private CustomerPassengerRepository		repository;              // Passenger
	@Autowired
	private CustomerBookingRepository		bookingRepository;       // Booking
	@Autowired
	private CustomerBookingRecordRepository	bookingRecordRepository; // BookingRecord

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		// Si viene bookingId, verifica que la booking existe y pertenece al customer logueado
		if (authorised && super.getRequest().hasData(CustomerPassengerCreateService.BOOKING_ID))
			try {
				final int bookingId = super.getRequest().getData(CustomerPassengerCreateService.BOOKING_ID, int.class);
				final Booking booking = this.bookingRepository.findBookingById(bookingId);
				final int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				authorised = booking != null && booking.getCustomer().getId() == customerId;
			} catch (final Throwable ignored) {
				// No denegar por datos malformados; se validará después
			}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Passenger passenger;

		final AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		final int customerId = principal.getId();
		final Customer customer = this.repository.findCustomerById(customerId);

		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setDraftMode(false);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "draftMode", "specialNeeds");
		passenger.setDraftMode(false);
	}

	@Override
	public void validate(final Passenger passenger) {
		// Reglas extra si las necesitas; las anotaciones ya cubren lo básico
	}

	@Override
	public void perform(final Passenger passenger) {
		// 1) Guardar el Passenger (JPA le asigna id)
		this.repository.save(passenger);

		// 2) Si venimos del listado de una booking, crear el BookingRecord
		if (super.getRequest().hasData(CustomerPassengerCreateService.BOOKING_ID)) {
			final int bookingId = super.getRequest().getData(CustomerPassengerCreateService.BOOKING_ID, int.class);
			final Booking booking = this.bookingRepository.findBookingById(bookingId);

			if (booking != null) {
				// Evitar duplicados (por si se reenvía el POST)
				final boolean alreadyLinked = this.bookingRecordRepository.existsByBookingIdAndPassengerId(bookingId, passenger.getId());

				if (!alreadyLinked) {
					final BookingRecord record = new BookingRecord();
					record.setBooking(booking);
					record.setPassenger(passenger);
					this.bookingRecordRepository.save(record);
				}

				// Mantener bookingId para la vuelta al listado
				super.getResponse().addGlobal(CustomerPassengerCreateService.BOOKING_ID, bookingId);
			}
		}
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "draftMode", "specialNeeds");

		// MUY IMPORTANTE: mantener bookingId entre GET (form) y POST (create)
		if (super.getRequest().hasData(CustomerPassengerCreateService.BOOKING_ID))
			dataset.put(CustomerPassengerCreateService.BOOKING_ID, super.getRequest().getData(CustomerPassengerCreateService.BOOKING_ID, int.class));

		super.getResponse().addData(dataset);
	}
}
