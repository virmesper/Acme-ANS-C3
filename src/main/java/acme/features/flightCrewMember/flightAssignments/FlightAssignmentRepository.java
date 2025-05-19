
package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Leg;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.AvailabilityStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival < :currentMoment and fa.flightCrewMember.id = :flighCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByCompletedLeg(Date currentMoment, int flighCrewMemberId);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.leg.scheduledArrival < :currentMoment")
	boolean associatedWithCompletedLeg(int id, Date currentMoment);

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival >= :currentMoment and fa.flightCrewMember.id = :flighCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByPlannedLeg(Date currentMoment, int flighCrewMemberId);

	@Query("select fa.leg from FlightAssignment fa where fa.id = :id")
	Collection<Leg> findLegsByFlightAssignmentId(int id);

	@Query("select l from Leg l where l.id  = :legId")
	Leg findLegById(int legId);

	@Query("select fa.flightCrewMember from FlightAssignment fa where fa.id = :id")
	Collection<FlightCrewMember> findFlightCrewMembersByFlightAssignmentId(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select fa.leg from FlightAssignment fa where fa.flightCrewMember.id = :id")
	Collection<Leg> findLegsByFlightCrewMember(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.availabilityStatus = :availabilityStatus")
	Collection<FlightCrewMember> findFlightCrewMembersByAvailability(AvailabilityStatus availabilityStatus);

	@Query("select fa.flightCrewMember from FlightAssignment fa where fa.leg.id = :legId")
	Collection<FlightCrewMember> findFlightCrewMembersAssignedToLeg(int legId);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int flightAssignmentId);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.leg.id = :legId and fa.duty = :duty")
	boolean existsFlightCrewMemberWithDutyInLeg(int legId, Duty duty);

	@Query("select fa from FlightAssignment fa where fa.leg.id=:id")
	Collection<FlightAssignment> findFlightAssignmentByLegId(int id);

	@Query("select case when count(fa) > 0 then true else false end " + "from FlightAssignment fa " + "where fa.id = :flightAssignmentId " + "and fa.leg.scheduledArrival < :currentMoment")
	boolean areLegsCompletedByFlightAssignment(int flightAssignmentId, Date currentMoment);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.id = :flightAssignmentId and fa.flightCrewMember.id = :flightCrewMemberId")
	boolean thatFlightAssignmentIsOf(int flightAssignmentId, int flightCrewMemberId);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT CASE WHEN COUNT(fcm) > 0 THEN true ELSE false END FROM FlightCrewMember fcm WHERE fcm.id = :id")
	boolean existsFlightCrewMember(int id);

	@Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leg l WHERE l.id = :id")
	boolean existsLeg(int id);

	@Query("SELECT CASE WHEN COUNT(fa) > 0 THEN true ELSE false END FROM FlightAssignment fa WHERE fa.id = :id")
	boolean existsFlightAssignment(int id);
}
