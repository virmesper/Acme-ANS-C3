<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-1"
				action="https://premierpadel.com" />
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-2"
				action="https://favoley.es" />
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-3"
				action="https://www.zara.com" />
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-4"
				action="https://www.netflix.com" />
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.authenticated.list-assignments" 
				action="/any/flight-assignment/list" />
		</acme:menu-option>
    
		<acme:menu-option code="master.menu.assistance-agent" access="hasRealm('AssistanceAgent')">
 			<acme:menu-suboption code="master.menu.assistance-agent.claim.list-completed" action="/assistance-agent/claim/list-completed"/>
 			<acme:menu-suboption code="master.menu.assistance-agent.claim.list-undergoing" action="/assistance-agent/claim/list-undergoing"/>
 			<acme:menu-suboption code="master.menu.assistance-agent.dashboard" action="/assistance-agent/assistance-agent-dashboard/show"/>

 		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
		    <acme:menu-suboption code="master.menu.customer.list-bookings" action="/customer/booking/list"/>
		    <acme:menu-separator/>
		    <acme:menu-suboption code="master.menu.customer.passenger-all" action="/customer/passenger/list-all"/>
		    <acme:menu-separator/>
		    <acme:menu-suboption code="master.menu.customer.dashboard" action="/customer/customer-dashboard/show"/>
			<acme:menu-separator/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.recommendation" access="hasRealm('Administrator') || hasRealm('Customer')">
			<acme:menu-suboption access="hasRealm('Customer')" code="master.menu.customer.list-recommendation" action="/customer/recommendation/list" />
			<acme:menu-suboption access="hasRealm('Customer')" code="master.menu.customer.list-related-recommendation" action="/customer/recommendation/list-related" />
			<acme:menu-suboption access="hasRealm('Administrator')" code="master.menu.administrator.list-recommendation" action="/administrator/recommendation/list" />
			<acme:menu-suboption access="hasRealm('Administrator')" code="master.menu.administrator.populate-recommendation" action="/administrator/recommendation/populate"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">

		    <acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
		    <acme:menu-separator/>
		    <acme:menu-suboption code="master.menu.administrator.list-airport" action="/administrator/airport/list"/>
		    <acme:menu-suboption code="master.menu.administrator.list-aircraft" action="/administrator/aircraft/list"/>
        	<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list" />
			<acme:menu-suboption code="master.menu.administrator.list-maintenance-records" action="/administrator/maintenance-record/list"/>
		    <acme:menu-separator/>
		    <acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
		    <acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>
		    <acme:menu-separator/>
			<acme:menu-suboption code="administrator.menu.populate-visa" action="/administrator/system/populate-visa"/>
		    <acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-bookings" action="/administrator/booking/list"/>
			<acme:menu-separator/>
		    <acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list.claim" action="/administrator/claim/list" access="isAuthenticated()"/>
		</acme:menu-option>


		<acme:menu-option code="master.menu.flight-crew-member" access="hasRealm('FlightCrewMember')">
			<acme:menu-suboption code="master.menu.flight-crew-member.flight-assignment.completedlist" action="/flight-crew-member/flight-assignment/completed-list"/>
			<acme:menu-suboption code="master.menu.flight-crew-member.flight-assignment.plannedlist" action="/flight-crew-member/flight-assignment/planned-list"/>
			<acme:menu-separator />
			<acme:menu-suboption code="master.menu.flight-crew-member.show-dashboard" action="/flight-crew-member/flight-crew-member-dashboard/show" />		
			<acme:menu-suboption code="master.menu.flight-crew-member.list-visa" action="/flight-crew-member/visa-requirement/list" />
		</acme:menu-option>


		<acme:menu-option code="master.menu.provider"
			access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link"
				action="http://www.example.com/" />
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer"
			access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link"
				action="http://www.example.com/" />
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.list-my-maintenance-records" action="/technician/maintenance-record/list?mine=true" />			
			<acme:menu-suboption code="master.menu.technician.list-my-tasks" action="/technician/task/list?mine=true" />
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.list-maintenance-record-catalogue" action="/technician/maintenance-record/list" />
			<acme:menu-suboption code="master.menu.technician.list-task-catalogue" action="/technician/task/list" />
 			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.show-dashboard" action="/technician/technician-dashboard/show" />
			
 		</acme:menu-option>
	</acme:menu-left>

	<acme:menu-right>
		<acme:menu-option code="master.menu.user-account"
			access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile"
				action="/authenticated/user-account/update" />
			<acme:menu-suboption code="master.menu.user-account.become-provider"
				action="/authenticated/provider/create"
				access="!hasRealm('Provider')" />
			<acme:menu-suboption code="master.menu.user-account.provider-profile"
				action="/authenticated/provider/update"
				access="hasRealm('Provider')" />
			<acme:menu-suboption code="master.menu.user-account.become-consumer"
				action="/authenticated/consumer/create"
				access="!hasRealm('Consumer')" />
			<acme:menu-suboption code="master.menu.user-account.consumer-profile"
				action="/authenticated/consumer/update"
				access="hasRealm('Consumer')" />
			<acme:menu-suboption code="master.menu.user-account.become-assistance-agent" 
				action="/authenticated/assistance-agent/create" 
				access="!hasRealm('AssistanceAgent')"/>
			<acme:menu-suboption code="master.menu.user-account.assistance-agent-profile" 
				action="/authenticated/assistance-agent/update" 
				access="hasRealm('AssistanceAgent')"/>

			<acme:menu-suboption code="master.menu.user-account.become-technician" 
				action="/authenticated/technician/create" 
				access="!hasRealm('Technician')" />
			<acme:menu-suboption code="master.menu.user-account.technician-profile" 
				action="/authenticated/technician/update" 
				access="hasRealm('Technician')" />

		    <acme:menu-suboption code="master.menu.user-account.become-customer"
		        action="/authenticated/customer/create"
		        access="!hasRealm('Customer')" />
		    <acme:menu-suboption code="master.menu.user-account.customer-profile"
		        action="/authenticated/customer/update"
		        access="hasRealm('Customer')" />
		    <acme:menu-suboption code="master.menu.user-account.become-crewMember" action="/authenticated/flight-crew-member/create" access="!hasRealm('FlightCrewMember')"/>
		    <acme:menu-suboption code="master.menu.user-account.crewMember-profile" action="/authenticated/flight-crew-member/update" access="hasRealm('FlightCrewMember')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>