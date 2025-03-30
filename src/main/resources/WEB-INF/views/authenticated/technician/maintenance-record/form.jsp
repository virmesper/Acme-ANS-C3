<%@page %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

    <!-- Campos del MaintenanceRecord -->
    <acme:input-moment code="technician.maintenance-record.form.label.maintenanceMoment" path="maintenanceMoment" readonly="true"/>
    <acme:input-select code="technician.maintenance-record.form.label.status" path="status" choices="${statusChoices}"/>
    <acme:input-moment code="technician.maintenance-record.form.label.nextInspectionDate" path="nextInspectionDate"/>
    <acme:input-money code="technician.maintenance-record.form.label.estimatedCost" path="estimatedCost"/>
    <acme:input-textarea code="technician.maintenance-record.form.label.notes" path="notes"/>

    <!-- Botones de acción -->
    <c:choose>
        <c:when test="${_command == 'create'}">
            <acme:submit code="technician.maintenance-record.form.button.create" action="/authenticated/technician/maintenance-record/create"/>
        </c:when>
        <c:when test="${_command == 'update'}">
            <acme:submit code="technician.maintenance-record.form.button.update" action="/authenticated/technician/maintenance-record/update"/>
            <acme:submit code="technician.maintenance-record.form.button.publish" action="/authenticated/technician/maintenance-record/publish"/>
        </c:when>
    </c:choose>

    <!-- Sección de tareas asociadas -->
    <c:if test="${not empty tasks}">
        <h4>Tareas asociadas</h4>
        <table class="table">
            <thead>
                <tr>
                    <th>Tipo</th>
                    <th>Descripción</th>
                    <th>Prioridad</th>
                    <th>Duración estimada</th>
                    <th>Publicado</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${tasks}" var="task">
                    <tr>
                        <td><c:out value="${task.type}"/></td>
                        <td><c:out value="${task.description}"/></td>
                        <td><c:out value="${task.priority}"/></td>
                        <td><c:out value="${task.estimatedDuration}"/></td>
                        <td><c:out value="${task.published}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</acme:form>
