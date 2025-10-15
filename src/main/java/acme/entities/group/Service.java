
package acme.entities.group;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "name"), @Index(columnList = "averageDwellTime")
})
public class Service extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				picture;

	@Mandatory
	@ValidNumber(min = 0.01, max = 24.00)
	@Automapped
	private Double				averageDwellTime; //timepo promedio q se pasa en un servicio

	@Optional
	@ValidString(pattern = "^[A-Z]{4}-[0-9]{2}$")
	@Automapped
	private String				promotionCode; //los ultimos dos digitos corresponden al año actusl

	@Optional
	@ValidMoney(min = 0, max = 1000)
	@Automapped
	private Money				discountAmount; //cantidad de dinero descontada

}
