
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.assistance_agent.AssistanceAgent;
import acme.realms.assistance_agent.AssistanceAgentRepository;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	private static final String				EMPLOYEE_CODE	= "employeeCode";

	private final AssistanceAgentRepository	repository;


	@Autowired
	public AssistanceAgentValidator(final AssistanceAgentRepository repository) {
		this.repository = repository;
	}

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

			String initials = AssistanceAgentValidator.extractInitials(applicantName);

			boolean validFormat = employeeCode.matches("^[A-Z]{2,3}\\d{6}$");
			boolean initialsMatch = employeeCode.startsWith(initials);

			// Verificación de unicidad del código de empleado
			boolean uniqueEmployeeCode;
			AssistanceAgent existingAgent = this.repository.findByEmployeeCode(employeeCode);
			uniqueEmployeeCode = existingAgent == null || existingAgent.equals(assistanceAgent);

			super.state(context, validFormat, AssistanceAgentValidator.EMPLOYEE_CODE, "acme.validation.employeeCode.format.message");
			super.state(context, initialsMatch, AssistanceAgentValidator.EMPLOYEE_CODE, "acme.validation.employeeCode.initials.message");
			super.state(context, uniqueEmployeeCode, AssistanceAgentValidator.EMPLOYEE_CODE, "acme.validation.employeeCode.unique.message");
		}

		result = !super.hasErrors(context);
		return result;
	}

	// Método para extraer las iniciales del nombre completo
	public static String extractInitials(final String fullName) {
		String cleanedFullName = fullName.replace(",", "").trim();

		String[] parts = cleanedFullName.split("\\s+");
		StringBuilder initials = new StringBuilder();

		for (String part : parts)
			if (!part.isEmpty() && initials.length() < 3)
				initials.append(part.charAt(0));

		return initials.toString().toUpperCase();
	}

}
