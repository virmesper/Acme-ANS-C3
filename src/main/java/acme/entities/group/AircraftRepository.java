
package acme.entities.group;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AircraftRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	Boolean existsAircraftWithregistrationNumber(String registrationNumber);

	@Query("SELECT a.registrationNumber FROM Aircraft a")
	List<String> findAllAircraftRegistrationNumber();

	@Query("select ac from Aircraft ac where ac.registrationNumber = :registrationNumber")
	Aircraft findAircraftByRegistrationNumber(String registrationNumber);

}
