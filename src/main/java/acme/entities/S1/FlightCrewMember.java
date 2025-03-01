
package acme.entities.S1;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.Group.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightCrewMember extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", max = 10)
	@Column(unique = true, nullable = false)
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$", max = 16)
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 255)
	private String				languageSkills;

	@Mandatory
	@Enumerated(EnumType.STRING)
	private AvailabilityStatus	availabilityStatus;

	@Mandatory
	@Valid
	@ManyToOne
	private Airline				airline;

	@Mandatory
	@ValidNumber(min = 0)
	private Double				salary;

	@Optional
	@ValidNumber(min = 0)
	private Integer				yearsOfExperience;
}
