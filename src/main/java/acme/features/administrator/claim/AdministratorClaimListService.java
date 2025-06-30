
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Claim;

@GuiService
public class AdministratorClaimListService extends AbstractGuiService<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	private final AdministratorClaimRepository repository;


	@Autowired
	public AdministratorClaimListService(final AdministratorClaimRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------

	@Override
	public void authorise() {
		super.getResponse().setAuthorised("GET".equals(super.getRequest().getMethod()));

	}

	@Override
	public void load() {
		Collection<Claim> objects;
		objects = this.repository.findAllPublishedClaims();
		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Claim object) {
		String published;
		Dataset dataset;

		dataset = super.unbindObject(object, "type", "indicator");
		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);
		dataset.put("leg", object.getLeg().getFlightNumber());
		dataset.put("assistanceAgent", object.getAssistanceAgent().getEmployeeCode());

		super.getResponse().addData(dataset);

	}

}
