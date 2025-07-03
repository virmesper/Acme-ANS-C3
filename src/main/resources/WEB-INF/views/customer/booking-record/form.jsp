<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

    
	<jstl:choose>		
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.bookingRecord.form.label.booking" path="booking" choices="${bookings}"/>
    		<acme:input-select code="customer.bookingRecord.form.label.passenger" path="passenger" choices="${passengers}"/>
			<input type="hidden" name="bookingId" value="${bookingId}" />
			<acme:submit code="customer.bookingRecord.form.button.create" action="/customer/booking-record/create"/>
		</jstl:when>		
	</jstl:choose>	
</acme:form>