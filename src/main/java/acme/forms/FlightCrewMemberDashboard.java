
package acme.forms;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import acme.client.components.basis.AbstractForm;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	// Serialisation version

	private static final long							serialVersionUID	= 1L;

	// Attributes

	@Mandatory
	@Valid
	private List<String>								lastFiveDestinations;

	// Número de etapas con actividad (por rangos de severidad: "0-3", "4-7", "8-10")
	@Mandatory
	@Valid
	private Map<String, Long>							activityLogCounts;

	@Mandatory
	@Valid
	private List<FlightCrewMember>						colleaguesInLastStage;

	@Mandatory
	@Valid
	private FlightCrewMember							member;

	// Asignaciones de vuelo agrupadas por estado
	@Mandatory
	@Valid
	private Map<CurrentStatus, List<FlightAssignment>>	assignmentsByStatus;

	@Mandatory
	@ValidNumber(min = 0)
	private Double										average;

	@Mandatory
	@ValidNumber(min = 0)
	private Double										minimum;

	@Mandatory
	@ValidNumber(min = 0)
	private Double										maximum;

	@Mandatory
	@ValidNumber(min = 0)
	private Double										standardDesviation;
}
