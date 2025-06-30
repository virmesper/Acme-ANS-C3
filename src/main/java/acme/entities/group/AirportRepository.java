
package acme.entities.group;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirportRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM Airport a WHERE a.iataCode = :iataCode")
	Boolean existsAirportWithIataCode(String iataCode);

	@Query("SELECT a.iataCode FROM Airport a")
	List<String> findAllAirportIataCode();

}
