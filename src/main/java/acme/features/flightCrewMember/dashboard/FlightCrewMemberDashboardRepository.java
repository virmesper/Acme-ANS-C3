
package acme.features.flightCrewMember.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	@Query("""
		    SELECT DISTINCT a.leg.arrivalAirport.city
		    FROM FlightAssignment a
		    WHERE a.flightCrewMember.id = :id
		          AND a.draftMode = false
		          AND a.currentStatus = 'CONFIRMED'
		          AND a.leg.draftMode = false
		          AND a.leg.status IN ('ON_TIME','DELAYED','LANDED')
		    ORDER BY a.leg.scheduledArrival DESC
		""")
	Page<String> findDestinations(int id, Pageable pageable);

	@Query("""
		    SELECT
		        CASE
		            WHEN l.severityLevel <= 3 THEN '0-3'
		            WHEN l.severityLevel <= 7 THEN '4-7'
		            ELSE '8-10'
		        END AS range,
		        COUNT(l)
		    FROM ActivityLog l
		    WHERE l.flightAssignment.flightCrewMember.id = :id
		          AND l.draftMode = false
		          AND l.flightAssignment.draftMode = false
		          AND l.flightAssignment.currentStatus = 'CONFIRMED'
		          AND l.flightAssignment.leg.draftMode = false
		    GROUP BY
		        CASE
		            WHEN l.severityLevel <= 3 THEN '0-3'
		            WHEN l.severityLevel <= 7 THEN '4-7'
		            ELSE '8-10'
		        END
		""")
	List<Object[]> countLogsBySeverityRanges(int id);

	@Query("""
		    SELECT a.leg.id
		    FROM FlightAssignment a
		    WHERE a.flightCrewMember.id = :id
		      AND a.draftMode = false
		      AND a.currentStatus = 'CONFIRMED'
		      AND a.leg.draftMode = false
		      AND a.leg.scheduledArrival < :moment
		    ORDER BY a.leg.scheduledArrival DESC
		""")
	List<Integer> findLastLegIdByCrewMember(int id, Date moment);

	@Query("""
		    SELECT DISTINCT fa.flightCrewMember
		    FROM FlightAssignment fa
		    WHERE fa.leg.id = :legId
		      AND fa.flightCrewMember.id <> :crewMemberId
		      AND fa.draftMode = false
		      AND fa.currentStatus = 'CONFIRMED'
		      AND fa.leg.draftMode = false
		      AND fa.leg.scheduledArrival < :moment
		""")
	List<FlightCrewMember> findColleaguesByLegId(int legId, int crewMemberId, Date moment);

	@Query("""
		    SELECT a.currentStatus, a
		    FROM FlightAssignment a
		    WHERE a.flightCrewMember.id = :id
		      AND a.draftMode = false
		      AND a.leg.draftMode = false
		""")
	List<Object[]> findAssignmentsByStatus(int id);

	@Query("""
		    SELECT FUNCTION('DATE', a.leg.scheduledDeparture) AS day, COUNT(a)
		    FROM FlightAssignment a
		    WHERE a.flightCrewMember.id = :id
		      AND a.draftMode = false
		      AND a.currentStatus = 'CONFIRMED'
		      AND a.leg.draftMode = false
		      AND (
		        a.leg.scheduledDeparture BETWEEN :start AND :now
		        OR a.leg.scheduledArrival BETWEEN :start AND :now
		      )
		    GROUP BY FUNCTION('DATE', a.leg.scheduledDeparture)
		    ORDER BY FUNCTION('DATE', a.leg.scheduledDeparture)
		""")
	List<Object[]> findAssignmentsCountPerDayInLastMonth(int id, Date start, Date now);

}
