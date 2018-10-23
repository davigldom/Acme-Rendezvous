
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="acme-service/manager/edit.do?serviceId=${serviceId }" modelAttribute="acmeService" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<acme:textbox
		code="service.name"
		path="name" />
	
	<acme:textarea
		code="service.description"
		path="description" />
		
	<acme:textbox
		code="service.price"
		path="price" placeholder="min = 1"/>
	
	<acme:textbox
		code="service.picture"
		path="picture" />
		
	<acme:select items="${categories }" itemLabel="name" code="service.category" path="category"/>
	
	<acme:submit
		code="service.save"
		name="save" />
	
	<jstl:if test="${acmeService.id != 0 }">	
		<acme:submit
			code="service.delete"
			name="delete" />	
	</jstl:if>
	
	<acme:button 
		url="/acme-service/manager/list.do" 
		code="service.cancel"/>
	
</form:form>


