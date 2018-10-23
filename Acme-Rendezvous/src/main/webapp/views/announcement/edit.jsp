
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="announcement/user/edit.do?rendezvousId=${rendezvousId }" modelAttribute="announcement" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<acme:textbox
	code="announcement.title"
	path="title" />
	
	<acme:textarea
	code="announcement.description"
	path="description" />
	
	<acme:submit
	code="announcement.save"
	name="save" />
	
	<acme:button url="rendezvous/list.do" code="actor.cancel"/>
	
	
</form:form>


