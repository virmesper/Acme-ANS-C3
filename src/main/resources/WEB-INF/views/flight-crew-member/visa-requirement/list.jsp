<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>

	<acme:list-column 
        code="flightCrewMember.visa.list.label.passportCountry"
        path="passportCountry"
	/>

    <acme:list-column 
        code="flightCrewMember.visa.list.label.destinationCountry"
        path="destinationCountry"
	/>

    <acme:list-column
        code="flightCrewMember.visa.list.label.visaType"
        path="visaType"
	/>

    <acme:list-column
        code="flightCrewMember.visa.list.label.stayDuration"
        path="stayDuration"
    />
    
    <acme:list-column
        code="flightCrewMember.visa.list.label.passportValidity"
        path="passportValidity"
    />

    <acme:list-payload path="payload"/>
</acme:list>