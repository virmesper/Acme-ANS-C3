
package acme.features.any.flightAssignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S3.FlightAssignment;

@Repository
public interface AnyFlightAssignmentRepository extends AbstractRepository {

	@Query("""
			SELECT a
			FROM FlightAssignment a
			WHERE a.draftMode = false
			  AND a.leg.draftMode = false
		""")
	Collection<FlightAssignment> findAllPublishedAssignments();

	@Query("SELECT a FROM FlightAssignment a WHERE a.id = :id")
	FlightAssignment findAssignmentById(int id);

}
