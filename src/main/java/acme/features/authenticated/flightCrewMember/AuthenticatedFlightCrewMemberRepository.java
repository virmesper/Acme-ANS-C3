
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.group.Airline;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface AuthenticatedFlightCrewMemberRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findOneUserAccountById(int id);

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(int id);

	@Query("SELECT COUNT(fcm) > 0 FROM FlightCrewMember fcm WHERE fcm.employeeCode = :employeeCode")
	boolean existsByEmployeeCode(String employeeCode);

	@Query("SELECT a FROM Airline a")
	Collection<Airline> findAllAirlines();

	@Query("SELECT m FROM FlightCrewMember m WHERE m.userAccount.id = :userAccountId")
	FlightCrewMember findOneFlightCrewMemberByUserAccountId(int userAccountId);

}
