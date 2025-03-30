
package acme.features.flightcrewmember.flightassignments;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query("""
		    select fa
		    from FlightAssignment fa
		    where fa.flightCrewMember.id = :id
		      and fa.leg.scheduledArrival >= CURRENT_TIMESTAMP
		""")
	Collection<FlightAssignment> findPlannedAssignmentsByCrewMemberId(@Param("id") int id);

	@Query("""
		    select fa
		    from FlightAssignment fa
		    where fa.flightCrewMember.id = :id
		      and fa.leg.scheduledArrival < CURRENT_TIMESTAMP
		""")
	Collection<FlightAssignment> findCompletedAssignmentsByCrewMemberId(@Param("id") int id);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :assignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int assignmentId);

	@Query("select a.flightCrewMember from ActivityLog a where a.flightAssignment.id = :id")
	Collection<FlightCrewMember> findCrewMembersByFlightAssignmentId(@Param("id") int id);

	@Query("""
		    select count(fa)
		    from FlightAssignment fa
		    where fa.id = :assignmentId
		      and fa.flightCrewDuty = acme.entities.S3.DutyRole.PILOT
		""")
	int countPilotsByFlightAssignmentId(@Param("assignmentId") int assignmentId);

	@Query("""
		    select count(fa)
		    from FlightAssignment fa
		    where fa.id = :assignmentId
		      and fa.flightCrewDuty = acme.entities.S3.DutyRole.CO_PILOT
		""")
	int countCopilotsByFlightAssignmentId(@Param("assignmentId") int assignmentId);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id and fcm.availabilityStatus = acme.entities.S3.AvailabilityStatus.AVAILABLE")
	FlightCrewMember findAvailableCrewMemberById(@Param("id") int id);

	@Query("""
		    select case when count(fa) > 0 then true else false end
		    from FlightAssignment fa
		    where fa.id = :assignmentId and fa.assignmentStatus = acme.entities.S3.AssignmentStatus.CONFIRMED
		""")
	boolean isFlightAssignmentPublished(@Param("assignmentId") int assignmentId);

	@Query("select fa from FlightAssignment fa where fa.leg.id = :id")
	Leg findLegById(int id);
}
