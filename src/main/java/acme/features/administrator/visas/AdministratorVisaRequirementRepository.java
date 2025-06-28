
package acme.features.administrator.visas;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S3.VisaRequirement;

@Repository
public interface AdministratorVisaRequirementRepository extends AbstractRepository {

	@Query("SELECT vr FROM VisaRequirement vr WHERE vr.passportCountry = :passport AND vr.destinationCountry = :destination")
	VisaRequirement findOneByPassportAndDestination(String passport, String destination);
}
