
package acme.features.authenticated.customer.bookingrecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student2.Booking;
import acme.entities.student2.BookingRecord;
import acme.entities.student2.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.id=:bookingId")
	Booking getBookingById(int bookingId);

	@Query("SELECT p FROM Passenger p WHERE p.customer.id=:customerId")
	Collection<Passenger> getAllPassengersByCustomer(int customerId);

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id=:bookingId")
	Collection<Passenger> getPassengersInBooking(int bookingId);

	@Query("SELECT b FROM Booking b WHERE b.customer.id=:customerId and b.draftMode=false")
	Collection<Booking> getBookingsByCustomerId(int customerId);

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.customer.id=:customerId")
	Collection<BookingRecord> getBookingRecordsByCustomerId(int customerId);

	@Query("SELECT br FROM BookingRecord br WHERE br.id=:bookingRecordId")
	BookingRecord getBookingRecordByBookingRecordId(int bookingRecordId);

	@Query("SELECT br.booking FROM BookingRecord br WHERE br.booking.id=:bookingId")
	Booking getBookingFromBookingRecord(int bookingId);

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.passenger.id=:passengerId")
	Passenger getPassengerFromBookingRecord(int passengerId);

	@Query("SELECT br FROM BookingRecord br WHERE br.passenger.id = :passengerId and br.booking.id = :bookingId")
	BookingRecord getBookingRecordByPassengerIdAndBookingId(int passengerId, int bookingId);

	@Query("SELECT b from Booking b WHERE b.id = :id")
	Booking findBookingById(int id);

	@Query("SELECT p FROM Passenger p WHERE p.customer.id = :customerId AND p.id NOT IN (SELECT br.passenger.id FROM BookingRecord br WHERE br.booking.id = :bookingId)")
	Collection<Passenger> getAvailablePassengersByCustomerAndBooking(int customerId, int bookingId);

}
