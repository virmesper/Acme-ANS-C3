
package acme.features.assistance_agent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student1.Leg;
import acme.entities.student4.Claim;
import acme.entities.student4.ClaimType;
import acme.entities.student4.Indicator;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentClaimRepository repository;


	@Autowired
	public AssistanceAgentClaimPublishService(final AssistanceAgentClaimRepository repository) {
		this.repository = repository;
	}

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
		// intentionally left blank to prevent post hacking
	}

	@Override
	public void validate(final Claim claim) {
		boolean valid;
		if (claim.getLeg() != null && claim.getRegistrationMoment() != null) {
			valid = MomentHelper.getCurrentMoment().after(claim.getLeg().getScheduledArrival());
			super.state(valid, "leg", "assistanceAgent.claim.form.error.badLeg");
		}
	}

	@Override
	public void perform(final Claim claim) {
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

}
