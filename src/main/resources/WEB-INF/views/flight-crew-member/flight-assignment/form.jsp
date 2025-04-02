<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select path="crewMember" code="flight-crew-member.flight-assignment.form.label.crewMember" choices="${crewMembers}"/>
	<acme:input-select path="leg" code="flight-crew-member.flight-assignment.form.label.leg" choices="${legs}"/>
	<acme:input-moment code="flight-crew-member.flight-assignment.form.label.moment" path="moment"/>
	<acme:input-select path="duty" code="flight-crew-member.flight-assignment.form.label.duty" choices="${duties}"  />
	<acme:input-select path="currentStatus" code="flight-crew-member.flight-assignment.form.label.status" choices="${statuses}"  />
	<acme:input-textarea code="flight-crew-member.flight-assignment.form.label.remarks" path="remarks"/>
	
	<jstl:choose>
	<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
	</jstl:when>
	<jstl:when test="${_command == 'create'}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>
	</jstl:choose>
	
</acme:form>