<%@page %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="technician.maintenance-record.list.label.maintenanceMoment" path="maintenanceMoment" width="20%"/>
    <acme:list-column code="technician.maintenance-record.list.label.status" path="status" width="15%"/>
    <acme:list-column code="technician.maintenance-record.list.label.nextInspectionDate" path="nextInspectionDate" width="20%"/>
    <acme:list-column code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" width="15%"/>
</acme:list>

<acme:button code="technician.maintenance-record.form.button.create" action="/authenticated/technician/maintenance-record/create"/>
