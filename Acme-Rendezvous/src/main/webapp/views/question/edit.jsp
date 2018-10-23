
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="question/user/edit.do?rendezvousId=${rendezvousId }" modelAttribute="question" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>

	
	<acme:textbox
	code="question.statement"
	path="statement" />
	
	
	<acme:submit
	code="question.save"
	name="saveEdit" />
	
	<jstl:if test="${question.id!=0}">
		<acme:submit
			code="question.delete"
			name="delete" />
	</jstl:if>
	
	<acme:button url="rendezvous/display.do?rendezvousId=${rendezvousId}" code="actor.cancel"/>
		
</form:form>

