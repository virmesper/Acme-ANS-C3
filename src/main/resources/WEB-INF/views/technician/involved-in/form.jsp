

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="technician.involved-in.form.label.aircraft"
		path="aircraftRegistrationNumber" readonly="true"/>
	<acme:input-select code="technician.involved-in.form.label.task"
		path="task" choices="${tasks}" />
	<acme:hidden-data path="maintenanceRecordId"/>
	
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.involved-in.form.button.link"
				action="/technician/involved-in/create" />
		</jstl:when>
		<jstl:when test="${_command == 'delete'}">
			<acme:submit code="technician.involved-in.form.button.unlink"
				action="/technician/involved-in/delete" />
		</jstl:when>
	</jstl:choose>
</acme:form>

