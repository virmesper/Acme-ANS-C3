
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Indicator;
import acme.entities.S4.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		AssistanceAgent assistanceAgent;
		TrackingLog trackingLog;

		masterId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findOneTrackingLogById(masterId);
		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
		status = trackingLog != null && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

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

		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "indicator", "resolution", "draftMode");
		dataset.put("indicators", indicatorChoices);

		super.getResponse().addData(dataset);
	}

}
