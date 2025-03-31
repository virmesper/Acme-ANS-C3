<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

		<jstl:if test="${acme:anyOf(_command, 'show|update|publish')}">
			<acme:input-select code="customer.booking.list.label.flightId" path="flightId" choices="${flights}"/>	
		</jstl:if>
		<acme:input-textbox code="customer.booking.list.label.lastCardDigits" path="lastCardDigits" placeholder="max 4 numbers. Ej: 1234" />
		<acme:input-textbox code="customer.booking.list.label.locatorCode" path="locatorCode" placeholder="between 6-8 chars. Ej: ABC6789"/>
	    <acme:input-money code="customer.booking.list.label.price" path="price" />
	    <jstl:if test="${acme:anyOf(_command, 'show|update|publish')}">
			<acme:input-moment code="customer.booking.list.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
		</jstl:if>
		<jstl:if test="${_command == 'create'}">
			<acme:input-moment code="customer.booking.list.label.purchaseMoment" path="purchaseMoment"/>
		</jstl:if>
	    <acme:input-select code="customer.booking.list.label.travelClass" path="travelClass" choices= "${travelClasses}"/>
	    <acme:input-checkbox code="customer.booking.list.label.draftMode" path="draftMode" readonly="true"/>
	    	    


   	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !isDraftMode}">
			<acme:submit code="customer.booking.update.submit" action="/customer/booking/update"/>
			<acme:submit code="customer.booking.publish" action="/customer/booking/publish" />	
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking.list.label.flight" path="flight" choices="${flights}" />
			<acme:submit code="customer.booking.create.submit" action="/customer/booking/create"/>
		</jstl:when>	
	</jstl:choose>	

</acme:form>>