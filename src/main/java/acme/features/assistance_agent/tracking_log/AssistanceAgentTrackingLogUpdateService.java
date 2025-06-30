
package acme.features.assistanceAgent.trackingLog;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Claim;
import acme.entities.S4.Indicator;
import acme.entities.S4.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		AssistanceAgent assistanceAgent;
		TrackingLog trackingLog;

		masterId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findOneTrackingLogById(masterId);
		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
		status = trackingLog != null && trackingLog.isDraftMode() && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

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
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "indicator");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors("indicator")) {
			boolean bool1;
			boolean bool2;

			if (!super.getBuffer().getErrors().hasErrors("resolutionPercentage")) {
				bool1 = trackingLog.getIndicator() == Indicator.PENDING && trackingLog.getResolutionPercentage() < 100;
				bool2 = trackingLog.getIndicator() != Indicator.PENDING && trackingLog.getResolutionPercentage() == 100;
				super.state(bool1 || bool2, "indicator", "assistanceAgent.tracking-log.form.error.indicator-pending");
			}

		}

		if (!super.getBuffer().getErrors().hasErrors("resolution")) {
			boolean isPending = trackingLog.getIndicator() == Indicator.PENDING;

			boolean valid = isPending && !Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent()
				|| !isPending && Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent();

			super.state(valid, "resolution", "assistanceAgent.tracking-log.form.error.resolution-not-null");

		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		Claim claim;

		claim = this.repository.findOneClaimById(trackingLog.getClaim().getId());
		claim.setIndicator(trackingLog.getIndicator());

		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		this.repository.save(trackingLog);
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
