
package acme.features.administrator.recommendation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AdministratorRecommendationRepository extends AbstractRepository {

	@Query("select count(r)>0 from Recommendation r where r.source = :source and r.externalId = :externalId")
	boolean existsBySourceAndExternalId(String source, String externalId);
}
