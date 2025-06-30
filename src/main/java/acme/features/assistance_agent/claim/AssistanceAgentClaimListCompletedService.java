
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Claim;
import acme.entities.student4.Indicator;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimListCompletedService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentClaimRepository repository;


	@Autowired
	public AssistanceAgentClaimListCompletedService(final AssistanceAgentClaimRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------

	@Override
	public void authorise() {
		AssistanceAgent assistanceAgent;
		boolean status;

		assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		status = super.getRequest().getPrincipal().hasRealm(assistanceAgent);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int masterId;
		masterId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findManyClaimsCompletedByMasterId(masterId, Indicator.PENDING);

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim object) {
		String published;
		Dataset dataset;

		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "type", "indicator");
		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);
		dataset.put("leg", object.getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}
}
