
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Claim;
import acme.entities.S4.Indicator;
import acme.entities.S4.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		AssistanceAgent assistanceAgent;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findOneClaimById(masterId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		status = claim != null && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog object;
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
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
		assert trackingLog != null;

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

		if (!super.getBuffer().getErrors().hasErrors("resolutionPercentage")) {
			Double maxResolutionPercentage;
			double finalMaxResolutionPercentage;

			// Manejo seguro del valor nulo devuelto por la consulta
			maxResolutionPercentage = this.repository.findMaxResolutionPercentageByClaimId(trackingLog.getId(), trackingLog.getClaim().getId());
			finalMaxResolutionPercentage = maxResolutionPercentage != null ? maxResolutionPercentage : 0.0;

			super.state(trackingLog.getResolutionPercentage() > finalMaxResolutionPercentage, "resolutionPercentage", "assistanceAgent.tracking-log.form.error.less-than-max-resolution-percentage");
		}

		if (!super.getBuffer().getErrors().hasErrors("resolution")) {
			boolean bool1;

			// Verificación segura de resolución no nula y no vacía
			bool1 = trackingLog.getIndicator() != Indicator.PENDING && trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();

			super.state(bool1 || trackingLog.getIndicator() == Indicator.PENDING, "resolution", "assistanceAgent.tracking-log.form.error.resolution-not-null");
		}
	}
	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;

		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "indicator");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("indicators", indicatorChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
