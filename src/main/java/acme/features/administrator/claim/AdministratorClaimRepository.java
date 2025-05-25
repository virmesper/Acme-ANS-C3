
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S1.Leg;
import acme.entities.S4.Claim;

@Repository
public interface AdministratorClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.draftMode = false")
	public Collection<Claim> findAllPublishedClaims();

	@Query("select c from Claim c where c.id = :id")
	public Claim findOneClaimById(int id);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.flight.draftMode = false")
	public Collection<Leg> findAllLegs();

	@Query("select c from Claim c")
	public Collection<Claim> findAllClaims();

}
