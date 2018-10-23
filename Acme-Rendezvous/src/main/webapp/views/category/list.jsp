<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<!-- Listing grid -->

<spring:message code="category.default.name" var="defaultName" />
<spring:message code="category.default.description"
	var="defaultDescription" />

<jstl:if test="${category!=null}">
	<h2 style="display: inline-block;">
		<spring:message code="category.viewing" />
		:
		<jstl:if test="${category.name=='Default' }">
			<jstl:out value="${defaultName }" />
		</jstl:if>
		<jstl:if test="${category.name!='Default' }">
			<jstl:out value="${category.name}" />
		</jstl:if>
		|
	</h2>

	<jstl:if test="${not empty category.parent }">
		<spring:message code="category.parent.goto" var="msg" />
	</jstl:if>

	<jstl:if test="${empty category.parent }">
		<spring:message code="category.root.goto" var="msg" />
	</jstl:if>

	<h3 style="display: inline-block;">
		<a href="category/list.do?categoryId=${category.parent.id}"><jstl:out
				value="${msg }" /></a>
	</h3>
	<h2 style="display: inline-block;">|</h2>
</jstl:if>

<p>
	<jstl:if test="${category.name=='Default' }">
		<p style="font-size:large"><jstl:out value="${defaultDescription }" /></p>
	</jstl:if>
	<jstl:if test="${category.name!='Default' }">
		<p style="font-size:large"><jstl:out value="${category.description}" /></p>
	</jstl:if>
</p>


<jstl:if test="${not empty category}">
	<div>
		<acme:button url="rendezvous/list-by-category.do?categoryId=${category.id}" code="category.seeRendezvouses"/>
	</div>
	<br>
</jstl:if>
<jstl:if test="${not empty categories }">
	<display:table pagesize="5" class="displaytag" keepStatus="true"
		name="categories" requestURI="${requestURI}" id="row">

		<!-- Attributes -->

		<spring:message code="category.subcategories"
			var="subcategoriesHeader" />
		<display:column title="${subcategoriesHeader}" sortable="true">

			<a href="category/list.do?categoryId=${row.id}"
				title="<spring:message code="category.children.goto"/>"> 
				<jstl:if test="${row.name== 'Default' }">
					<jstl:out value="${defaultName}" />
				</jstl:if>
				<jstl:if test="${row.name!='Default' }">
					<jstl:out value="${row.name}" />
				</jstl:if>
			</a>


		</display:column>



		<security:authorize access="hasRole('ADMIN')">
			<display:column>

				<jstl:if test="${row.name!='Default' }">
					<acme:button url="category/admin/edit.do?categoryId=${row.id}" code="category.edit"/>
				</jstl:if>
			</display:column>
		</security:authorize>


	</display:table>
</jstl:if>
