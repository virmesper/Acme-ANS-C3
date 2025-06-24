

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.involved-in.list.label.maintenanceRecord" path="maintenanceRecord" width="50%"/>	
	<acme:list-column code="technician.involved-in.list.label.task" path="task" width="50%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="technician.involved-in.list.button.create" action="/technician/involved-in/create"/>
