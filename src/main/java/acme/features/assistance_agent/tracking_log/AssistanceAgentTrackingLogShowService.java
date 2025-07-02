
package acme.features.assistance_agent.tracking_log;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Claim;
import acme.entities.student4.Indicator;
import acme.entities.student4.TrackingLog;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentTrackingLogRepository repository;


	@Autowired
	public AssistanceAgentTrackingLogShowService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		boolean status = false;
		int masterId;
		TrackingLog trackingLog;

		masterId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findOneTrackingLogById(masterId);
		if (trackingLog != null)
			if (super.getRequest().getPrincipal().hasRealm(trackingLog.getClaim().getAssistanceAgent()))
				status = true;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId;
		TrackingLog object;

		masterId = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrackingLogById(masterId);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		Boolean exceptionalCase;
		SelectChoices choicesIndicator;
		Claim claim;

		claim = trackingLog.getClaim();
		exceptionalCase = !claim.isDraftMode() && this.repository.countTrackingLogsForExceptionalCase(claim.getId()) == 1;
		choicesIndicator = SelectChoices.from(Indicator.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "draftMode", "resolution", "indicator");
		dataset.put("indicators", choicesIndicator);
		dataset.put("exceptionalCase", exceptionalCase);

		super.getResponse().addData(dataset);
	}

}
