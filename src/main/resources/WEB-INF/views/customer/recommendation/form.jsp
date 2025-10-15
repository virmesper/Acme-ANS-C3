<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.recommendation.form.label.name" path="name" />
	<acme:input-textbox code="customer.recommendation.form.label.city" path="city" />
	<acme:input-textbox code="customer.recommendation.form.label.businessStatus" path="businessStatus"/>
	<acme:input-textbox code="customer.recommendation.form.label.formattedAddress" path="formattedAddress" />
	<acme:input-double code="customer.recommendation.form.label.rating" path="rating"/>
	<acme:input-integer code="customer.recommendation.form.label.userRatingsTotal" path="userRatingsTotal"/>
	<acme:input-checkbox code="customer.recommendation.form.label.openNow" path="openNow"/>
	<acme:input-textbox code="customer.recommendation.form.label.photoReference" path="photoReference"/>
</acme:form>
	<img src="${photoReference}" alt="${name}" class="img-fluid rounded" style="border-style: solid;" width="30%"/>