
package acme.realms.flightCrewMember;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightCrewMemberRepository extends AbstractRepository {

	@Query("select a from FlightCrewMember a where a.employeeCode = :employeeCode")
	List<FlightCrewMember> findByEmployeeCode(String employeeCode);

}
