
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Claim;
import acme.entities.S4.Indicator;
import acme.entities.S4.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimListUndergoingService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

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
		claims = this.repository.findManyClaimsUndergoingByMasterId(masterId, Indicator.PENDING);

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim object) {
		assert object != null;

		String published;
		Dataset dataset;

		Collection<TrackingLog> tlogs;
		Indicator value;

		tlogs = this.repository.findManyTrackingLogsByClaimId(object.getId());
		value = tlogs.stream().map(t -> t.getIndicator()).filter(t -> t.equals(Indicator.ACCEPTED) || t.equals(Indicator.REJECTED)).findFirst().orElse(Indicator.PENDING);
		object.setIndicator(value);

		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "type", "indicator");

		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);
		dataset.put("leg", object.getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}
}
