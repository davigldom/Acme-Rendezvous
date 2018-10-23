
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="comment/user/edit.do?rendezvousId=${rendezvousId}" modelAttribute="comment" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	

	<acme:textarea
	code="comment.text"
	path="text" />
	
	<acme:textbox
	code="comment.picture"
	path="picture" />
	
	<acme:submit
	code="comment.save"
	name="save" />
	
	<acme:button url="comment/list.do?rendezvousId=${rendezvousId }" code="actor.cancel"/>
	
	
</form:form>


