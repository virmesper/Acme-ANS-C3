
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S2.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("SELECT DISTINCT p FROM Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("SELECT DISTINCT p FROM Passenger p WHERE p.customer.id = :customerId")
	Collection<Passenger> findAllByCustomer(int customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

	@Query("SELECT p FROM Passenger p WHERE p.id = :passengerId")
	Passenger findById(int passengerId);

	@Query("SELECT DISTINCT pb.passenger FROM PassengerBooking pb WHERE pb.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("SELECT count(pb) FROM PassengerBooking pb WHERE pb.booking.id = :bookingId")
	Integer countNumberOfPassengersOfBooking(int bookingId);
}
