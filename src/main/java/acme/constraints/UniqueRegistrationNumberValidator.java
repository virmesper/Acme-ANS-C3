
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.entities.group.AircraftRepository;

public class UniqueRegistrationNumberValidator extends AbstractValidator<ValidUniqueRegistrationNumber, String> {

	@Autowired
	AircraftRepository aircraftRepository;


	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return false;

		Boolean existsInAircraft = this.aircraftRepository.existsAircraftWithregistrationNumber(value);

		return !existsInAircraft;
	}

}
