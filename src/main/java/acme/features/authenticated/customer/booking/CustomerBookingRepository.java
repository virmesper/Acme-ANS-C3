
package acme.features.authenticated.customer.booking;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student1.Flight;
import acme.entities.student2.Booking;
import acme.entities.student2.BookingRecord;
import acme.entities.student2.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select bk.passenger from BookingRecord bk where bk.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(@Param("bookingId") Integer bookingId);

	@Query("select b from Booking b WHERE b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(@Param("locatorCode") String locatorCode);

	@Query("SELECT DISTINCT b FROM Booking b")
	Collection<Booking> findAllBookings();

	@Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
	Booking findById(int bookingId);

	@Query("SELECT DISTINCT b FROM Booking b WHERE b.customer.id = :customerId")
	Collection<Booking> findAllByCustomer(int customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);

	@Query("SELECT DISTINCT f FROM Flight f")
	Collection<Flight> findAllFlights();

	@Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.locatorCode = :locatorCode")
	boolean existsByLocatorCode(@Param("locatorCode") String locatorCode);

	@Query("select c from Customer c where c.id = :customerId")
	Customer findCustomerById(@Param("customerId") Integer customerId);

	@Query("select b from Booking b WHERE b.id = :bookingId")
	Booking findBookingById(@Param("bookingId") int bookingId);

	@Query("select bk from Booking bk where bk.customer.userAccount.id = :customerId")
	Collection<Booking> findBookingByCustomer(@Param("customerId") Integer customerId);

	@Query("SELECT COUNT(pb) FROM BookingRecord pb WHERE pb.booking.id = :bookingId")
	Integer countNumberOfPassengersOfBooking(@Param("bookingId") int bookingId);

	@Query("SELECT b FROM Booking b")
	List<Booking> findAllBooking();

	@Query("SELECT c FROM Customer c WHERE c.userAccount.id = :userAccountId")
	Customer findCustomerByUserAccountId(Integer userAccountId);

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomer(Integer customerId);

	@Query("SELECT f FROM Flight f WHERE f.id = :flightId")
	Flight findFlightById(Integer flightId);

	@Query("SELECT f FROM Flight f WHERE f.draftMode=false")
	Collection<Flight> findAllPublishedFlights();

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<BookingRecord> findAllBookingRecordsByBookingId(int bookingId);

	@Query("SELECT b FROM Booking b WHERE b.locatorCode = :locatorCode")
	Collection<Booking> findBookingsByLocatorCode(String locatorCode);

}
