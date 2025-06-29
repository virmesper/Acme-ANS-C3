
package acme.features.flight_crew_member.activity_log;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student3.ActivityLog;
import acme.entities.student3.FlightAssignment;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select al.flightAssignment from ActivityLog al where al.id = :id")
	FlightAssignment findFlightAssignmentByActivityLogId(int id);

	@Query("select case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id and al.flightAssignment.draftMode = false")
	boolean isFlightAssignmentAlreadyPublishedByActivityLogId(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.draftMode = false")
	boolean isFlightAssignmentAlreadyPublishedById(int id);

	@Query("select al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :masterId")
	Collection<ActivityLog> findActivityLogsByMasterId(int masterId);

	@Query("select count(al) > 0 from ActivityLog al where al.id = :activityLogId and al.flightAssignment.flightCrewMember.id = :flightCrewMemberId")
	boolean thatActivityLogIsOf(int activityLogId, int flightCrewMemberId);
	@Query("SELECT CASE WHEN COUNT(fcm) > 0 THEN true ELSE false END FROM FlightCrewMember fcm WHERE fcm.id = :id")
	boolean existsFlightCrewMember(int id);

	@Query("select case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id and al.flightAssignment.leg.scheduledArrival < :currentMoment")
	boolean associatedWithCompletedLeg(int id, Date currentMoment);

	@Query("select case when count(fa) > 0 then true else false END FROM FlightAssignment  fa WHERE fa.id = :id AND fa.leg.scheduledArrival < :currentMoment")
	boolean flightAssignmentAssociatedWithCompletedLeg(int id, Date currentMoment);

	@Query("SELECT CASE WHEN COUNT(fa) > 0 THEN true ELSE false END FROM FlightAssignment fa WHERE fa.id = :id")
	boolean existsFlightAssignment(int id);

	@Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM ActivityLog al WHERE al.id = :id")
	boolean existsActivityLog(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.leg.scheduledArrival < :currentMoment and fa.id = :flightAssignmentId")
	boolean isFlightAssignmentCompleted(Date currentMoment, int flightAssignmentId);
}
