
package acme.features.administrator.tracking_log;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student1.Leg;
import acme.entities.student4.TrackingLog;

@Repository
public interface AdministratorTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.draftMode = false")
	public Collection<TrackingLog> findAllPublishedTrackingLogs();

	@Query("select s from TrackingLog s where s.id = :id")
	public TrackingLog findOneTrackingLogById(int id);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.flight.draftMode = false")
	public Collection<Leg> findAllLegs();

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId and t.draftMode=false")
	Collection<TrackingLog> findTrackingLogsPublishedByClaimId(int claimId);

}
