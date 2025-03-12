
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

	double						ratioResolvedClaims;
	double						ratioRejectedClaims;

	private List<String>		topThreeMonthsWithMostClaims;

	double						averageClaimLogs;
	double						deviationClaimLogs;
	int							minClaimLogs;
	int							maxClaimLogs;

	double						averageClaimsAssisted;
	double						deviationClaimsAssisted;
	int							minClaimsAssisted;
	int							maxClaimsAssisted;

}
