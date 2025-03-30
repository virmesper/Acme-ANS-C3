<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="authenticated.passenger.list.label.fullName" path="fullName" width="25%"/>
	<acme:list-column code="authenticated.passenger.list.label.email" path="email" width="25%"/>
	<acme:list-column code="authenticated.passenger.list.label.passportNumber" path="passportNumber" width="25%"/>
	<acme:list-column code="authenticated.passenger.list.label.draftMode" path="draftMode" width="10%"/>
</acme:list>

<acme:button code="authenticated.passenger.form.button.create" action="/authenticated/passenger/create"/>
