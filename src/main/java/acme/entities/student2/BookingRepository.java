
package acme.entities.student2;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import acme.client.repositories.AbstractRepository;

public interface BookingRepository extends AbstractRepository {

	@Query("select bk.passenger from BookingRecord bk where bk.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(@Param("bookingId") Integer bookingId);

	@Query("SELECT count(b) > 1 FROM Booking b WHERE b.locatorCode = :locatorCode")
	Boolean existsBookingWithLocatorCode(String locatorCode);
}
