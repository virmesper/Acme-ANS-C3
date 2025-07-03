
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student1.Leg;
import acme.entities.student4.Claim;
import acme.entities.student4.Indicator;
import acme.entities.student4.TrackingLog;
import acme.realms.assistance_agent.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT a FROM AssistanceAgent a WHERE a.id = :agentId")
	AssistanceAgent findAssistanceAgentById(int agentId);

	@Query("SELECT c FROM Claim c WHERE c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.flight.draftMode = false")
	public Collection<Leg> findAllLegs();

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegById(int legId);

	@Query("select c from Claim c where c.assistanceAgent.id = :id and c.indicator != :type ORDER BY c.id ASC")
	public Collection<Claim> findManyClaimsCompletedByMasterId(int id, Indicator type);

	@Query("select c from Claim c where c.assistanceAgent.id = :id and c.indicator = :type ORDER BY c.id ASC")
	public Collection<Claim> findManyClaimsUndergoingByMasterId(int id, Indicator type);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.flight.draftMode = false")
	Collection<Leg> findAvailableLegs();

	@Query("SELECT c.leg FROM Claim c WHERE c.id = :claimId")
	Leg findLegByClaimId(int claimId);

	@Query("select count(t) = 0 from TrackingLog t where t.draftMode = true and t.claim.id = :id")
	boolean allTrackingLogsPublishedByClaimId(int id);

	@Query("select max(t.resolutionPercentage) from TrackingLog t where t.claim.id = :claimId")
	Double findMaxResolutionPercentageByClaimId(int claimId);

	@Query("select t from TrackingLog t where t.claim.id = :id")
	public Collection<TrackingLog> findManyTrackingLogsByClaimId(int id);

}
