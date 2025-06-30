
package acme.features.flight_crew_member.flight_assignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Leg;
import acme.entities.S1.LegStatus;
import acme.entities.student3.ActivityLog;
import acme.entities.student3.AvailabilityStatus;
import acme.entities.student3.Duty;
import acme.entities.student3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.leg.status = :status  and fa.flightCrewMember.id = :flighCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByCompletedLeg(LegStatus status, int flighCrewMemberId);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.leg.status = :status")
	boolean associatedWithCompletedLeg(int id, LegStatus status);

	@Query("select fa from FlightAssignment fa where fa.leg.status != :status and fa.flightCrewMember.id = :flighCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByPlannedLeg(LegStatus status, int flighCrewMemberId);

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

	@Query("select case when count(fa) > 0 then true else false end " + "from FlightAssignment fa " + "where fa.id = :flightAssignmentId " + "and fa.leg.status = :status")
	boolean areLegsCompletedByFlightAssignment(int flightAssignmentId, LegStatus status);

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

	@Query("SELECT l FROM Leg l WHERE l.draftMode = true AND l.id = :legId")
	Leg findPublishedLegById(int legId);

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.leg.id = :legId AND fa.duty = :duty AND fa.id != :id AND fa.draftMode = true")
	int hasDutyAssigned(int legId, Duty duty, int id);

	@Query("select m from FlightCrewMember m")
	Collection<FlightCrewMember> findAllCrewMembers();

	@Query("select fa from FlightAssignment fa")
	Collection<FlightAssignment> findAllFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival < :now and fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> findCompletedFlightAssignmentsByMemberId(Date now, int id);

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledDeparture > :now and fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> findPlannedFlightAssignmentsByMemberId(Date now, int id);

	@Query("select m from FlightCrewMember m where m.id = :memberId")
	FlightCrewMember findMemberById(int memberId);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :id")
	Collection<ActivityLog> findActivityLogsByAssignmentId(int id);

	@Query("select distinct fa.leg from FlightAssignment fa where fa.flightCrewMember.id = :memberId")
	Collection<Leg> findLegsByFlightCrewMemberId(int memberId);

	@Query("select fa from FlightAssignment fa where fa.leg.status = :status and fa.flightCrewMember.id = :flightCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByLegStatus(LegStatus status, int flightCrewMemberId);
}
