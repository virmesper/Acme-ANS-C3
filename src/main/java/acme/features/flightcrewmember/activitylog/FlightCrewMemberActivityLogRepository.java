
package acme.features.flightcrewmember.activitylog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("select a from ActivityLog a where a.flightCrewMember.id = :id")
	Collection<ActivityLog> findActivityLogsByCrewMemberId(@Param("id") int id);

	@Query("select a from ActivityLog a where a.id = :id")
	ActivityLog findOneActivityLogById(int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :crewId and fa.assignmentStatus = acme.entities.S3.AssignmentStatus.CONFIRMED")
	Collection<FlightAssignment> findConfirmedAssignmentsByCrewMemberId(int crewId);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :assignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(@Param("assignmentId") int assignmentId);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> findAssignmentsByCrewMemberId(@Param("id") int id);

}
