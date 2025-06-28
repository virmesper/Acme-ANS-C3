<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<div class="container my-4">

    <!-- Assignment Statistics -->
    <div class="card mb-4">
        <div class="card-header">
            <h2>
                <acme:print code="flight-crew-member.dashboard.title.assignment-stats"/>
            </h2>
        </div>
        <div class="card-body">
            <table class="table table-sm table-bordered">
                <tr>
                    <th><acme:print code="flight-crew-member.dashboard.label.average"/></th>
                    <td><acme:print value="${average}"/></td>
                </tr>
                <tr>
                    <th><acme:print code="flight-crew-member.dashboard.label.minimum"/></th>
                    <td><acme:print value="${minimum}"/></td>
                </tr>
                <tr>
                    <th><acme:print code="flight-crew-member.dashboard.label.maximum"/></th>
                    <td><acme:print value="${maximum}"/></td>
                </tr>
                <tr>
                    <th><acme:print code="flight-crew-member.dashboard.label.standard-deviation"/></th>
                    <td><acme:print value="${standardDesviation}"/></td>
                </tr>
            </table>
        </div>
    </div>

    <!-- Activity Severity -->
    <div class="card mb-4">
        <div class="card-header">
            <h2>
                <acme:print code="flight-crew-member.dashboard.title.activity-severity"/>
            </h2>
        </div>
        <div class="card-body">
            <table class="table table-sm table-striped">
                <thead>
                    <tr>
                        <th><acme:print code="flight-crew-member.dashboard.label.severity-range"/></th>
                        <th><acme:print code="flight-crew-member.dashboard.label.count"/></th>
                    </tr>
                </thead>
                <tbody>
                    <jstl:forEach var="entry" items="${activityLogCounts}">
                        <tr>
                            <td><jstl:out value="${entry.key}"/></td>
                            <td><jstl:out value="${entry.value}"/></td>
                        </tr>
                    </jstl:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Last Destinations -->
    <div class="card mb-4">
        <div class="card-header">
            <h2>
                <acme:print code="flight-crew-member.dashboard.title.last-destinations"/>
            </h2>
        </div>
        <div class="card-body">
            <ul class="list-group">
                <jstl:forEach var="destination" items="${lastFiveDestinations}">
                    <li class="list-group-item">
                        <jstl:out value="${destination}"/>
                    </li>
                </jstl:forEach>
            </ul>
        </div>
    </div>

    <!-- Colleagues in Last Stage -->
    <div class="card mb-4">
        <div class="card-header">
            <h2>
                <acme:print code="flight-crew-member.dashboard.title.colleagues-last-stage"/>
            </h2>
        </div>
        <div class="card-body">
            <ul class="list-group">
                <jstl:forEach var="colleague" items="${colleaguesInLastStage}">
                    <li class="list-group-item">
                        <jstl:out value="${colleague.identity.fullName}"/> - 
                        <jstl:out value="${colleague.employeeCode}"/>
                    </li>
                </jstl:forEach>
            </ul>
        </div>
    </div>

    <!-- Assignments by Status -->
    <div class="card mb-4">
        <div class="card-header bg-light text-black">
            <h2 class="mb-0">
                <acme:print code="flight-crew-member.dashboard.title.assignments-by-status"/>
            </h2>
        </div>
        <div class="card-body">
            <jstl:forEach var="entry" items="${assignmentsByStatus}">
                <div class="mb-3">
                    <h3><jstl:out value="${entry.key}"/></h3>
                    <table class="table table-bordered table-sm">
                        <thead>
                            <tr>
                                <th>Flight Number</th>
                                <th>Duty Role</th>
                                <th>Current Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <jstl:forEach var="assignment" items="${entry.value}">
                                <tr>
                                    <td><jstl:out value="${assignment.leg.flightNumber}"/></td>
                                    <td><jstl:out value="${assignment.duty}"/></td>
                                    <td><jstl:out value="${assignment.currentStatus}"/></td>
                                </tr>
                            </jstl:forEach>
                        </tbody>
                    </table>
                </div>
            </jstl:forEach>
        </div>
    </div>

</div>

<acme:return/>
