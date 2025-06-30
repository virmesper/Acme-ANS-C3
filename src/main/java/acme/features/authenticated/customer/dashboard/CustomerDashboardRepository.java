
package acme.features.authenticated.customer.dashboard;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student2.Booking;
import acme.entities.student2.Passenger;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :id AND b.draftMode = false")
	Collection<Booking> findAllBookings(int id);

	@Query("SELECT SUM(b.price.amount) FROM Booking b WHERE b.purchaseMoment > :lastYear AND b.customer.id = :id AND b.draftMode = false")
	Double moneySpentLastYear(Date lastYear, int id);

	@Query("select bt.passenger from BookingRecord bt where bt.booking.id = :id AND bt.booking.draftMode = false")
	Collection<Passenger> findPassengersByBookingId(int id);

}
