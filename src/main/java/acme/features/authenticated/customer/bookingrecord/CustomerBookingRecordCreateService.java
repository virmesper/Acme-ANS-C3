
package acme.features.authenticated.customer.bookingrecord;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.BookingRecord;
import acme.entities.student2.Passenger;
import acme.features.authenticated.customer.booking.CustomerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository	customerBookingRecordRepository;
	@Autowired
	private CustomerBookingRepository		bookingRepository;


	@Override
	public void authorise() {
		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		final int activeCustomerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		// 0) bookingId obligatorio en la query y debe ser válido/propietario/no draft
		String rawBookingId = null;
		Booking bookingFromQuery = null;
		if (authorised) {
			try {
				rawBookingId = super.getRequest().getData("bookingId", String.class);
			} catch (Throwable ignored) {
			}

			if (rawBookingId == null || rawBookingId.isBlank() || !rawBookingId.trim().chars().allMatch(Character::isDigit))
				authorised = false;
			else {
				final int bookingId = Integer.parseInt(rawBookingId.trim());
				bookingFromQuery = this.customerBookingRecordRepository.findBookingById(bookingId);
				authorised = bookingFromQuery != null && bookingFromQuery.getCustomer().getId() == activeCustomerId && !bookingFromQuery.isDraftMode();
			}
		}

		// 1) Anti-F12 solo en POST
		if (authorised && "POST".equalsIgnoreCase(super.getRequest().getMethod())) {

			// 1.a) SELECT "booking": si viene con valor real, debe coincidir con el bookingId de la query
			String rawBooking = null;
			try {
				rawBooking = super.getRequest().getData("booking", String.class);
			} catch (Throwable ignored) {
			}

			if (rawBooking != null && !rawBooking.isBlank() && !"0".equals(rawBooking.trim())) {
				final String t = rawBooking.trim();
				if (!t.chars().allMatch(Character::isDigit))
					authorised = false; // manipulado
				else {
					final int bookingIdFromForm = Integer.parseInt(t);
					// Debe coincidir con la booking de la query
					authorised = authorised && bookingFromQuery != null && bookingIdFromForm == bookingFromQuery.getId() && bookingFromQuery.getCustomer().getId() == activeCustomerId && !bookingFromQuery.isDraftMode();
				}
			}

			// 1.b) SELECT "passenger": si viene con valor real, debe ser tuyo y estar disponible para esa booking
			if (authorised) {
				String rawPassenger = null;
				try {
					rawPassenger = super.getRequest().getData("passenger", String.class);
				} catch (Throwable ignored) {
				}

				if (rawPassenger != null && !rawPassenger.isBlank() && !"0".equals(rawPassenger.trim())) {
					final String t = rawPassenger.trim();
					if (!t.chars().allMatch(Character::isDigit))
						authorised = false; // manipulado
					else {
						final int passengerId = Integer.parseInt(t);

						// Debe existir y ser del customer
						final Passenger p = this.customerBookingRecordRepository.findPassengerById(passengerId);
						authorised = p != null && p.getCustomer().getId() == activeCustomerId;

						// Además, debe estar en la lista de "disponibles" para la booking de la query
						if (authorised && bookingFromQuery != null) {
							final Collection<Passenger> avail = this.customerBookingRecordRepository.getAvailablePassengersByCustomerAndBooking(activeCustomerId, bookingFromQuery.getId());
							final boolean inList = avail.stream().anyMatch(x -> x.getId() == passengerId);
							authorised = inList; // si no está disponible para esa booking => manipuló el value
						}
					}
				}
			}
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

	private void recalcAndUpdateBookingPrice(final int bookingId) {
		final Booking booking = this.bookingRepository.findBookingById(bookingId);
		if (booking == null)
			return;

		final long pax = this.customerBookingRecordRepository.countPassengersInBooking(bookingId);

		// Coste por asiento desde el propio Flight de la booking
		final Money seatCost = booking.getFlightId().getCost();

		final Money total = new Money();
		total.setCurrency(seatCost.getCurrency());
		total.setAmount(seatCost.getAmount() * pax);

		booking.setPrice(total);
		this.bookingRepository.save(booking);
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.customerBookingRecordRepository.save(bookingRecord);

		// Recalcular el precio total de la reserva
		final int bookingId = bookingRecord.getBooking().getId();
		this.recalcAndUpdateBookingPrice(bookingId);
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
