
package acme.entities.S4;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import acme.entities.Group.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "assistance_agents")

public class AssistanceAgent extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Column(length = 10, unique = true, nullable = false)
	@Pattern(regexp = "^[A-Z]{2,3}\\d{6}$", message = "Invalid employee code format")
	private String				employeeCode;

	@ElementCollection
	@Column(length = 255, nullable = false)
	private List<String>		spokenLanguages;

	//Fecha en la que el agente comenzó a trabajar en la aerolínea 
	@Past
	@NotNull
	@Column(nullable = false)
	private LocalDate			startDate;

	@Size(max = 255)
	@Column(length = 255, nullable = true)
	private String				biografia;

	@Positive
	@Column(nullable = true)
	private Double				salary;

	@Column(length = 255, nullable = true)
	private String				photoUrl;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	// Aerolínea para la que trabaja
	@ManyToOne(optional = false)
	@JoinColumn(name = "airline_id", nullable = false)
	private Airline				airline;

}
