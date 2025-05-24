
package acme.features.administrator.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.entities.S4.Claim;

@Controller
public class AdministratorClaimController extends AbstractGuiController<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorClaimListService	listService;

	@Autowired
	private AdministratorClaimShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
