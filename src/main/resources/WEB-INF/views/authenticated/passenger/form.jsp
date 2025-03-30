<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

    <acme:input-textarea code="authenticated.passenger.list.label.fullName" path="fullName"/>
    <acme:input-textarea code="authenticated.passenger.list.label.email" path="email"/>
    <acme:input-textarea code="authenticated.passenger.list.label.passportNumber" path="passportNumber"/>
    <acme:input-moment code="authenticated.passenger.list.label.dateOfBirth" path="dateOfBirth"/>
    <acme:input-textarea code="authenticated.passenger.list.label.specialNeeds" path="specialNeeds"/>
    <acme:input-textarea code="authenticated.passenger.list.label.draftMode" path="draftMode" readonly="true"/>

    <jstl:choose>
        <jstl:when test="${_command == 'update' && draftMode == true}">
            <acme:submit code="authenticated.passenger.form.button.update" action="/authenticated/passenger/update"/>
        </jstl:when>
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="authenticated.passenger.form.button.create" action="/authenticated/passenger/create"/>
        </jstl:when>
    </jstl:choose>

</acme:form>
