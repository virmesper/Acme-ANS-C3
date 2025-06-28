
package acme.entities.S3;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {
		"passportCountry", "destinationCountry"
	})
})
public class VisaRequirement extends AbstractEntity {

	/*
	 * Travel Buddy API (https://travel-buddy.ai/api/) - 600 peticiones mensuales gratuitas
	 */

	// Serialisation version

	private static final long	serialVersionUID	= 1L;

	// Attributes

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				passportCountry; // País del pasaporte

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				destinationCountry; // País de destino

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				continent;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				capital;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				currency;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$", message = "Número de teléfono inválido: Debe contener entre 6 y 15 dígitos y puede incluir un '+' opcional.")
	@Automapped
	private String				phoneCode; // por ejemplo: "+971"

	@Optional
	@ValidString(pattern = "^[+-]\\d{2}:\\d{2}$", message = "Zona horaria inválida: Debe seguir el formato ±HH:MM.")
	@Automapped
	private String				timezone; // por ejemplo: "+04:00"

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				visaType; // por ejemplo: "Visa required"

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				stayDuration; // duración de estancia permitida

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				passportValidity; // por ejemplo: "6 months"

	@Optional
	@ValidString(min = 1)
	@Automapped
	private String				additionalInfo; // Texto adicional de excepciones sobre los requisitos de visa

	@Optional
	@ValidUrl(remote = false)
	@Automapped
	private String				officialLink; // Enlace oficial para más detalles

	// Evitamos cadenas vacías no nulas, pues optional no las convierte a null


	public void setAdditionalInfo(String c) {
		// Si c está vacío o son solo espacios, lo pongo a null
		if (c != null && c.trim().isEmpty())
			c = null;
		this.additionalInfo = c;
	}

	public void setOfficialLink(String l) {
		// Si l está vacío o son solo espacios, lo pongo a null
		if (l != null && l.trim().isEmpty())
			l = null;
		this.officialLink = l;
	}
}
