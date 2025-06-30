
package acme.features.assistance_agent.tracking_log;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Claim;
import acme.entities.student4.Indicator;
import acme.entities.student4.TrackingLog;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private static final String							INDICATOR				= "indicator";
	private static final String							RESOLUTION				= "resolution";
	private static final String							RESOLUTION_PERCENTAGE	= "resolutionPercentage";

	private final AssistanceAgentTrackingLogRepository	repository;


	@Autowired
	public AssistanceAgentTrackingLogUpdateService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interfaced ------------------------------------------

	@Override
	public void authorise() {
		boolean status = false;
		int id = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findOneTrackingLogById(id);

		if (trackingLog != null) {
			Claim claim = trackingLog.getClaim();
			boolean isDraft = trackingLog.isDraftMode();
			boolean isOwner = super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

			status = isDraft && isOwner;
		}

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
		super.bindObject(trackingLog, "step", AssistanceAgentTrackingLogUpdateService.RESOLUTION_PERCENTAGE, AssistanceAgentTrackingLogUpdateService.RESOLUTION, AssistanceAgentTrackingLogUpdateService.INDICATOR);
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogUpdateService.INDICATOR)) {
			boolean bool1;
			boolean bool2;

			if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogUpdateService.RESOLUTION_PERCENTAGE)) {
				bool1 = trackingLog.getIndicator() == Indicator.PENDING && trackingLog.getResolutionPercentage() < 100;
				bool2 = trackingLog.getIndicator() != Indicator.PENDING && trackingLog.getResolutionPercentage() == 100;
				super.state(bool1 || bool2, AssistanceAgentTrackingLogUpdateService.INDICATOR, "assistanceAgent.tracking-log.form.error.indicator-pending");
			}

		}

		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogUpdateService.RESOLUTION)) {
			boolean isPending = trackingLog.getIndicator() == Indicator.PENDING;

			boolean valid = isPending && !Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent()
				|| !isPending && Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent();

			super.state(valid, AssistanceAgentTrackingLogUpdateService.RESOLUTION, "assistanceAgent.tracking-log.form.error.resolution-not-null");

		}

		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogUpdateService.RESOLUTION_PERCENTAGE)) {
			Double maxResolutionPercentage = this.repository.findMaxResolutionPercentageByClaimId(trackingLog.getId(), trackingLog.getClaim().getId());
			double finalMax = maxResolutionPercentage != null ? maxResolutionPercentage : -0.01;
			super.state(trackingLog.getResolutionPercentage() > finalMax, AssistanceAgentTrackingLogUpdateService.RESOLUTION_PERCENTAGE, "assistanceAgent.tracking-log.form.error.less-than-max-resolution-percentage");
		}

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", AssistanceAgentTrackingLogUpdateService.RESOLUTION_PERCENTAGE, AssistanceAgentTrackingLogUpdateService.INDICATOR, AssistanceAgentTrackingLogUpdateService.RESOLUTION,
			"draftMode");
		dataset.put("indicators", indicatorChoices);

		super.getResponse().addData(dataset);
	}
}
