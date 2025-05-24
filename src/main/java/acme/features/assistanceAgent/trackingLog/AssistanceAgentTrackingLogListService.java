
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Claim;
import acme.entities.S4.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		AssistanceAgent assistanceAgent;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findOneClaimById(masterId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		status = claim != null && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> objects;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		objects = this.repository.findManyTrackingLogsClaimId(masterId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final TrackingLog object) {
		String published;
		Dataset dataset;

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "indicator");
		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> object) {
		int masterId;
		Claim claim;
		Boolean noMore;
		Boolean notCreateButton;
		Boolean exceptionalCase;
		Boolean greatRealm;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findOneClaimById(masterId);

		exceptionalCase = !claim.isDraftMode() && this.repository.countTrackingLogsForExceptionalCase(masterId) == 1;
		notCreateButton = claim.isDraftMode() && this.repository.countTrackingLogsForExceptionalCaseNotDraftMode(masterId) == 1;
		noMore = !claim.isDraftMode() && this.repository.countTrackingLogsForExceptionalCaseNotDraftMode(masterId) == 2;

		greatRealm = super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("exceptionalCase", exceptionalCase);
		super.getResponse().addGlobal("notCreateButton", notCreateButton);
		super.getResponse().addGlobal("noMore", noMore);
		super.getResponse().addGlobal("greatRealm", greatRealm);

	}

}
