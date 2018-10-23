
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>



<display:table name="services" id="row" requestURI="${requestURI }"
	pagesize="5" class="displaytag" keepStatus="false">

	<acme:columnOut code="service.name" path="${row.name}" />
	<acme:columnOut code="service.category" path="${row.category.name}" />
	<spring:message code="service.yes" var="yes" />
	<spring:message code="service.no" var="no" />
	<jstl:choose>
		<jstl:when test="${row.cancelled == false }">
			<acme:columnOut code="service.cancelled" path="${no }" />
		</jstl:when>
		<jstl:otherwise>
			<acme:columnOut code="service.cancelled" path="${yes }" />
		</jstl:otherwise>
	</jstl:choose>

	<fmt:formatNumber var="priceFormat" type="number" minFractionDigits="1" maxFractionDigits="2" value="${row.price}" />
	<acme:columnOut code="service.price" path="${priceFormat}"/>

	<acme:columnButton url="acme-service/display.do?serviceId=${row.id}"
		code="service.display" />

	<jstl:if test="${listManager == true }">
		<security:authorize access="hasRole('MANAGER')">
			<display:column>
				<jstl:if test="${row.cancelled == false }">
					<acme:button
						url="acme-service/manager/edit.do?serviceId=${row.id}"
						code="service.edit" />
				</jstl:if>
			</display:column>
		</security:authorize>
	</jstl:if>

	<security:authorize access="hasRole('USER')">
		<display:column>
			<jstl:if test="${row.cancelled == false }">
				<acme:button url="request/user/create.do?serviceId=${row.id}"
					code="service.request" />
			</jstl:if>
		</display:column>
	</security:authorize>

	<security:authorize access="hasRole('ADMIN')">
		<display:column>
			<jstl:if test="${row.cancelled==false}">
				<acme:button url="acme-service/admin/cancel.do?serviceId=${row.id}"
					code="service.cancel" />
			</jstl:if>
		</display:column>
	</security:authorize>


</display:table>
