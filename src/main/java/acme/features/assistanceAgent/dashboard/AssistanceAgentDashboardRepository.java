
package acme.features.assistanceAgent.dashboard;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S4.Indicator;

@Repository
public interface AssistanceAgentDashboardRepository extends AbstractRepository {

	// Ratio de claims aceptadas/rechazadas
	@Query("SELECT 1.0 * COUNT(c) / (SELECT COUNT(c1) FROM Claim c1) FROM Claim c WHERE c.indicator = :indicator")
	Double ratioOfClaimsStoredSuccessfully(Indicator indicator);

	// Ratio de claims rechazadas
	@Query("SELECT 1.0 * COUNT(c) / (SELECT COUNT(c1) FROM Claim c1) FROM Claim c WHERE c.indicator = :indicator")
	Double ratioOfClaimsRejected(Indicator indicator);

	// Top 3 meses con más claims
	@Query("SELECT MONTH(c.registrationMoment) FROM Claim c GROUP BY MONTH(c.registrationMoment) ORDER BY COUNT(c) DESC")
	List<Integer> topThreeMonthsHighestNumberOfClaims(Pageable pageable);

	// Promedio de logs por claim
	@Query("SELECT AVG(count(l)) FROM Claim c JOIN TrackingLog l ON l.claim = c GROUP BY c.id")
	Double avgNumberOfLogsClaimsHave();

	// Mínimo de logs por claim
	@Query("SELECT MIN(count(l)) FROM Claim c JOIN TrackingLog l ON l.claim = c GROUP BY c.id")
	Double minNumberOfLogsClaimsHave();

	// Máximo de logs por claim
	@Query("SELECT MAX(count(l)) FROM Claim c JOIN TrackingLog l ON l.claim = c GROUP BY c.id")
	Double maxNumberOfLogsClaimsHave();

	// Desviación estándar de logs por claim
	@Query("SELECT STDDEV(count(l)) FROM Claim c JOIN TrackingLog l ON l.claim = c GROUP BY c.id")
	Double devNumberOfLogsClaimsHave();

	// Promedio de claims asistidos por asistencia agente en el último mes
	@Query("""
		    SELECT AVG(count(c))
		    FROM Claim c
		    WHERE c.assistanceAgent IS NOT NULL
		      AND c.registrationMoment > CURRENT_DATE - 30
		    GROUP BY c.assistanceAgent
		""")
	Double avgNumberOfClaimsAssistedDuringLastMonth();

	// Mínimo de claims asistidos por asistencia agente en el último mes
	@Query("""
		    SELECT MIN(count(c))
		    FROM Claim c
		    WHERE c.assistanceAgent IS NOT NULL
		      AND c.registrationMoment > CURRENT_DATE - 30
		    GROUP BY c.assistanceAgent
		""")
	Double minNumberOfClaimsAssistedDuringLastMonth();

	// Máximo de claims asistidos por asistencia agente en el último mes
	@Query("""
		    SELECT MAX(count(c))
		    FROM Claim c
		    WHERE c.assistanceAgent IS NOT NULL
		      AND c.registrationMoment > CURRENT_DATE - 30
		    GROUP BY c.assistanceAgent
		""")
	Double maxNumberOfClaimsAssistedDuringLastMonth();

	// Desviación estándar de claims asistidos por asistencia agente en el último mes
	@Query("""
		    SELECT STDDEV(count(c))
		    FROM Claim c
		    WHERE c.assistanceAgent IS NOT NULL
		      AND c.registrationMoment > CURRENT_DATE - 30
		    GROUP BY c.assistanceAgent
		""")
	Double devNumberOfClaimsAssistedDuringLastMonth();

}
