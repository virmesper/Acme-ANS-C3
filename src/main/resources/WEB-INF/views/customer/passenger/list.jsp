<%@page %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<h2>List of passengers</h2>

<acme:list>
    <acme:list-column code="customer.passenger.list.label.fullName" path="fullName" width="30%"/>
    <acme:list-column code="customer.passenger.list.label.passportNumber" path="passportNumber" width="20%" />
    <acme:list-column code="customer.passenger.list.label.specialNeeds" path="specialNeeds" width="20%" />
    <acme:list-column code="customer.passenger.list.label.email" path="email" width="20%" />
    <acme:list-payload path="payload"/>   
    
</acme:list>
<c:if test="${_command == 'list' && !draftMode}">
    <acme:button code="customer.booking-record.create" action="/customer/booking-record/create?bookingId=${bookingId}" />
</c:if>
<acme:button code="customer.passenger.form.button.create" action="/customer/passenger/create"/>