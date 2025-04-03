<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<jstl:if test="${_command == 'list-uncompleted'}">
	<acme:list>
		<acme:list-column
			code="flight-crew-member.flight-assignment-uncompleted.list-uncompleted.label.duty"
			path="duty" width="20%" />
		<acme:list-column
			code="flight-crew-member.flight-assignment-uncompleted.list-uncompleted.label.lastUpdate"
			path="moment" width="20%" />
		<acme:list-column
			code="flight-crew-member.flight-assignment-uncompleted.list-uncompleted.label.assignmentStatus"
			path="currentStatus" width="20%" />
		<acme:list-payload path="payload" />
	</acme:list>
	<acme:button
		code="flight-crew-member.flight-assignment-uncompleted.list.button.create"
		action="/flight-crew-member/flight-assignment/create" />
</jstl:if>

<jstl:if test="${_command == 'list'}">

	<acme:list>
		<acme:list-column
			code="flight-crew-member.assignments.list.label.duty" path="duty"
			width="10%" />
		<acme:list-column
			code="flight-crew-member.assignments.list.label.moment" path="moment"
			width="10%" />
		<acme:list-column
			code="flight-crew-member.assignments.list.label.status"
			path="currentStatus" width="10%" />
		<acme:list-column
			code="flight-crew-member.assignments.list.label.scheduledArrival"
			path="leg.scheduledArrival" width="10%" />
		<acme:list-payload path="payload" />
	</acme:list>

	<acme:button code="flight-crew-member.assignments.list.button.create"
		action="/flight-crew-member/flight-assignment/create" />

</jstl:if>
