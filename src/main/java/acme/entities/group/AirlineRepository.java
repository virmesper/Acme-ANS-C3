
package acme.entities.group;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM Airline a WHERE a.iataCode = :iataCode")
	Boolean existsAirlineWithIataCode(String iataCode);

	@Query("SELECT a.iataCode FROM Airline a")
	List<String> findAllAirlineIataCode();

}
