
package acme.features.assistanceAgent.dashboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Indicator;
import acme.forms.AssistanceAgentDashboard;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentDashboardShowService extends AbstractGuiService<AssistanceAgent, AssistanceAgentDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentDashboardRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		AssistanceAgentDashboard dashboard = new AssistanceAgentDashboard();

		dashboard.setRatioOfClaimsStoredSuccessfully(this.repository.ratioOfClaimsStoredSuccessfully(Indicator.ACCEPTED));
		dashboard.setRatioOfClaimsRejected(this.repository.ratioOfClaimsRejected(Indicator.REJECTED));

		List<Integer> topMonths = this.repository.topThreeMonthsHighestNumberOfClaims(Pageable.ofSize(3));
		dashboard.setTopThreeMonthsHighestNumberOfClaims(topMonths);

		dashboard.setAvgNumberOfLogsClaimsHave(0.0);
		dashboard.setMinNumberOfLogsClaimsHave(0.0);
		dashboard.setMaxNumberOfLogsClaimsHave(0.0);
		dashboard.setDevNumberOfLogsClaimsHave(0.0);

		dashboard.setAvgNumberOfLogsClaimsAssistedDuringLastMonth(0.0);
		dashboard.setMinNumberOfLogsClaimsAssistedDuringLastMonth(0.0);
		dashboard.setMaxNumberOfLogsClaimsAssistedDuringLastMonth(0.0);
		dashboard.setDevNumberOfLogsClaimsAssistedDuringLastMonth(0.0);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AssistanceAgentDashboard object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "ratioOfClaimsStoredSuccessfully", "ratioOfClaimsRejected", "topThreeMonthsHighestNumberOfClaims", "avgNumberOfLogsClaimsHave", "minNumberOfLogsClaimsHave", "maxNumberOfLogsClaimsHave",
			"devNumberOfLogsClaimsHave", "avgNumberOfLogsClaimsAssistedDuringLastMonth", "minNumberOfLogsClaimsAssistedDuringLastMonth", "maxNumberOfLogsClaimsAssistedDuringLastMonth", "devNumberOfLogsClaimsAssistedDuringLastMonth");

		super.getResponse().addData(dataset);
	}
}
