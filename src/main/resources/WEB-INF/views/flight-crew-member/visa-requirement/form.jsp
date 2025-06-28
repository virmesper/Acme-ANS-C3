<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox 
        code="flightCrewMember.visa.form.label.destinationCountry"
        path="destinationCountry"
    />

    <acme:input-textbox 
        code="flightCrewMember.visa.form.label.passportCountry"
        path="passportCountry"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.continent"
        path="continent"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.capital"
        path="capital"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.currency"
        path="currency"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.phoneCode"
        path="phoneCode"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.timezone"
        path="timezone"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.visaType"
        path="visaType"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.stayDuration"
        path="stayDuration"
    />

    <acme:input-textbox
        code="flightCrewMember.visa.form.label.passportValidity"
        path="passportValidity"
    />

    <acme:input-textarea
        code="flightCrewMember.visa.form.label.additionalInfo"
        path="additionalInfo"
    />

    <acme:input-url
        code="flightCrewMember.visa.form.label.officialLink"
        path="officialLink"
    />
</acme:form>