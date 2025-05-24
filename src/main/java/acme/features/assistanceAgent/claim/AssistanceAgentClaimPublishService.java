
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Leg;
import acme.entities.S4.Claim;
import acme.entities.S4.ClaimType;
import acme.entities.S4.Indicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		int legId;
		Leg leg;
		boolean externalRelation = true;

		if (super.getRequest().getMethod().equals("POST")) {
			legId = super.getRequest().getData("leg", int.class);
			leg = this.repository.findLegById(legId);

			boolean isLegIdZero = legId == 0;
			boolean isLegValid = leg != null;
			boolean isLegNotDraft = isLegValid && !leg.isDraftMode();
			boolean isFlightNotDraft = isLegNotDraft && !leg.getFlight().getDraftMode();

			externalRelation = isLegIdZero || isLegValid && isLegNotDraft && isFlightNotDraft;
		}

		masterId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(masterId);
		status = claim != null && claim.isDraftMode() && externalRelation && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int masterId;

		masterId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(masterId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;

		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(claim, "registrationMoment", "passengerEmail", "description", "leg", "indicator", "type");
		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim claim) {
		super.state(this.repository.allTrackingLogsPublishedByClaimId(claim.getId()), "*", "assistanceAgent.claim.form.error.all-tracking-logs-published");

		if (!super.getBuffer().getErrors().hasErrors("indicator")) {
			boolean bool1;
			boolean bool2;

			// Obtener el porcentaje de resolución de manera segura
			Double maxPercentage = this.repository.findMaxResolutionPercentageByClaimId(claim.getId());

			// Si el valor es nulo, considerar que el porcentaje es 0
			maxPercentage = maxPercentage != null ? maxPercentage : 0.0;

			bool1 = claim.getIndicator() == Indicator.PENDING && maxPercentage < 100;
			bool2 = claim.getIndicator() != Indicator.PENDING && maxPercentage == 100;

			super.state(bool1 || bool2, "indicator", "assistanceAgent.claim.form.error.indicator-pending");
		}
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		claim.setDraftMode(false);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		SelectChoices claimTypeChoices = SelectChoices.from(ClaimType.class, claim.getType());
		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, claim.getIndicator());
		SelectChoices legChoices = SelectChoices.from(this.repository.findAllLegs(), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "draftMode", "leg", "indicator", "type");
		dataset.put("types", claimTypeChoices);
		dataset.put("indicators", indicatorChoices);
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
