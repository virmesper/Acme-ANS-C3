
package acme.features.authenticated.customer.bookingRecord;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S2.BookingRecord;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.id = :bookingId AND br.passenger.id = :passengerId")
	BookingRecord findBookingRecordById(@Param("bookingId") Integer bookingId, @Param("passengerId") Integer passengerId);
}
