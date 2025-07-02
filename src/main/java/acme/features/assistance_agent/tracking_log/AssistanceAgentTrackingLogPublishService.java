
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
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private static final String							INDICATOR				= "indicator";
	private static final String							RESOLUTION_PERCENTAGE	= "resolutionPercentage";
	private static final String							RESOLUTION				= "resolution";

	private final AssistanceAgentTrackingLogRepository	repository;


	@Autowired
	public AssistanceAgentTrackingLogPublishService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interfaced ------------------------------------------

	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().getMethod().equals("GET")) {
			Integer id = super.getRequest().getData("id", Integer.class);
			if (id != null) {
				TrackingLog trackingLog = this.repository.findOneTrackingLogById(id);

				if (trackingLog != null && trackingLog.getClaim() != null) {
					AssistanceAgent assistanceAgent = trackingLog.getClaim().getAssistanceAgent();
					status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && trackingLog.isDraftMode();
				}
			}
		} else if (super.getRequest().getMethod().equals("POST")) {
			Integer masterId = super.getRequest().getData("id", Integer.class);
			if (masterId != null) {
				TrackingLog trackingLog = this.repository.findOneTrackingLogById(masterId);

				if (trackingLog != null && trackingLog.getClaim() != null) {
					AssistanceAgent assistanceAgent = trackingLog.getClaim().getAssistanceAgent();
					status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && trackingLog.isDraftMode();
				}
			}
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
		// intentionally left blank to prevent post hacking
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		this.validateClaimIsPublished(trackingLog);
		this.validateMaxTwoExceptionalCases(trackingLog);
		this.validateIndicatorResolutionConsistency(trackingLog);
		this.validateResolutionPercentageLimits(trackingLog);
		this.validateResolutionPresence(trackingLog);
	}

	private void validateClaimIsPublished(final TrackingLog trackingLog) {
		super.state(!trackingLog.getClaim().isDraftMode(), "*", "assistanceAgent.tracking-log.form.error.claim-must-be-published");
	}

	private void validateMaxTwoExceptionalCases(final TrackingLog trackingLog) {
		super.state(this.repository.countTrackingLogsForExceptionalCase(trackingLog.getClaim().getId()) < 2, "*", "assistanceAgent.tracking-log.form.error.only-two-tracking-logs-100");
	}

	private void validateIndicatorResolutionConsistency(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogPublishService.INDICATOR) && !super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogPublishService.RESOLUTION_PERCENTAGE)) {

			boolean bool1 = trackingLog.getIndicator() == Indicator.PENDING && trackingLog.getResolutionPercentage() < 100;
			boolean bool2 = trackingLog.getIndicator() != Indicator.PENDING && trackingLog.getResolutionPercentage() == 100;

			super.state(bool1 || bool2, AssistanceAgentTrackingLogPublishService.INDICATOR, "assistanceAgent.tracking-log.form.error.indicator-pending");
		}
	}

	private void validateResolutionPercentageLimits(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogPublishService.RESOLUTION_PERCENTAGE)) {
			Double maxResolutionPercentage = this.repository.findMaxResolutionPercentageByClaimId(trackingLog.getId(), trackingLog.getClaim().getId());
			double finalMax = maxResolutionPercentage != null ? maxResolutionPercentage : -0.01;
			super.state(trackingLog.getResolutionPercentage() > finalMax, AssistanceAgentTrackingLogPublishService.RESOLUTION_PERCENTAGE, "assistanceAgent.tracking-log.form.error.less-than-max-resolution-percentage");
		}
	}

	private void validateResolutionPresence(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogPublishService.RESOLUTION)) {
			boolean isPending = trackingLog.getIndicator() == Indicator.PENDING;
			boolean hasContent = Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent();

			boolean valid = isPending && !hasContent || !isPending && hasContent;
			super.state(valid, AssistanceAgentTrackingLogPublishService.RESOLUTION, "assistanceAgent.tracking-log.form.error.resolution-not-null");
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		Claim claim;

		claim = this.repository.findOneClaimById(trackingLog.getClaim().getId());
		claim.setIndicator(trackingLog.getIndicator());

		trackingLog.setDraftMode(false);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		this.repository.save(claim);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", AssistanceAgentTrackingLogPublishService.RESOLUTION_PERCENTAGE, AssistanceAgentTrackingLogPublishService.INDICATOR, AssistanceAgentTrackingLogPublishService.RESOLUTION,
			"draftMode");
		dataset.put("indicators", indicatorChoices);

		super.getResponse().addData(dataset);
	}

}
