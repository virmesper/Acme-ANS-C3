<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="administrator.claim.form.label.description" path="description" readonly="true"/>
	<acme:input-email code="administrator.claim.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
	<acme:input-moment code="administrator.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-select code="administrator.claim.form.label.type" path="type" choices="${types}" readonly="true"/>
	<acme:input-select code="administrator.claim.form.label.indicator" path="indicator" choices="${indicators}" readonly="true"/>
	<acme:input-select code="administrator.claim.form.label.leg" path="leg" choices="${legs}" readonly="true"/>	

    <acme:button code="administrator.claim.form.button.trackingLogs" action="/administrator/tracking-log/list?claimId=${id}"/>
   
</acme:form>