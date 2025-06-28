<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.assignment.list.completed.label.flightNumber" path="leg.flightNumber"/>
	<acme:list-column code="any.assignment.list.completed.label.duty" path="duty"/>
	<acme:list-column code="any.assignment.list.completed.label.departureAirport.name" path="leg.departureAirport.name"/>
	<acme:list-column code="any.assignment.list.completed.label.arrivalAirport.name" path="leg.arrivalAirport.name"/>
	<acme:list-payload path="payload"/>
</acme:list>