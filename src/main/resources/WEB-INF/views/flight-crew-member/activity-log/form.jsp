<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${readonly}">
	<acme:input-moment
		code="flightCrewMember.activityLog.form.label.registrationMoment"
		path="registrationMoment" />
	<acme:input-textbox
		code="flightCrewMember.activityLog.form.label.typeOfIncident"
		path="typeOfIncident" />
	<acme:input-textbox
		code="flightCrewMember.activityLog.form.label.description"
		path="description" />
	<acme:input-integer
		code="flightCrewMember.activityLog.form.label.severityLevel"
		path="severityLevel" />
	<acme:input-textbox
		code="flightCrewMember.activityLog.form.label.flightCrewMember"
		path="flightCrewMember" />
	<jstl:choose>
		<jstl:when
			test="${acme:anyOf(_command, 'show|update|delete') && draftMode == true}">
			<acme:submit code="flightCrewMember.activityLog.form.button.update"
				action="/flight-crew-member/activity-log/update" />
			<acme:submit code="flightCrewMember.activityLog.form.button.delete"
				action="/flight-crew-member/activity-log/delete" />
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="flightCrewMember.activityLog.form.button.create"
				action="/flight-crew-member/activity-log/create" />
		</jstl:when>
	</jstl:choose>
</acme:form>