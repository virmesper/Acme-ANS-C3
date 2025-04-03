<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.aircraft.list.label.model" path="model" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.registrationnumber" path="registrationnumber" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.capacity" path="capacity" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.cargoweight" path="cargoweight" width="20%"/>
	<acme:list-column code="administrator.aircraft.list.label.status" path="status" width="20%"/>
	<acme:list-payload path="payload"/>	
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="administrator.aicraft.list.button.create" action="/administrator/aircraft/create"/>
</jstl:if>