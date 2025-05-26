
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractService;
import acme.entities.S1.Leg;
import acme.entities.S4.Claim;
import acme.entities.S4.ClaimType;
import acme.entities.S4.Indicator;

@Service
public class AdministratorClaimShowService extends AbstractService<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorClaimRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int ClaimId;
		Claim Claim;

		ClaimId = super.getRequest().getData("id", int.class);
		Claim = this.repository.findOneClaimById(ClaimId);
		status = Claim != null && !Claim.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim object;
		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneClaimById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final Claim object) {
		Dataset dataset;

		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices choicesIndicator;
		SelectChoices choicesType;

		legs = this.repository.findAllLegs();

		choices = SelectChoices.from(legs, "flightNumber", object.getLeg());
		choicesType = SelectChoices.from(ClaimType.class, object.getType());
		choicesIndicator = SelectChoices.from(Indicator.class, object.getIndicator());

		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "description", "draftMode", "leg", "indicator", "type");
		dataset.put("legs", choices);
		dataset.put("indicators", choicesIndicator);
		dataset.put("types", choicesType);

		dataset.put("id", object.getId());

		super.getResponse().addData(dataset);
	}

}
