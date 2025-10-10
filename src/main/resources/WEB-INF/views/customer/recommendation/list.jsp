<%@page %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<h2><acme:print code="customer.recommendation.list-related.title"/></h2>

<acme:list >
  <acme:list-column code="customer.recommendation.list.label.title"    path="title"   width="35%"/>
  <acme:list-column code="customer.recommendation.list.label.category" path="category" width="15%"/>
  <acme:list-column code="customer.recommendation.list.label.city"     path="city"     width="15%"/>
  <acme:list-column code="customer.recommendation.list.label.country"  path="country"  width="10%"/>
  <acme:list-column code="customer.recommendation.list.label.rating"   path="rating"   width="10%"/>
  <acme:list-column code="customer.recommendation.list.label.url"      path="url"      width="15%"/>
</acme:list>

