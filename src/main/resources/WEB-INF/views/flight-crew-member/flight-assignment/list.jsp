<%--
- list.jsp for FlightAssignment
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.flightCrewDuty" path="flightCrewDuty"/>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate"/>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.assignmentStatus" path="assignmentStatus"/>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.remarks" path="remarks"/>
</acme:list>
