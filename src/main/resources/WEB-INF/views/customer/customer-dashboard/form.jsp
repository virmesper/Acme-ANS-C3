<%@page %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<div class="container my-4">

  <div class="card mb-4 shadow-sm">
    <div class="card-header">
      <h3 class="mb-0">
        <acme:print code="customer.customer-dashboard.section.last-destinations"/>
      </h3>
    </div>
    <div class="card-body">
      <jstl:choose>
        <jstl:when test="${empty lastFiveDestinations}">
          <p class="text-muted"><acme:print code="customer.customer-dashboard.label.no-data"/></p>
        </jstl:when>
        <jstl:otherwise>
          <ul class="list-group list-group-flush">
            <jstl:forEach var="d" items="${lastFiveDestinations}">
              <li class="list-group-item"><jstl:out value="${d}"/></li>
            </jstl:forEach>
          </ul>
        </jstl:otherwise>
      </jstl:choose>
    </div>
  </div>

  <!-- Gasto último año -->
  <div class="card mb-4 shadow-sm">
    <div class="card-header">
      <h3 class="mb-0">
        <acme:print code="customer.customer-dashboard.section.spent-last-year"/>
      </h3>
    </div>
    <div class="card-body">
      <table class="table table-sm table-bordered mb-0">
        <tr>
          <th class="w-50"><acme:print code="customer.customer-dashboard.label.amount"/></th>
          <td class="text-end"><acme:print value="${moneySpentLastYear}"/></td>
        </tr>
      </table>
      <small class="text-muted">
        <acme:print code="customer.customer-dashboard.hint.currency-note"/>
      </small>
    </div>
  </div>

  <!-- Reservas por clase -->
  <div class="card mb-4 shadow-sm">
    <div class="card-header">
      <h3 class="mb-0">
        <acme:print code="customer.customer-dashboard.section.by-class"/>
      </h3>
    </div>
    <div class="card-body">
      <table class="table table-sm table-striped mb-0">
        <thead>
          <tr>
            <th><acme:print code="customer.customer-dashboard.label.travel-class"/></th>
            <th class="text-end"><acme:print code="customer.customer-dashboard.label.count"/></th>
          </tr>
        </thead>
        <tbody>
          <jstl:choose>
            <jstl:when test="${empty bookingsByTravelClass}">
              <tr>
                <td colspan="2" class="text-muted">
                  <acme:print code="customer.customer-dashboard.label.no-data"/>
                </td>
              </tr>
            </jstl:when>
            <jstl:otherwise>
              <jstl:forEach var="e" items="${bookingsByTravelClass}">
                <tr>
                  <td><jstl:out value="${e.key}"/></td>
                  <td class="text-end"><jstl:out value="${e.value}"/></td>
                </tr>
              </jstl:forEach>
            </jstl:otherwise>
          </jstl:choose>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Coste (5 años) -->
  <div class="card mb-4 shadow-sm">
    <div class="card-header">
      <h3 class="mb-0">
        <acme:print code="customer.customer-dashboard.section.cost-5y"/>
      </h3>
    </div>
    <div class="card-body">
      <table class="table table-sm table-bordered mb-0">
        <tr><th><acme:print code="customer.customer-dashboard.label.count"/></th><td class="text-end"><acme:print value="${costCount5y}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.avg"/></th>  <td class="text-end"><acme:print value="${costAvg5y}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.min"/></th>  <td class="text-end"><acme:print value="${costMin5y}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.max"/></th>  <td class="text-end"><acme:print value="${costMax5y}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.stddev"/></th><td class="text-end"><acme:print value="${costStddev5y}"/></td></tr>
      </table>
    </div>
  </div>

  <!-- Pasajeros por reserva -->
  <div class="card mb-4 shadow-sm">
    <div class="card-header">
      <h3 class="mb-0">
        <acme:print code="customer.customer-dashboard.section.pax-per-booking"/>
      </h3>
    </div>
    <div class="card-body">
      <table class="table table-sm table-bordered mb-0">
        <tr><th><acme:print code="customer.customer-dashboard.label.samples"/></th><td class="text-end"><acme:print value="${paxCountSamples}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.avg"/></th>    <td class="text-end"><acme:print value="${paxAvg}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.min"/></th>    <td class="text-end"><acme:print value="${paxMin}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.max"/></th>    <td class="text-end"><acme:print value="${paxMax}"/></td></tr>
        <tr><th><acme:print code="customer.customer-dashboard.label.stddev"/></th> <td class="text-end"><acme:print value="${paxStddev}"/></td></tr>
      </table>
    </div>
  </div>

  <acme:return/>
</div>
