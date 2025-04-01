
package acme.features.authenticated.management;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Flight;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select a from Manager a where a.idNumber = :id")
	Flight findFlightByAirlineManagerId(int id);
}
