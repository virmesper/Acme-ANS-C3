
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student2.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("SELECT p FROM Passenger p WHERE p.id = :passengerId")
	Passenger findPassengerById(@Param("passengerId") Integer passengerId);

	@Query("SELECT DISTINCT p FROM Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select p from Passenger p where p.customer.userAccount.id = :customerId")
	Collection<Passenger> findPassengerByCustomer(@Param("customerId") Integer customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

	@Query("SELECT p FROM Passenger p WHERE p.id = :passengerId")
	Passenger findById(int passengerId);

	@Query("SELECT DISTINCT pb.passenger FROM BookingRecord pb WHERE pb.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("SELECT count(pb) FROM BookingRecord pb WHERE pb.booking.id = :bookingId")
	Integer countNumberOfPassengersOfBooking(int bookingId);

	@Query("select c from Customer c where c.id = :customerId")
	Customer findCustomerById(@Param("customerId") Integer customerId);

}
