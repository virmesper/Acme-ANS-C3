

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.involved-in.list.label.ticker" path="taskTicker" width="30%"/>
	<acme:list-column code="technician.involved-in.list.label.type" path="taskType" width="20%"/>	
	<acme:list-column code="technician.involved-in.list.label.priority" path="taskPriority" width="20%"/>
	<acme:list-column code="technician.involved-in.list.label.technician" path="taskTechnician" width="30%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${draft}">
	<acme:button code="technician.involved-in.list.button.create" action="/technician/involved-in/create?masterId=${masterId}"/>
</jstl:if>