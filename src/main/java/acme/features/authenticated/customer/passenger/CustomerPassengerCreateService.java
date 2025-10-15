
package acme.features.authenticated.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.datatypes.Money;
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

		// Si llega bookingId (puede venir vacío), comprueba propiedad solo si tiene valor
		if (authorised && super.getRequest().hasData(CustomerPassengerCreateService.BOOKING_ID))
			try {
				final Integer bookingId = super.getRequest().getData(CustomerPassengerCreateService.BOOKING_ID, Integer.class);
				if (bookingId != null) {
					final Booking booking = this.bookingRepository.findBookingById(bookingId);
					final int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
					authorised = booking != null && booking.getCustomer().getId() == customerId;
				}
			} catch (final Throwable ignored) {
				// No denegar por formato inválido; se validará después
			}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		final AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		final int customerId = principal.getId();
		final Customer customer = this.repository.findCustomerById(customerId);

		final Passenger passenger = new Passenger();
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
		// Validaciones adicionales si necesitas
	}

	private void recalcAndUpdateBookingPrice(final int bookingId) {
		final Booking booking = this.bookingRepository.findBookingById(bookingId);
		if (booking == null)
			return;

		// nº de pasajeros asociados a la reserva
		final long pax = this.bookingRecordRepository.countPassengersInBooking(bookingId);

		// coste por asiento del vuelo
		final Money seatCost = booking.getFlightId().getCost();

		final Money total = new Money();
		total.setCurrency(seatCost.getCurrency());
		total.setAmount(seatCost.getAmount() * pax);

		booking.setPrice(total);
		this.bookingRepository.save(booking);
	}

	@Override
	public void perform(final Passenger passenger) {
		// 1) Guardar Passenger
		this.repository.save(passenger);

		// 2) Si venimos desde una booking, crear el enlace BookingRecord
		if (super.getRequest().hasData(CustomerPassengerCreateService.BOOKING_ID)) {
			final Integer bookingId = super.getRequest().getData(CustomerPassengerCreateService.BOOKING_ID, Integer.class);
			if (bookingId != null) {
				final Booking booking = this.bookingRepository.findBookingById(bookingId);
				if (booking != null) {
					final BookingRecord record = new BookingRecord();
					record.setBooking(booking);
					record.setPassenger(passenger);
					this.bookingRecordRepository.save(record);

					// >>> Recalcular precio total de la booking <<<
					this.recalcAndUpdateBookingPrice(booking.getId());

					super.getResponse().addGlobal(CustomerPassengerCreateService.BOOKING_ID, bookingId);
				}
			}
		}

	}

	@Override
	public void unbind(final Passenger passenger) {
		final Dataset dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "draftMode", "specialNeeds");

		// Mantener bookingId entre GET (form) y POST (create) solo si tiene valor
		if (super.getRequest().hasData(CustomerPassengerCreateService.BOOKING_ID)) {
			final Integer bookingId = super.getRequest().getData(CustomerPassengerCreateService.BOOKING_ID, Integer.class);
			if (bookingId != null)
				dataset.put(CustomerPassengerCreateService.BOOKING_ID, bookingId);
		}

		super.getResponse().addData(dataset);
	}
}
