
package acme.features.administrator.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.entities.S4.TrackingLog;

@Service
public class AdministratorTrackingLogListService extends AbstractGuiService<Administrator, TrackingLog> {

	// Internal staTrackingLog-------------------------------------------------------

	@Autowired
	private AdministratorTrackingLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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

		dataset = super.unbindObject(object, "lastUpdateMoment", "resolutionPercentage", "indicator");
		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);

		super.getResponse().addData(dataset);
	}

}
