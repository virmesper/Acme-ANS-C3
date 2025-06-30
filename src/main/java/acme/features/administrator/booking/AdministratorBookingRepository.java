
package acme.features.administrator.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Flight;
import acme.entities.student2.Booking;
import acme.entities.student2.Passenger;

@Repository
public interface AdministratorBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select b from Booking b where b.draftMode = false")
	Collection<Booking> findPublishedBookings();

	@Query("select br.passenger from BookingRecord br where br.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

}
