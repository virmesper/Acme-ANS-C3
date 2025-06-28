
package acme.features.flightCrewMember.visa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.VisaRequirement;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightCrewMemberVisaRequirementShowService extends AbstractGuiService<FlightCrewMember, VisaRequirement> {

	@Autowired
	protected FlightCrewMemberVisaRequirementRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;

		if (super.getRequest().hasData("id", int.class)) {
			int id = super.getRequest().getData("id", int.class);
			VisaRequirement vr = this.repository.findOneById(id);

			if (vr != null && super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class)) {
				int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
				List<String> countries = this.repository.findDestinationCountriesByCrewMemberId(crewMemberId);
				authorised = countries.contains(vr.getDestinationCountry());
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		// Se obtiene el id del VisaRequirement
		int requirementId = super.getRequest().getData("id", int.class);

		// Se busca en BD
		VisaRequirement vr = this.repository.findOneById(requirementId);

		// Metemos en el buffer
		super.getBuffer().addData(vr);
	}

	@Override
	public void unbind(final VisaRequirement vr) {
		Dataset dataset = super.unbindObject(vr, "passportCountry", "destinationCountry", "continent", "capital", "currency", "phoneCode", "timezone", "visaType", "stayDuration", "passportValidity", "additionalInfo", "officialLink");
		super.getResponse().addData(dataset);
	}
}
