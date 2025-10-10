
package acme.features.authenticated.customer.recommendation;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student2.Booking;
import acme.entities.student2.Recommendation;

@Repository
public interface CustomerRecommendationRepository extends AbstractRepository {

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(int customerId);

	@Query("""
		  select r from Recommendation r
		  where (r.city is not null and lower(r.city) in :cities)
		     or (r.country is not null and lower(r.country) in :countries)
		""")
	Collection<Recommendation> findByCitiesOrCountries(Set<String> cities, Set<String> countries);

	@Query("select r from Recommendation r where r.id = :id")
	Recommendation findOneById(int id);
}
