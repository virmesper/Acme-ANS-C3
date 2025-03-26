
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.AssistanceAgent;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

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

			super.state(context, validFormat, "employeeCode", "acme.validation.employeeCode.format.message");
			super.state(context, initialsMatch, "employeeCode", "acme.validation.employeeCode.initials.message");
		}

		result = !super.hasErrors(context);
		return result;
	}

	private String extractInitials(final String fullName) {
		String cleanedFullName = fullName.replace(",", "").trim();

		String[] parts = cleanedFullName.split("\\s+");
		StringBuilder initials = new StringBuilder();

		for (String part : parts)
			if (!part.isEmpty() && initials.length() < 3)
				initials.append(part.charAt(0));

		return initials.toString().toUpperCase();
	}
}
