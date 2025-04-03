<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-moment code="flight-crew-member.activity-log.form.label.registrationMoment" path="registrationMoment" readonly ="true"/>
	<acme:input-textbox code="flight-crew-member.activity-log.list.label.typeOfIncident" path="typeOfIncident"/>	
	<acme:input-textbox code="flight-crew-member.activity-log.form.label.description" path="description"/>	
	<acme:input-integer code="flight-crew-member.activity-log.form.label.severityLevel" path="severityLevel"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete') && draftMode == true}">
			<acme:submit code="flight-crew-member.activity-log.form.button.update" action="/flight-crew-member/activity-log/update"/>
			<acme:submit code="flight-crew-member.activity-log.form.button.publish" action="/flight-crew-member/activity-log/publish"/>
			<acme:submit code="flight-crew-member.activity-log.form.button.delete" action="/flight-crew-member/activity-log/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
		<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.confirmation" path="confirmation"/>
			<acme:submit code="flight-crew-member.activity-log.form.button.create" action="/flight-crew-member/activity-log/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>