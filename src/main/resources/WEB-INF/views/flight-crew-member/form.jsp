<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="employer.duty.form.label.title" path="title"/>
	<acme:input-textarea code="employer.duty.form.label.description" path="description"/>
	<acme:input-double code="employer.duty.form.label.workLoad" path="workLoad" placeholder="employer.duty.form.placeholder.workLoad"/>
	<acme:input-url code="employer.duty.form.label.moreInfo" path="moreInfo"/>
	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="flightcrewmembers.form.button.FlightAssignments" action="/flightcrewmembers/FlightAssignment/list?masterId=${id}"/>			
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:button code="flightcrewmembers.form.button.FlightAssignments" action="/flightcrewmembers/list?masterId=${id}"/>
			<acme:submit code="flightcrewmembers.form.button.update" action="/flightcrewmembers/update"/>
			<acme:submit code="flightcrewmembers.form.button.delete" action="/flightcrewmembers/delete"/>
			<acme:submit code="flightcrewmembers.form.button.publish" action="/flightcrewmembers/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="flightcrewmembers.form.button.create" action="/flightcrewmembers/create"/>
		</jstl:when>
	</jstl:choose>
	
</acme:form>