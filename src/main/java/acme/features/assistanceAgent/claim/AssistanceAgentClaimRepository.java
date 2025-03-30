
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Leg;
import acme.entities.S4.Claim;
import acme.entities.S4.ClaimStatus;
import acme.entities.S4.TrackingLog;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.assistanceAgent.id = :id and c.indicator <> :type")
	public Collection<Claim> findManyClaimsCompletedByMasterId(int id, ClaimStatus type);

	@Query("select l from Leg l")
	public Collection<Leg> findAllLegs();

	@Query("select c from Claim c where c.assistanceAgent.id = :id")
	public Collection<Claim> findManyClaimsByMasterId(int id);

	@Query("select c from Claim c where c.id = :id")
	public Claim findOneClaimById(int id);

	@Query("select a from AssistanceAgent a where a.id = :id")
	public AssistanceAgent findOneAssitanceAgentById(int id);

	@Query("select l from Leg l where l.id = :id")
	public Leg findOneLegById(int id);

	@Query("select t from TrackingLog t where t.claim.id = :id")
	public Collection<TrackingLog> findManyTrackingLogsByClaimId(int id);

	@Query("select count(t) = 0 from TrackingLog t where t.draftMode = true and t.claim.id = :id")
	public boolean allTrackingLogsPublishedByClaimId(int id);

}
