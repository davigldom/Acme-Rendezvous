<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<display:table pagesize="5" class="displaytag" keepStatus="true"
	name="users" id="row" requestURI="/actor/list.do">
	

	<!-- Attributes -->
	
	<acme:column code="actor.name" path="name"/>
	<acme:column code="actor.surname" path="surname"/>
	<acme:column code="actor.birthdate" path="birthdate"/>
	<acme:column code="actor.postalAddress" path="postalAddress"/>
	<acme:column code="actor.phone" path="phone"/>
	<acme:column code="actor.email" path="email"/>

	
	<display:column>
	<acme:button url="actor/display.do?actorId=${row.id}" code="actor.display"/>
	</display:column>
	


</display:table>
