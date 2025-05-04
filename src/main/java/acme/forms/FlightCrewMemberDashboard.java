
package acme.forms;

import java.util.HashMap;
import java.util.List;

import acme.client.components.basis.AbstractForm;

public class FlightCrewMemberDashboard extends AbstractForm {

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

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
