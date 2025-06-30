
package acme.features.administrator.airport;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.group.Airport;

@Repository
public interface AdministratorAirportRepository extends AbstractRepository {

	@Query("select a from Airport a")
	Collection<Airport> findAllAirport();

	@Query("select a from Airport a where a.id = :id")
	Airport findAirportById(int id);

	@Query("SELECT a FROM Airport a WHERE a.iataCode = :iataCode")
	Airport findAirportByIataCode(String iataCode);

}
