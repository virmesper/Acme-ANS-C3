
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.principals.UserAccount;
import acme.client.helpers.PrincipalHelper;
import acme.client.helpers.RandomHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.Customer;

@GuiService
public class AuthenticatedCustomerCreateService extends AbstractGuiService<Authenticated, Customer> {

	// Constants ---------------------------------------------------------------

	private static final String				IDENTIFIER_FIELD	= "identifier";

	// Internal state ----------------------------------------------------------

	@Autowired
	private AuthenticatedCustomerRepository	repository;

	// AbstractService<Authenticated, Customer> -------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = !super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Customer();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Customer object) {
		assert object != null;

		super.bindObject(object, AuthenticatedCustomerCreateService.IDENTIFIER_FIELD, "phoneNumber", "address", "city", "country", "earnedPoints");
	}

	@Override
	public void validate(final Customer object) {
		assert object != null;

		Collection<Customer> customers = this.repository.findAllCustomers();
		Collection<String> customerIds = customers.stream().map(Customer::getIdentifier).toList();
		String identifier = object.getIdentifier();

		if (identifier != null && identifier.length() >= 2) {
			super.state(!customerIds.contains(identifier), AuthenticatedCustomerCreateService.IDENTIFIER_FIELD, "authenticated.customer.create.not-unique-identifier");

			DefaultUserIdentity dui = this.repository.findUserAccountById(super.getRequest().getPrincipal().getAccountId()).getIdentity();
			boolean identifierBegin = identifier.charAt(0) == dui.getName().charAt(0) && identifier.charAt(1) == dui.getSurname().charAt(0);

			super.state(identifierBegin, AuthenticatedCustomerCreateService.IDENTIFIER_FIELD, "authenticated.customer.create.not-initials-in-identifier");
		}
	}

	@Override
	public void perform(final Customer object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Customer object) {
		assert object != null;

		Dataset dataset;
		String identifierBegin;
		DefaultUserIdentity dui = this.repository.findUserAccountById(super.getRequest().getPrincipal().getAccountId()).getIdentity();
		identifierBegin = String.valueOf(dui.getName().charAt(0)) + String.valueOf(dui.getSurname().charAt(0));
		String identifier = "";

		dataset = super.unbindObject(object, "phoneNumber", "address", "city", "country", "earnedPoints");

		if (super.getRequest().hasData(AuthenticatedCustomerCreateService.IDENTIFIER_FIELD))
			identifier = super.getRequest().getData(AuthenticatedCustomerCreateService.IDENTIFIER_FIELD, String.class);
		else {
			boolean uniqueIdentifier = false;
			while (!uniqueIdentifier) {
				identifier = identifierBegin;
				for (int i = 0; i < 6; i++)
					identifier += RandomHelper.nextInt(10);
				uniqueIdentifier = this.repository.findCustomerByIdentifier(identifier) == null;
			}
		}

		dataset.put(AuthenticatedCustomerCreateService.IDENTIFIER_FIELD, identifier);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
