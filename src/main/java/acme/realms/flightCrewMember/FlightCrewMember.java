
package acme.realms.flightCrewMember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidFlightCrewMember;
import acme.entities.group.Airline;
import acme.entities.student3.AvailabilityStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlightCrewMember
public class FlightCrewMember extends AbstractRole {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", max = 10)
	@Column(unique = true, nullable = false)
	private String				employeeCode;

	@Automapped
	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$", max = 16)
	private String				phoneNumber;

	@Automapped
	@Mandatory
	@ValidString(max = 255)
	private String				languageSkills;

	@Automapped
	@Mandatory
	@Enumerated(EnumType.STRING)
	private AvailabilityStatus	availabilityStatus;

	@Mandatory
	@ValidMoney(min = 0)
	@Automapped
	private Money				salary;

	@Automapped
	@Optional
	@ValidNumber(min = 0, max = 90)
	private Integer				yearsOfExperience;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;
}
