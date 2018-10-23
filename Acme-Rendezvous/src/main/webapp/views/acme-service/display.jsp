<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<spring:message code="service.name" var="name" />
<b><jstl:out value="${name }: " /></b>
<jstl:out value="${acmeService.name }" />
<br>
<br>

<spring:message code="service.category" var="serviceCategory" />
<b><jstl:out value="${serviceCategory}: " /></b>
<jstl:out value="${acmeService.category.name}" />
<br>
<br>

<spring:message code="service.description" var="description" />
<b><jstl:out value="${description }: " /></b>
<jstl:out value="${acmeService.description }" />
<br>
<b><spring:message code="service.price"/>:</b>
<fmt:formatNumber var="priceFormat" type="number" minFractionDigits="1" maxFractionDigits="2" value="${acmeService.price}" />

<jstl:out value="${priceFormat}" />
<br>
<br>

<spring:message code="service.picture" var="picture" />
<b><jstl:out value="${picture }: " /></b>
<br>
<jstl:choose>
	<jstl:when test="${acmeService.picture != '' }">
		<img src="<jstl:out value="${acmeService.picture }" />"
			alt="${picture }" height="200" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="service.noPicture" var="noPicture" />
		<jstl:out value="${noPicture }" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<spring:message code="service.creator" var="creatorTitle" />
<b><jstl:out value="${creatorTitle }: " /></b>
<jstl:out value="${creator.name}" />
<jstl:out value="${creator.surname}" />
<acme:button url="actor/display.do?actorId=${creator.id}"
	code="service.seeProfile" />
<br>
<br>


<jstl:if test="${acmeService.cancelled==false}">
	<security:authorize access="hasRole('ADMIN')">
		<acme:button url="acme-service/admin/cancel.do?serviceId=${acmeService.id}" code="service.cancel"/>
	</security:authorize>
	<br><br>
</jstl:if>

<acme:button url="acme-service/list.do" code="service.back"/>
