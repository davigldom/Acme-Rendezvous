
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<i><jstl:out value="'${comment.text}'   -${comment.user.userAccount.username }"/></i>

<form:form action="comment/user/reply.do?commentId=${comment.id }&rendezvousId=${rendezvousId }" modelAttribute="reply" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
<%-- 	<form:hidden path="moment"/>
	<form:hidden path="user"/>
	<form:hidden path="rendezvous"/>
	<form:hidden path="replies"/> --%>

	<acme:textarea
	code="comment.text"
	path="text" />
	
	<acme:textbox
	code="comment.picture"
	path="picture" />
	
	<acme:submit
	code="comment.save"
	name="reply" />
	
<acme:button url="comment/listReplies.do?commentId=${comment.id}" code="actor.cancel"/>
	
	
</form:form>


