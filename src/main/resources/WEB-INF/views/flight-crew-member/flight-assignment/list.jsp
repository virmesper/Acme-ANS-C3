<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column path="flightCrew" code="flightcrewmembers.list.label.flightCrew" width="20%"/>
	<acme:list-column path="moment" code="flightcrewmembers.list.label.moment" width="30%"/>
	<acme:list-column path="currentStatus" code="flightcrewmembers.list.label.currentStatus" width="30%"/>
	<acme:list-column path="remarks" code="flightcrewmembers.list.label.remarks" width="20%"/>
</acme:list>


<acme:list>
	<acme:list-column path="flightCrew" code="flightcrewmembers.list.label.flightCrew" width="20%"/>
	<acme:list-column path="moment" code="flightcrewmembers.list.label.moment" width="30%"/>
	<acme:list-column path="currentStatus" code="flightcrewmembers.list.label.currentStatus" width="30%"/>
	<acme:list-column path="remarks" code="flightcrewmembers.list.label.remarks" width="20%"/>
</acme:list>

<acme:button code="flightcrewmembers.list.button.create" action="/flightcrewmembers/create?masterId=${masterId}"/>