<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="Title" path="title" />
	<acme:input-moment code="Registration Moment" path="registrationMoment" />
	<acme:input-textbox code="Body" path="body" />
	<acme:input-checkbox code="Draft Mode" path="draftMode" />

	<jstl:if test="${_command == 'create'}">
		<acme:submit code="flight-crew-member.activity-log.form.button.create" action="/flight-crew-member/activity-log/create" />
	</jstl:if>

	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
		<acme:submit code="flight-crew-member.activity-log.form.button.update" action="/flight-crew-member/activity-log/update" />
		<acme:submit code="flight-crew-member.activity-log.form.button.delete" action="/flight-crew-member/activity-log/delete" />
		<acme:submit code="flight-crew-member.activity-log.form.button.publish" action="/flight-crew-member/activity-log/publish" />
	</jstl:if>
</acme:form>
