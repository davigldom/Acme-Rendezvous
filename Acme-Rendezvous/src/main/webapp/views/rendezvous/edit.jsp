
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="rendezvous/user/edit.do" modelAttribute="rendezvous" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>

	
	<acme:textbox
	code="rendezvous.name"
	path="name" />
	
	<acme:textarea
	code="rendezvous.description"
	path="description" />
	
	<spring:message code="rendezvous.validMoment" var="valid"/>
	<jstl:out value="${valid }"/>
	<acme:moment
	code="rendezvous.moment"
	path="moment"/>
	
	<acme:textbox
	code="rendezvous.picture"
	path="picture" />
	
	<acme:coordinates
	code="rendezvous.coordinates"
	path="coordinates" />
	

	<jstl:if test="${rendezvous.id!=0}">	
		<acme:checkbox
			code="rendezvous.draft"
			path="draft" />
	</jstl:if>
	

	<jstl:if test="${canSee==true}">
		<jstl:if test="${rendezvous.id!=0}">	
			<acme:checkbox
				code="rendezvous.adultOnly"
				path="adultOnly" />
		</jstl:if>
	</jstl:if>

	
	
	<acme:submit
	code="rendezvous.save"
	name="save" />
	
	<jstl:if test="${rendezvous.id!=0}">
		<acme:submit
			code="rendezvous.delete"
			name="delete" />
	</jstl:if>

	<acme:button url="rendezvous/list.do" code="actor.cancel"/>
	
</form:form>

