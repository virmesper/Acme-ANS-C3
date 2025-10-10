<%@page %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<h2><acme:print code="administrator.recommendation.populate.title"/></h2>

<acme:form>
  <acme:input-textbox code="administrator.recommendation.populate.city"    path="city"    placeholder="e.g. Seville"/>
  <acme:input-textbox code="administrator.recommendation.populate.country" path="country" placeholder="e.g. Spain"/>
  <acme:input-textbox code="administrator.recommendation.populate.category" path="category" placeholder="restaurant, museum, ..."/>
  <acme:input-integer code="administrator.recommendation.populate.limit"    path="limit"/>

  <acme:submit code="administrator.recommendation.populate.submit" action="/administrator/recommendation/populate"/>
</acme:form>

<acme:return/>
