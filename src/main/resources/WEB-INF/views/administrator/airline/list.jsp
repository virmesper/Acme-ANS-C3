<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airline.list.label.name" path="name" width="15%"/>
	<acme:list-column code="administrator.airline.list.label.iata" path="iataCode" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.web" path="website" width="15%"/>
	<acme:list-column code="administrator.airline.list.label.type" path="type" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.foundationMoment" path="foundationMoment" width="15%"/>
	<acme:list-column code="administrator.airline.list.label.mail" path="email" width="15%"/>
	<acme:list-column code="administrator.airline.list.label.phone" path="phoneNumber" width="10%"/>
	<acme:list-column code="administrator.airline.list.label.airport" path="airport" width="10%"/>
</acme:list>


<acme:button code="administrator.airline.list.button.create" action="/administrator/airline/create"/> 