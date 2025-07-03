
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
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------
	private static final String							MASTER_ID				= "masterId";
	private static final String							RESOLUTION				= "resolution";
	private static final String							RESOLUTION_PERCENTAGE	= "resolutionPercentage";
	private static final String							INDICATOR				= "indicator";

	private final AssistanceAgentTrackingLogRepository	repository;


	@Autowired
	public AssistanceAgentTrackingLogCreateService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		boolean status = false;
		int masterId = super.getRequest().getData(AssistanceAgentTrackingLogCreateService.MASTER_ID, int.class);
		Claim claim = this.repository.findOneClaimById(masterId);

		if (claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent())) {
			var logs = this.repository.findManyTrackingLogsClaimId(masterId);

			boolean noTrackingLogs = logs.isEmpty();

			boolean noPublishedWith100 = logs.stream().filter(t -> !t.isDraftMode()).noneMatch(t -> t.getResolutionPercentage() == 100);

			status = noTrackingLogs || noPublishedWith100;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog object;
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData(AssistanceAgentTrackingLogCreateService.MASTER_ID, int.class);
		claim = this.repository.findOneClaimById(masterId);

		object = new TrackingLog();
		object.setDraftMode(true);
		object.setClaim(claim);
		object.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		object.setIndicator(Indicator.PENDING);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", AssistanceAgentTrackingLogCreateService.RESOLUTION_PERCENTAGE, AssistanceAgentTrackingLogCreateService.RESOLUTION, AssistanceAgentTrackingLogCreateService.INDICATOR);
	}
	@Override
	public void validate(final TrackingLog trackingLog) {
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogCreateService.INDICATOR)) {
			boolean bool1;
			boolean bool2;
			if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogCreateService.RESOLUTION_PERCENTAGE)) {
				bool1 = trackingLog.getIndicator() == Indicator.PENDING && trackingLog.getResolutionPercentage() < 100;
				bool2 = trackingLog.getIndicator() != Indicator.PENDING && trackingLog.getResolutionPercentage() == 100;
				super.state(bool1 || bool2, AssistanceAgentTrackingLogCreateService.INDICATOR, "assistanceAgent.tracking-log.form.error.indicator-pending");
			}

		}

		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogCreateService.RESOLUTION)) {
			boolean isPending = trackingLog.getIndicator() == Indicator.PENDING;

			boolean valid = isPending && !Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent()
				|| !isPending && Optional.ofNullable(trackingLog.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent();

			super.state(valid, AssistanceAgentTrackingLogCreateService.RESOLUTION, "assistanceAgent.tracking-log.form.error.resolution-not-null");

		}
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogCreateService.RESOLUTION_PERCENTAGE)) {
			var publishedLogs = this.repository.findManyTrackingLogsClaimId(trackingLog.getClaim().getId()).stream().filter(t -> !t.isDraftMode()).toList();

			double maxPublished = publishedLogs.stream().mapToDouble(TrackingLog::getResolutionPercentage).max().orElse(-0.01);

			super.state(trackingLog.getResolutionPercentage() > maxPublished, AssistanceAgentTrackingLogCreateService.RESOLUTION_PERCENTAGE, "assistanceAgent.tracking-log.form.error.less-than-max-resolution-percentage");
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

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", AssistanceAgentTrackingLogCreateService.RESOLUTION_PERCENTAGE, AssistanceAgentTrackingLogCreateService.RESOLUTION, AssistanceAgentTrackingLogCreateService.INDICATOR);
		dataset.put(AssistanceAgentTrackingLogCreateService.MASTER_ID, super.getRequest().getData(AssistanceAgentTrackingLogCreateService.MASTER_ID, int.class));
		dataset.put("indicators", indicatorChoices);

		super.getResponse().addData(dataset);
	}

}
