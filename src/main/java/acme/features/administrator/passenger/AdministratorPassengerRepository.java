
package acme.features.administrator.passenger;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student2.Ban;
import acme.entities.student2.Booking;
import acme.entities.student2.Passenger;

@Repository
public interface AdministratorPassengerRepository extends AbstractRepository {

	@Query("select bt.passenger from BookingRecord bt where bt.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select bt.booking from BookingRecord bt where bt.passenger.id = :id and bt.booking.draftMode = false")
	Collection<Booking> findPublishedBookingsOfPassenger(int id);

	@Query("select distinct(b.passenger) from Ban b where b.liftDate is null or b.liftDate > :today")
	Collection<Passenger> findBannedPassengers(Date today);

	@Query("select distinct(b.passenger) from Ban b")
	Collection<Passenger> findPassengersEverBanned();

	@Query("select distinct(b.passenger) from Ban b where b.banIssuedDate >= :lastMonth")
	Collection<Passenger> findPassengersBannedLastMonth(Date lastMonth);

	@Query("select b.nationality from Ban b where b.passenger.id = :id order by b.banIssuedDate")
	List<String> findNationality(int id);

	@Query("select b from Ban b where b.passenger.id = :id")
	List<Ban> findBanOfPassenger(int id);

}
