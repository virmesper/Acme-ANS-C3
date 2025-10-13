
package acme.entities.student2;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recommendation", indexes = {
	@Index(name = "idx_reco_city", columnList = "city"), @Index(name = "idx_reco_country", columnList = "country"), @Index(name = "idx_reco_category", columnList = "category"), @Index(name = "idx_reco_rating", columnList = "rating")
})
public class Recommendation extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				source;      // "opentripmap"
	@Mandatory
	@ValidString(min = 1, max = 100)
	@Automapped
	private String				externalId;  // id externo
	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				title;
	@Optional
	@ValidString(min = 0, max = 50)
	@Automapped
	private String				category;    // restaurant/activity/…
	@Optional
	@ValidString(min = 0, max = 100)
	@Automapped
	private String				city;
	@Optional
	@ValidString(min = 0, max = 100)
	@Automapped
	private String				country;
	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				shortDescription;
	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				url;
	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				imageUrl;
	@Optional
	@Automapped
	private Double				rating;
	@Optional
	@Automapped
	private Integer				priceLevel; // 0..4 si aplica
	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				lastUpdate;
}
