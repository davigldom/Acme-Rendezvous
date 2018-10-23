<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="actor/${authority}/edit.do"
	modelAttribute="${authority}">

	<form:hidden path="id" />
	<form:hidden path="version" />
	
	<jstl:if test="${authority == 'manager'}">
		<acme:textbox code="actor.vat" path="vat"/>
	</jstl:if>
	
	<acme:textbox code="actor.name" path="name" />

	<acme:textbox code="actor.surname" path="surname" />

	<acme:textbox code="actor.email" path="email" />

	<acme:textbox code="actor.phone" path="phone" placeholder="+34954667899"/>

	<acme:textbox code="actor.postalAddress" path="postalAddress" />

	<acme:birthdate code="actor.birthdate" path="birthdate" />



	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal.username" var="user" />
	</security:authorize>
	<jstl:if test="${user == actor.userAccount.username }">

		<acme:submit code="actor.save" name="save" />

		<acme:button url="actor/display.do?actorId=${actor.id }" code="actor.cancel"/>
	</jstl:if>

</form:form>


<jstl:if test="${authority == 'user'}">
	<form:form action="actor/${authority}/edit.do">
		<jstl:if test="${user == actor.userAccount.username }">
			<input type="hidden" name="actorId" value="${actor.id}" />
			<acme:submit code="actor.delete" name="delete" />

		</jstl:if>
	</form:form>
</jstl:if>

<jstl:if test="${authority == 'manager'}">
	<form:form action="actor/${authority}/edit.do">
		<jstl:if test="${user == actor.userAccount.username }">
			<input type="hidden" name="actorId" value="${actor.id}" />
			<acme:submit code="actor.delete" name="delete" />

		</jstl:if>
	</form:form>
</jstl:if>