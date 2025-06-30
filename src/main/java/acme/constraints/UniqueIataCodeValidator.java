
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.group.AirlineRepository;
import acme.entities.group.AirportRepository;

@Validator
public class UniqueIataCodeValidator extends AbstractValidator<ValidUniqueIataCode, String> {

	@Autowired
	AirlineRepository	airlineRepository;

	@Autowired
	AirportRepository	airportRepository;


	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return false;

		Boolean existsInAirline = this.airlineRepository.existsAirlineWithIataCode(value);
		Boolean existsInAirport = this.airportRepository.existsAirportWithIataCode(value);

		return !(existsInAirline || existsInAirport);
	}

}
