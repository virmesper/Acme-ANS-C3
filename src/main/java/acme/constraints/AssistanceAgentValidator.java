
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.assistanceAgent.AssistanceAgent;
import acme.realms.assistanceAgent.AssistanceAgentRepository;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Autowired
	private AssistanceAgentRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (assistanceAgent == null || assistanceAgent.getEmployeeCode() == null || assistanceAgent.getIdentity().getFullName() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String employeeCode = assistanceAgent.getEmployeeCode().toUpperCase();
			String applicantName = assistanceAgent.getIdentity().getFullName().trim();

			String initials = this.extractInitials(applicantName);

			boolean validFormat = employeeCode.matches("^[A-Z]{2,3}\\d{6}$");
			boolean initialsMatch = employeeCode.startsWith(initials);

			// Verificación de unicidad del código de empleado
			boolean uniqueCode = this.isEmployeeCodeUnique(employeeCode, assistanceAgent.getId());

			super.state(context, validFormat, "employeeCode", "acme.validation.employeeCode.format.message");
			super.state(context, initialsMatch, "employeeCode", "acme.validation.employeeCode.initials.message");
			super.state(context, uniqueCode, "employeeCode", "acme.validation.employeeCode.unique.message");
		}

		result = !super.hasErrors(context);
		return result;
	}

	// Método para extraer las iniciales del nombre completo
	private String extractInitials(final String fullName) {
		String cleanedFullName = fullName.replace(",", "").trim();

		String[] parts = cleanedFullName.split("\\s+");
		StringBuilder initials = new StringBuilder();

		for (String part : parts)
			if (!part.isEmpty() && initials.length() < 3)
				initials.append(part.charAt(0));

		return initials.toString().toUpperCase();
	}

	// Método para verificar la unicidad del código de empleado
	private boolean isEmployeeCodeUnique(final String employeeCode, final int agentId) {
		AssistanceAgent existingAgent = this.repository.findByEmployeeCode(employeeCode);

		// Si no existe ningún agente con el mismo código, es único
		if (existingAgent == null)
			return true;

		// Si existe el agente, pero el ID coincide (es el mismo agente), también es válido
		return existingAgent.getId() == agentId;
	}

}
