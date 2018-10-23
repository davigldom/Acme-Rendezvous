
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<spring:message code="category.default.name" var="defaultName" />
<spring:message code="rendezvous.category" var="categoryTitle" />

<jstl:if test="${category.name != 'Default' }">
<h2><jstl:out value="${categoryTitle} " /> <a href="category/list.do?categoryId=${category.id}">${category.name }</a></h2>
</jstl:if>
<jstl:if test="${category.name == 'Default' }">
<h2><jstl:out value="${categoryTitle} " /> <a href="category/list.do?categoryId=${category.id}">${defaultName}</a></h2>
</jstl:if>



<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="rendezvouses" requestURI="${requestURI }" id="row">


	<!-- Attributes -->

	<acme:column code="rendezvous.name" path="name"/>
	<acme:columnMoment code="rendezvous.moment" property="moment"/>

<jstl:choose>
	<jstl:when test="${row.coordinates.latitude != null && row.coordinates.longitude != null}">
		<acme:columnOut code="rendezvous.coordinates" path="${row.coordinates.latitude }, ${row.coordinates.longitude }"/>
	</jstl:when>
	<jstl:otherwise>
		<acme:columnOut code="rendezvous.coordinates" path=""/>
	</jstl:otherwise>
</jstl:choose>
	
	<jstl:if test="${isListingCreated == false}">
		<acme:columnOut code="rendezvous.creator" path="${row.creator.name } ${row.creator.surname }"/>
	</jstl:if>
	

	<spring:message code="rendezvous.yes" var="yes" />
	<spring:message code="rendezvous.no" var="no" />
	<jstl:choose>
		<jstl:when test="${row.draft == false }">
			<acme:columnOut code="rendezvous.final" path="${yes }"/>
		</jstl:when>	
		<jstl:otherwise>
			<acme:columnOut code="rendezvous.final" path="${no }"/>
		</jstl:otherwise>
	</jstl:choose>
	
	<jstl:choose>
		<jstl:when test="${row.deleted == false }">
			<acme:columnOut code="rendezvous.deleted" path="${no }"/>
		</jstl:when>	
		<jstl:otherwise>
			<acme:columnOut code="rendezvous.deleted" path="${yes }"/>
		</jstl:otherwise>
	</jstl:choose>
	
	<spring:message code="rendezvous.expired" var="expiredHeader" />
	<jstl:choose>
		<jstl:when test="${row.moment > now}">
			<acme:columnOut code="rendezvous.expired" path="${no }"/>
		</jstl:when>	
		<jstl:otherwise>
			<acme:columnOut code="rendezvous.expired" path="${yes }"/>
		</jstl:otherwise>
	</jstl:choose>
	
	<spring:message code="rendezvous.adultOnly" var="adultHeader" />
	<jstl:choose>
		<jstl:when test="${row.adultOnly == false}">
			<acme:columnOut code="rendezvous.adultOnly" path="${no }"/>
		</jstl:when>	
		<jstl:otherwise>
			<acme:columnOut code="rendezvous.adultOnly" path="${yes }"/>
		</jstl:otherwise>
	</jstl:choose>
	
	
	
	<acme:columnButton url="rendezvous/display.do?rendezvousId=${row.id}" code="rendezvous.display"/>

	<security:authorize access="hasRole('USER')">
		<display:column>
			<jstl:if test="${row.creator == principal}">
				<jstl:if test="${row.draft == true}">
					<jstl:if test="${row.deleted == false}">	
						<acme:button url="rendezvous/user/edit.do?rendezvousId=${row.id}" code="rendezvous.edit"/>
					</jstl:if>
				</jstl:if>
			</jstl:if>
		</display:column>
		
		<display:column>
			<jstl:if test="${row.creator == principal}">
				<jstl:if test="${row.draft == true}">
					<jstl:if test="${row.deleted == false}">	
						<acme:button url="announcement/user/create.do?rendezvousId=${row.id}" code="rendezvous.addAnnouncement"/>
					</jstl:if>
				</jstl:if>
			</jstl:if>
		</display:column>
		
		<display:column>
			<jstl:if test="${row.moment > now}">
				<jstl:if test="${!row.attendants.contains(principal)}">
					<acme:button url="question/user/display.do?rendezvousId=${row.id}" code="rendezvous.RSVP"/>
				</jstl:if>
			</jstl:if>
			
			<jstl:if test="${isListingRSVPd == true}">
				<jstl:if test="${row.attendants.contains(principal)}">
					<acme:button url="rendezvous/user/cancel.do?rendezvousId=${row.id}" code="rendezvous.cancel"/>
				</jstl:if>
			</jstl:if>
		</display:column>
	</security:authorize>

</display:table>

<br/>

<jstl:if test="${isListingRSVPd == true}">
	<jstl:if test="${row.attendants.contains(principal)}">
		<acme:button url="announcement/user/listRSVP.do?userId=${principal.id}" code="rendezvous.listAnnouncements"/>
	</jstl:if>
</jstl:if>
