<%@page %>
<%@taglib prefix="acme" uri="http://acme-framework.org/" %>

<h2><acme:print code="customer.recommendation.form.title"/></h2>

<acme:form>
    <acme:input-textbox code="customer.recommendation.form.label.title"    path="title"           readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.category" path="category"        readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.city"     path="city"            readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.country"  path="country"         readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.rating"   path="rating"          readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.price"    path="priceLevel"      readonly="true"/>
    <acme:input-textarea code="customer.recommendation.form.label.desc"    path="shortDescription" readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.url"      path="url"             readonly="true"/>
    <acme:input-textbox code="customer.recommendation.form.label.image"    path="imageUrl"        readonly="true"/>
    <acme:input-moment  code="customer.recommendation.form.label.updated"  path="lastUpdate"      readonly="true"/>

    <acme:return/>
</acme:form>
