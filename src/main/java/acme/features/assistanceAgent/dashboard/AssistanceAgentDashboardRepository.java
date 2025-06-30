
package acme.features.assistanceAgent.dashboard;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S4.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@Repository
public interface AssistanceAgentDashboardRepository extends AbstractRepository {

	@Query("SELECT COUNT(t) FROM TrackingLog t WHERE t.claim.id = :claimId")
	long countLogsByClaimId(int claimId);

	@Query("select a from AssistanceAgent a")
	Collection<AssistanceAgent> findAllAssistanceAgent();

	@Query("SELECT aa FROM AssistanceAgent aa WHERE aa.id = :id")
	AssistanceAgent findAssistanceAgentById(int id);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :id")
	Collection<Claim> findClaimsByAssistanceAgentId(int id);

	// Top 3 meses con más claims
	@Query("SELECT MONTH(c.registrationMoment) FROM Claim c WHERE c.assistanceAgent.id = :agentId AND c.draftMode = false GROUP BY MONTH(c.registrationMoment) ORDER BY COUNT(c) DESC")
	List<Integer> topThreeMonthsHighestNumberOfClaimsByAgent(int agentId, Pageable pageable);

}
