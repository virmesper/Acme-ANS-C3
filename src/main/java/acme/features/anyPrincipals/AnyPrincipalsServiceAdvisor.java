
package acme.features.anyPrincipals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import acme.entities.group.Service;

@ControllerAdvice
public class AnyPrincipalsServiceAdvisor {

	@Autowired
	private AnyPrincipalsServiceRepository repository;


	@ModelAttribute("service")
	public Service getService() {
		try {
			return this.repository.findRandomService(); // ✅ ahora seguro
		} catch (final Throwable oops) {
			return null;
		}
	}
}
