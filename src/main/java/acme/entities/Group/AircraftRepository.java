
package acme.entities.Group;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface AircraftRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM Aircraft a WHERE a.registrationnumber = :registrationnumber")
	Boolean existsAircraftWithregistrationnumber(String registrationnumber);

	@Query("SELECT a.registrationnumber FROM Aircraft a")
	List<String> findAllAircraftRegistrationnumber();

}
