
package acme.features.assistance_agent.tracking_log;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		boolean status = false;

		if (super.getRequest().getMethod().equals("POST")) {
			int masterId = super.getRequest().getData("id", int.class);
			TrackingLog trackingLog = this.repository.findOneTrackingLogById(masterId);
			status = trackingLog != null && trackingLog.isDraftMode() && super.getRequest().getPrincipal().hasRealm(trackingLog.getClaim().getAssistanceAgent());
		}

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
