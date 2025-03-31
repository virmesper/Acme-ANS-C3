<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="acme" uri="http://acme-framework.org/" %>

<acme:list>
    <acme:list-column code="flightcrewmember.activity-log.list.label.duty" path="duty" width="25%"/>
    <acme:list-column code="flightcrewmember.activity-log.list.label.startTime" path="startTime" width="25%"/>
    <acme:list-column code="flightcrewmember.activity-log.list.label.endTime" path="endTime" width="25%"/>
    <acme:list-column code="flightcrewmember.activity-log.list.label.extraInfo" path="extraInfo" width="25%"/>
</acme:list>
