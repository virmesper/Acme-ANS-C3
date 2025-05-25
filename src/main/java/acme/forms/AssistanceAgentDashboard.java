
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistanceAgentDashboard extends AbstractForm {

	// Serialisation Identifier

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Double						ratioOfClaimsStoredSuccessfully;
	Double						ratioOfClaimsRejected;

	List<Integer>				topThreeMonthsHighestNumberOfClaims;

	Double						avgNumberOfLogsClaimsHave;
	Double						minNumberOfLogsClaimsHave;
	Double						maxNumberOfLogsClaimsHave;
	Double						devNumberOfLogsClaimsHave;

	Double						avgNumberOfLogsClaimsAssistedDuringLastMonth;
	Double						minNumberOfLogsClaimsAssistedDuringLastMonth;
	Double						maxNumberOfLogsClaimsAssistedDuringLastMonth;
	Double						devNumberOfLogsClaimsAssistedDuringLastMonth;

}
