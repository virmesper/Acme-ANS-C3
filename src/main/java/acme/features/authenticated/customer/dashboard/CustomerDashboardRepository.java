
package acme.features.authenticated.customer.dashboard;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student2.Booking;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	// Trae bookings del customer ordenadas por compra (las usaremos para las 5 últimas)
	@Query("""
			select b
			from Booking b
			where b.customer.id = :customerId
			order by b.purchaseMoment desc
		""")
	Collection<Booking> findBookingsOrderedByPurchaseDesc(int customerId);

	// Suma del dinero gastado desde fecha
	@Query("""
			select coalesce(sum(b.price.amount), 0.0)
			from Booking b
			where b.customer.id = :customerId
			  and b.purchaseMoment >= :fromDate
		""")
	Double sumMoneySpentSince(int customerId, Date fromDate);

	// Nº de bookings por travel class
	@Query("""
		  select b.travelClass, count(b)
		  from Booking b
		  where b.customer.id = :customerId
		  group by b.travelClass
		""")
	Collection<Object[]> countBookingsByTravelClass(int customerId);

	// Importes de bookings desde fecha (para stats en Java)
	@Query("""
			select b.price.amount
			from Booking b
			where b.customer.id = :customerId
			  and b.purchaseMoment >= :fromDate
		""")
	Collection<Double> findBookingAmountsSince(int customerId, Date fromDate);

	// Nº de pasajeros por booking (cuenta por booking)
	@Query("""
			select count(br)
			from acme.entities.student2.BookingRecord br
			where br.booking.customer.id = :customerId
			group by br.booking.id
		""")
	Collection<Long> passengersPerBookingCounts(int customerId);
}
