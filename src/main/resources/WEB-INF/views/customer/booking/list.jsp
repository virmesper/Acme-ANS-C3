<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking.list.label.travelClass" path="travelClass" />
	<acme:list-column code="customer.booking.list.label.price" path="price" />
	<acme:list-column code="customer.booking.list.label.locatorCode" path="locatorCode" />
	<acme:list-column code="customer.booking.list.label.flightTag" path="flightTag" />
    <acme:list-payload path="payload"/>	
</acme:list>

<acme:button code="customer.booking.create" action="/customer/booking/create" />