
package acme.features.flightcrewmember.flightassignments;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Leg;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@Repository
public interface FlightCrewMemberFlightAssignmentsRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> findFlightAssignmentsByCrewMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :id and fa.completed = false")
	Collection<FlightAssignment> findPlannedAssignmentsByCrewMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :id and fa.completed = true")
	Collection<FlightAssignment> findCompletedAssignmentsByCrewMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :assignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int assignmentId);

	@Query("select fcm from FlightCrewMember fcm where fcm.flightAssignment.id = :assignmentId")
	Collection<FlightCrewMember> findCrewMembersByFlightAssignmentId(int assignmentId);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id and fcm.role = 'LEAD_ATTENDANT'")
	FlightCrewMember findLeadAttendantById(int id);

	@Query("select count(fcm) from FlightCrewMember fcm where fcm.flightAssignment.id = :assignmentId and fcm.role = 'PILOT'")
	int countPilotsByFlightAssignmentId(int assignmentId);

	@Query("select count(fcm) from FlightCrewMember fcm where fcm.flightAssignment.id = :assignmentId and fcm.role = 'COPILOT'")
	int countCopilotsByFlightAssignmentId(int assignmentId);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id and fcm.status = 'AVAILABLE'")
	FlightCrewMember findAvailableCrewMemberById(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :assignmentId and fa.published = true")
	boolean isFlightAssignmentPublished(int assignmentId);

	@Query("select fa from FlightAssignment fa where fa.leg.id = :id")
	Leg findLegById(int id);
}
