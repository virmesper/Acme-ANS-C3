<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	
		<acme:input-textarea code="authenticated.booking.list.label.locatorCode" path="locatorCode" />
		<acme:input-textarea code="authenticated.booking.list.label.purchaseMoment" path="purchaseMoment"/>
		<acme:input-select code="authenticated.booking.list.label.travelClass" path="travelClass" choices="${travelClass}"/>
		<acme:input-textarea code="authenticated.booking.list.label.price" path="price"/>
		<acme:input-textarea code="authenticated.booking.list.label.lastCardDigits" path="lastCardDigits"/>
		<acme:input-textarea code="authenticated.booking.list.label.passenger" path="passengers" />
		<acme:input-textarea code="authenticated.booking.list.label.draftMode" path="draftMode" />
	<jstl:choose>
			<jstl:when test="${(_command == 'update' || _command == 'show' || _command == 'publish') && draftMode == false}">
				<acme:submit code="authenticated.booking.form.button.update" action="/authenticated/booking/update"/>
				<acme:submit code="authenticated.booking.form.button.publish" action="/authenticated/booking/publish"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:submit code="authenticated.booking.form.button.create" action="/authenticated/booking/create"/>
			</jstl:when>		
	</jstl:choose>		
</acme:form>