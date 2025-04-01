
package acme.features.assistanceAgent.claim;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Leg;
import acme.entities.S4.Claim;
import acme.entities.S4.ClaimType;
import acme.entities.S4.Indicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean isAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(isAgent);
	}

	@Override
	public void load() {
		Claim claim = new Claim();
		AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		int agentId = principal.getId();
		AssistanceAgent agent = this.repository.findAssistanceAgentById(agentId);
		Date today = MomentHelper.getCurrentMoment();

		claim.setAssistanceAgent(agent);
		claim.setRegistrationMoment(today);
		claim.setIndicator(Indicator.PENDING);
		claim.setDraftMode(true);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "indicator", "draftMode");
		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim object) {
		assert object != null;

		// Verificar que el indicador esté en estado PENDING al crear la reclamación
		if (object.getIndicator() != Indicator.PENDING)
			super.state(false, "indicator", "acme.validation.claim.indicator.pending");
	}

	@Override
	public void perform(final Claim object) {
		assert object != null;

		object.setRegistrationMoment(MomentHelper.getCurrentMoment());

		this.repository.save(object);
	}

	@Override
	public void unbind(final Claim object) {
		Dataset dataset;
		SelectChoices claimTypeChoices = SelectChoices.from(ClaimType.class, object.getType());
		SelectChoices indicatorChoices = SelectChoices.from(Indicator.class, object.getIndicator());
		SelectChoices legChoices = SelectChoices.from(this.repository.findAvailableLegs(), "flightNumber", object.getLeg());
		SelectChoices draftModeChoices = new SelectChoices();

		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "description", "draftMode", "leg", "indicator", "type");
		dataset.put("types", claimTypeChoices);
		dataset.put("indicators", indicatorChoices);
		dataset.put("draftModes", draftModeChoices);
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
