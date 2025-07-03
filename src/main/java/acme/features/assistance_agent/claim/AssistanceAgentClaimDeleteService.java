
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Claim;
import acme.entities.student4.ClaimType;
import acme.entities.student4.Indicator;
import acme.entities.student4.TrackingLog;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentClaimRepository repository;


	@Autowired
	public AssistanceAgentClaimDeleteService(final AssistanceAgentClaimRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------

	@Override
	public void authorise() {
		int masterId;
		Claim claim;
		AssistanceAgent assistanceAgent;

		masterId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(masterId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		boolean status = false;

		if (claim != null)
			if (claim.isDraftMode())
				if (super.getRequest().getPrincipal().hasRealm(assistanceAgent))
					status = true;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId;
		Claim object;

		masterId = super.getRequest().getData("id", int.class);
		object = this.repository.findClaimById(masterId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Claim claim) {
		// intentionally left blank to prevent post hacking
	}

	@Override
	public void validate(final Claim claim) {
		// Intentionally left blank
	}

	@Override
	public void perform(final Claim claim) {
		Collection<TrackingLog> trackingLogs;

		trackingLogs = this.repository.findManyTrackingLogsByClaimId(claim.getId());
		this.repository.deleteAll(trackingLogs);
		this.repository.delete(claim);
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
