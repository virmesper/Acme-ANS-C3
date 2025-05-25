
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
public class AssistanceAgentTrackingLogCreateExceptionalCaseService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		AssistanceAgent assistanceAgent;
		Claim claim;
		Boolean exceptionalCase;

		masterId = super.getRequest().getData("masterId", int.class);
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

		masterId = super.getRequest().getData("masterId", int.class);
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
		super.bindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "indicator");
	}

	@Override
	public void validate(final TrackingLog object) {
		if (!super.getBuffer().getErrors().hasErrors("resolution"))
			super.state(Optional.ofNullable(object.getResolution()).map(String::strip).filter(s -> !s.isEmpty()).isPresent(), "resolution", "assistanceAgent.tracking-log.form.error.resolution-not-null");
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

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "indicator");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("exceptionalCase", exceptionalCase);
		dataset.put("indicators", SelectChoices.from(Indicator.class, object.getIndicator()));

		super.getResponse().addData(dataset);
	}
}
