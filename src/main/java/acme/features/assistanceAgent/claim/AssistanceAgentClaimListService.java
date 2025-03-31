
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean isAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(isAgent);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int agentId;

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findAllClaimsByAssistanceAgentId(agentId);

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;

		String published;
		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "type", "indicator", "leg");

		published = !claim.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);

		super.getResponse().addData(dataset);
	}

}
