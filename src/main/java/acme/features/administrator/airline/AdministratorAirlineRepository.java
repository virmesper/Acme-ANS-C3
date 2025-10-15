
package acme.features.administrator.airline;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.group.Airline;
import acme.entities.group.Airport;

@Repository
public interface AdministratorAirlineRepository extends AbstractRepository {

	@Query("select a from Airline a where a.id = :id")
	Airline findAirlineById(final int id);

	@Query("select a from Airline a")
	List<Airline> findAllAirlines();
	@Query("select ap from Airport ap")
	List<Airport> findAllAirports();

	@Query("select ap from Airport ap where ap.id = :id")
	Airport findAirportById(int id);

	@Query("select al from Airline al where al.iataCode = :code")
	Airline findAirlineByIataCode(String code);
}
