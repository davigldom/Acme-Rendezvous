<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>



<security:authorize access="isAuthenticated()">
	<security:authentication property="principal.username" var="user" />
</security:authorize>


<jstl:if test="${authority == 'manager'}">
	<spring:message code="actor.vat" var="vat" />
	<b><jstl:out value="${vat }: " /></b>
	<jstl:out value="${actor.vat}" />
	<br><br>

</jstl:if>


<spring:message code="actor.name" var="name" />
<b><jstl:out value="${name }: " /></b>
<jstl:out value="${actor.name}" />
<br><br>

<spring:message code="actor.surname" var="surname" />
<b><jstl:out value="${surname}: " /></b>
<jstl:out value="${actor.surname}" />
<br><br>

<spring:message code="actor.birthdate" var="birthdate" />
<b><jstl:out value="${birthdate }: " /></b>
<jstl:out value="${dateFormated}" />
<br><br>

<spring:message code="actor.email" var="email" />
<b><jstl:out value="${email }: " /></b>
<jstl:out value="${actor.email}" />
<br><br>

<spring:message code="actor.phone" var="phone" />
<b><jstl:out value="${phone }: " /></b>
<jstl:out value="${actor.phone}" />
<br><br>

<spring:message code="actor.postalAddress" var="postalAddress" />
<b><jstl:out value="${postalAddress }: " /></b>
<jstl:out value="${actor.postalAddress}" />
<br><br>



<security:authorize access="isAuthenticated()">
	<form:form action="actor/${authority}/edit.do" method="post">
		<input type="hidden" name="actorId" value="${actor.id}"/>	
		<jstl:if test="${user == actor.userAccount.username }">
			<acme:button url="actor/edit.do?actorId=${actor.id}" code="actor.edit"/>
		</jstl:if>
	</form:form>
</security:authorize>

<br>

<jstl:if test="${authority=='user'}">
	<h2>
		<b><spring:message code="actor.rendezvouses" /></b>
	</h2>
		
	<display:table pagesize="5" class="displaytag" keepStatus="true"
		name="rendezvouses" id="row" requestURI="actor/display.do">

		<!-- Attributes -->
		<acme:column code="rendezvous.name" path="name"/>
		<acme:columnMoment code="rendezvous.moment" property="moment"/>
		<acme:columnOut code="rendezvous.coordinates" path="${row.coordinates.latitude }, ${row.coordinates.longitude }"/>
		<acme:columnOut code="rendezvous.creator" path="${row.creator.name } ${row.creator.surname }"/>	
		<acme:columnButton url="rendezvous/display.do?rendezvousId=${row.id}" code="rendezvous.display"/>

	</display:table>
	<br>
</jstl:if>
	
<acme:button url="/" code="master.page.home"/>
