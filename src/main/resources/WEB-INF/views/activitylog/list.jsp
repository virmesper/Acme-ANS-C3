<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="activitylog.list.label.registrationMoment" path="registrationMoment"/>
    <acme:list-column code="activitylog.list.label.incidentType" path="incidentType"/>
    <acme:list-column code="activitylog.list.label.description" path="description"/>
    <acme:list-column code="activitylog.list.label.severityLevel" path="severityLevel"/>
</acme:list>
