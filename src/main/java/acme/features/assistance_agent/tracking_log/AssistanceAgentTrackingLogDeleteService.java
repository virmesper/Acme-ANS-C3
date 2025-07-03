
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
public class AssistanceAgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentTrackingLogRepository repository;


	@Autowired
	public AssistanceAgentTrackingLogDeleteService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	@Override
	public void authorise() {
		int masterId;
		TrackingLog trackingLog;
		Claim claim;
		AssistanceAgent assistanceAgent;
		boolean status = false;

		masterId = super.getRequest().getData("id", int.class);

		trackingLog = this.repository.findOneTrackingLogById(masterId);

		claim = trackingLog == null ? null : trackingLog.getClaim();

		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();

		if (trackingLog != null || claim != null)
			if (trackingLog.isDraftMode())
				if (super.getRequest().getPrincipal().hasRealm(assistanceAgent))
					status = true;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog object;
		int masterId;

		masterId = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrackingLogById(masterId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		// Intentionally left blank
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		// Intentionally left blank: no additional validation required
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "draftMode", "resolution", "indicator");
		dataset.put("indicators", indicatorChoices);

		super.getResponse().addData(dataset);
	}
}
