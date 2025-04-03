
package acme.features.flightcrewmember.activitylog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("SELECT a FROM ActivityLog a WHERE a.flightCrewMember.id = :flightCrewMemberId")
	Collection<ActivityLog> findAllActivityLogs(int flightCrewMemberId);

	@Query("select a from FlightCrewMember a where a.id = :id")
	FlightAssignment findFlightCrewMemberById(int id);

	@Query("select a from FlightCrewMember a")
	List<FlightCrewMember> findAllFlightCrewMember();

	@Query("SELECT a FROM ActivityLog a WHERE a.id = :activityLogId")
	ActivityLog findActivityLogById(int activityLogId);

}
