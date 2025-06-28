
package acme.features.flightCrewMember.visa;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S3.VisaRequirement;

@Repository
public interface FlightCrewMemberVisaRequirementRepository extends AbstractRepository {

	// Devuelve la lista de países a donde ha volado/va a volar un FlightCrewMember
	@Query("""
		    SELECT DISTINCT a.leg.arrivalAirport.country
		    FROM FlightAssignment a
		    WHERE a.flightCrewMember.id = :crewMemberId
		      AND a.draftMode = false
		      AND a.currentStatus = 'CONFIRMED'
		      AND a.leg.draftMode = false
		      AND a.leg.status IN ('LANDED','ON_TIME','DELAYED')
		""")
	List<String> findDestinationCountriesByCrewMemberId(int crewMemberId);

	// Devuelve los VisaRequirement que tengan un destinationCountry entre la lista de países anterior.
	@Query("""
		    SELECT v
		    FROM VisaRequirement v
		    WHERE v.destinationCountry IN :countries
		""")
	List<VisaRequirement> findVisaRequirementsByCountries(Collection<String> countries);

	// Devuelve un VisaRequirement concreto por su id (para el show).
	@Query("""
		    SELECT v
		    FROM VisaRequirement v
		    WHERE v.id = :id
		""")
	VisaRequirement findOneById(int id);
}
