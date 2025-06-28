
package acme.features.administrator.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Indicator;
import acme.entities.S4.TrackingLog;

@GuiService
public class AdministratorTrackingLogShowService extends AbstractGuiService<Administrator, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorTrackingLogRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findOneTrackingLogById(trackingLogId);
		status = trackingLog != null && !trackingLog.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog object;
		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrackingLogById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;

		SelectChoices choicesIndicator;

		choicesIndicator = SelectChoices.from(Indicator.class, object.getIndicator());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "draftMode", "resolution", "indicator");
		dataset.put("indicators", choicesIndicator);

		super.getResponse().addData(dataset);
	}

}
