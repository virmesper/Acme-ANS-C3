
package acme.features.administrator.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student4.Claim;

@GuiController
public class AdministratorClaimController extends AbstractGuiController<Administrator, Claim> {

	private final AdministratorClaimListService	listService;
	private final AdministratorClaimShowService	showService;


	@Autowired
	public AdministratorClaimController(final AdministratorClaimListService listService, final AdministratorClaimShowService showService) {
		this.listService = listService;
		this.showService = showService;
	}

	// Constructors -----------------------------------------------------------
	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);

	}
}
