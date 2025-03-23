
package acme.forms;

import java.util.HashMap;
import java.util.List;

import acme.client.components.basis.AbstractForm;

public class FlightCrewDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	// The last five destinations they have been assigned. 
	List<String>				lastFiveDestinationsAssigned;

	// TThe number of legs that have an activity log record with an incident severity ranging from 0 up to 3, 4 up to 7, and 8 up to 10.
	Integer						numberOfLegsWithActivityLogsLowSeverityLevel;
	Integer						numberOfLegsWithActivityLogsMediumSeverityLevel;
	Integer						numberOfLegsWithActivityLogsHighSeverityLevel;

	// The crew members who were assigned with him or her in their last leg. 
	Integer						crewMembersAssignedInLastLeg;

	// Their flight assignments grouped by their statuses. 
	HashMap<String, Integer>	numberOfFlightAssignmentsGroupedByTheirStatus;

	// The average, minimum, maximum, and standard deviation of the number of flight assignments they had in the last month.. 

	Double						averageFlightAssignmentsLastMonth;
	Integer						minimumFlightAssignmentsLastMonth;
	Integer						maximumFlightAssignmentsLastMonth;
	Double						standardDeviationFlightAssignmentsLastMonth;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
