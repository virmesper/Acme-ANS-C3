
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;
import java.util.Currency;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Airline;
import acme.entities.student3.AvailabilityStatus;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberUpdateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	@Autowired
	protected AuthenticatedFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		boolean isAuthorised = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		Integer airlineId = super.getRequest().getData("airlineId", int.class, null);
		if (airlineId != null && airlineId != 0) {
			Collection<Airline> availableAirlines = this.repository.findAllAirlines();
			boolean airlineIsAvailable = availableAirlines.stream().anyMatch(a -> a.getId() == airlineId);
			isAuthorised = isAuthorised && airlineIsAvailable;
		}

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		FlightCrewMember member = this.repository.findOneFlightCrewMemberByUserAccountId(userAccountId);
		super.getBuffer().addData(member);
	}

	@Override
	public void bind(final FlightCrewMember member) {
		super.bindObject(member, "employeeCode", "phoneNumber", "languageSkills", "salary", "yearsOfExperience", "availabilityStatus");

		int airlineId = super.getRequest().getData("airlineId", int.class);
		Airline airline = this.repository.findAirlineById(airlineId);
		member.setAirline(airline);
	}

	@Override
	public void validate(final FlightCrewMember member) {
		// Validar código duplicado sólo si ha cambiado
		FlightCrewMember original = this.repository.findOneFlightCrewMemberByUserAccountId(member.getUserAccount().getId());
		if (!member.getEmployeeCode().equals(original.getEmployeeCode())) {
			boolean duplicateCode = this.repository.existsByEmployeeCode(member.getEmployeeCode());
			super.state(!duplicateCode, "employeeCode", "authenticated.flight-crew-member.error.duplicate-code");
		}

		super.state(member.getAirline() != null, "airlineId", "authenticated.flight-crew-member.error.null-airline");

		// Validar currency del salary

		if (member.getSalary() != null)
			try {
				Currency.getInstance(member.getSalary().getCurrency());
			} catch (IllegalArgumentException ex) {
				super.state(false, "salary", "administrator.service.error.invalid-currency");
			}

	}

	@Override
	public void perform(final FlightCrewMember member) {
		this.repository.save(member);
	}

	@Override
	public void unbind(final FlightCrewMember member) {
		Dataset dataset = super.unbindObject(member, "employeeCode", "phoneNumber", "languageSkills", "salary", "yearsOfExperience", "availabilityStatus");

		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices choicesAirlines = SelectChoices.from(airlines, "iataCode", member.getAirline());
		SelectChoices choicesStatus = SelectChoices.from(AvailabilityStatus.class, member.getAvailabilityStatus());

		dataset.put("airlines", choicesAirlines);
		dataset.put("airlineId", choicesAirlines.getSelected().getKey());
		dataset.put("availabilityStatuses", choicesStatus);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
