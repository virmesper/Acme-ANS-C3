
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
public class AssistanceAgentTrackingLogCreateExceptionalCaseService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private static final String							RESOLUTION	= "resolution";
	private static final String							MASTER_ID	= "masterId";

	private final AssistanceAgentTrackingLogRepository	repository;


	@Autowired
	public AssistanceAgentTrackingLogCreateExceptionalCaseService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------

	@Override
	public void authorise() {
		boolean status;
		int masterId;
		AssistanceAgent assistanceAgent;
		Claim claim;
		Boolean exceptionalCase;

		masterId = super.getRequest().getData(AssistanceAgentTrackingLogCreateExceptionalCaseService.MASTER_ID, int.class);
		claim = this.repository.findOneClaimById(masterId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		exceptionalCase = this.repository.countTrackingLogsForExceptionalCase(masterId) == 1;

		status = claim != null && exceptionalCase && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog object;
		int masterId;
		Claim claim;
		TrackingLog trackingLog;

		masterId = super.getRequest().getData(AssistanceAgentTrackingLogCreateExceptionalCaseService.MASTER_ID, int.class);
		claim = this.repository.findOneClaimById(masterId);
		trackingLog = this.repository.findManyTrackingLogsClaimIdAndIndicator(masterId, Indicator.PENDING).stream().toList().get(0);

		object = new TrackingLog();
		object.setDraftMode(true);
		object.setClaim(claim);
		object.setResolutionPercentage(100.00);
		object.setIndicator(trackingLog.getIndicator());
		object.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrackingLog object) {
		super.bindObject(object, "step", AssistanceAgentTrackingLogCreateExceptionalCaseService.RESOLUTION);
	}

	@Override
	public void validate(final TrackingLog object) {
		if (!super.getBuffer().getErrors().hasErrors(AssistanceAgentTrackingLogCreateExceptionalCaseService.RESOLUTION))
			super.state(Optional.ofNullable(object.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent(), AssistanceAgentTrackingLogCreateExceptionalCaseService.RESOLUTION, "assistanceAgent.tracking-log.form.error.resolution-not-null");

		boolean exceptionalCase = this.repository.countTrackingLogsForExceptionalCase(object.getClaim().getId()) == 1;
		if (exceptionalCase)
			super.state(object.getResolutionPercentage() == 100.00, "resolutionPercentage", "assistanceAgent.tracking-log.form.error.must-be-100");
	}

	@Override
	public void perform(final TrackingLog object) {
		object.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;

		Boolean exceptionalCase;
		Claim claim;

		claim = object.getClaim();
		exceptionalCase = this.repository.countTrackingLogsForExceptionalCase(claim.getId()) == 1;

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", AssistanceAgentTrackingLogCreateExceptionalCaseService.RESOLUTION, "indicator");
		dataset.put(AssistanceAgentTrackingLogCreateExceptionalCaseService.MASTER_ID, super.getRequest().getData(AssistanceAgentTrackingLogCreateExceptionalCaseService.MASTER_ID, int.class));
		dataset.put("exceptionalCase", exceptionalCase);
		dataset.put("indicators", SelectChoices.from(Indicator.class, object.getIndicator()));

		super.getResponse().addData(dataset);
	}
}
