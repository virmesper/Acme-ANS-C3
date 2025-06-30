
package acme.realms.assistance_agent;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface AssistanceAgentRepository extends AbstractRepository {

	@Query("SELECT a FROM AssistanceAgent a WHERE a.employeeCode = :employeeCode")
	AssistanceAgent findByEmployeeCode(String employeeCode);

}
