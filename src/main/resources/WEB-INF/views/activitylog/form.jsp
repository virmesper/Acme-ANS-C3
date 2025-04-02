<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="activitylog.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="activitylog.form.label.incidentType" path="incidentType"/>
	<acme:input-textarea code="activitylog.form.label.description" path="description"/>
	<acme:input-integer code="activitylog.form.label.severityLevel" path="severityLevel" min="0" max="10"/>

	<jstl:choose>
		<jstl:when test="${_command == 'show'}">
			<acme:button code="activitylog.form.button.back" action="/flightcrewmembers/ActivityLog/list"/>
		</jstl:when>

		<jstl:when test="${_command == 'create'}">
			<acme:submit code="activitylog.form.button.create" action="/flightcrewmembers/ActivityLog/create"/>
		</jstl:when>

		<jstl:when test="${_command == 'update' && !published}">
			<acme:submit code="activitylog.form.button.update" action="/flightcrewmembers/ActivityLog/update"/>
			<acme:submit code="activitylog.form.button.delete" action="/flightcrewmembers/ActivityLog/delete"/>
			<acme:submit code="activitylog.form.button.publish" action="/flightcrewmembers/ActivityLog/publish"/>
		</jstl:when>
	</jstl:choose>
</acme:form>
