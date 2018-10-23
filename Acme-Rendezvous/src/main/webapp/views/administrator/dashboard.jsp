<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><!-- Added -->
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<h4><b><spring:message code="administrator.dashboard.rendezvousPerUser"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageRendezvousesPerUser}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationRendezvousesPerUser" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationRendezvousesPerUser}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationRendezvousesPerUser}"></jstl:out></p>
<br>

<h4><b><spring:message code="administrator.dashboard.everVSnever"/></b></h4>
	<fmt:formatNumber var="everCreateRendezvous" type="number" minFractionDigits="2" maxFractionDigits="2" value="${everCreateRendezvous}" />
<p><spring:message code="administrator.dashboard.ratio"/> : <jstl:out value="${everCreateRendezvous}"></jstl:out></p>
<br>

<h4><b><spring:message code="administrator.dashboard.usersPerRendezvous"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageUsersPerRendezvous}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationUsersPerRendezvous" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationUsersPerRendezvous}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationUsersPerRendezvous}"></jstl:out></p>
<br>

<h4><b><spring:message code="administrator.dashboard.RSVPdRendezvousesPerUser"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageRSVPdRendezvousesPerUser}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationRSVPdRendezvousesPerUser" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationRSVPdRendezvousesPerUser}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationRSVPdRendezvousesPerUser}"></jstl:out></p>

 <br>
 <h4><b><spring:message code="administrator.dashboard.announcementsPerRendezvous"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageAnnouncementsPerRendezvous}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationAnnouncementsPerRendezvous" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationAnnouncementsPerRendezvous}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationAnnouncementsPerRendezvous}"></jstl:out></p>
<br>
<h4><b><spring:message code="administrator.dashboard.rendezvousesAbove75PercentAnnouncements"/></b></h4>
<jstl:forEach items="${selectAbove75PercentAnnouncements}" var="rendezvous">
<p><jstl:out value="${rendezvous.name}"/></p>
</jstl:forEach>
<br>
<h4><b><spring:message code="administrator.dashboard.rendezvousesAboveTenPercentPlusAverageAnnouncements"/></b></h4>
<jstl:forEach items="${selectAboveTenPercentPlusAverageAnnouncements}" var="rendezvous">
<p><jstl:out value="${rendezvous.name}"/></p>
</jstl:forEach>
<br/><br/>

 <h4><b><spring:message code="administrator.dashboard.questionsPerRendezvous"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageQuestionsPerRendezvous}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationQuestionsPerRendezvous" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationQuestionsPerRendezvous}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationQuestionsPerRendezvous}"></jstl:out></p>
<br>

 <h4><b><spring:message code="administrator.dashboard.answersToQuestionsPerRendezvous"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageAnswersToQuestionsPerRendezvous}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationAnswersToQuestionsPerRendezvous" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationAnswersToQuestionsPerRendezvous}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationAnswersToQuestionsPerRendezvous}"></jstl:out></p>
<br>

 <h4><b><spring:message code="administrator.dashboard.repliesPerComment"/></b></h4>

<p><spring:message code="administrator.dashboard.average"/> : <jstl:out value="${averageRepliesPerComment}"></jstl:out></p>
	<fmt:formatNumber var="standardDeviationRepliesPerComment" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationRepliesPerComment}" />
<p><spring:message code="administrator.dashboard.standardDeviation"/> : <jstl:out value="${standardDeviationRepliesPerComment}"></jstl:out></p>
<br>

<br>

<h4><b><spring:message code="administrator.dashboard.top10RSVPdRendezvouses"/></b></h4>

<display:table pagesize="5"  class="displaytag" keepStatus="true" name="top10RSVPdRendezvouses" id="row" requestURI="actor/admin/dashboard.do">
	<acme:column code="administrator.dashboard.top10RSVPdRendezvouses.name" path="name"/>
	<acme:column code="administrator.dashboard.top10RSVPdRendezvouses.moment" path="moment"/>
	<acme:columnOut code="administrator.dashboard.top10RSVPdRendezvouses.coordinates" path="${row.coordinates.latitude } , ${row.coordinates.longitude }"/>
	<acme:columnOut code="administrator.dashboard.top10RSVPdRendezvouses.creator" path="${row.creator.name } ${row.creator.surname }"/>	
</display:table>

<br>

<h4><b><spring:message code="administrator.dashboard.managersProvidingMoreServicesThanAvg"/></b></h4>
	<display:table pagesize="5"  class="displaytag" keepStatus="true" name="managersProvidingMoreServicesThanAvg" id="row" requestURI="actor/admin/dashboard.do">
		<acme:column code="actor.name" path="name"/>
		<acme:column code="actor.surname" path="surname"/>
		<acme:column code="actor.email" path="email"/>
	</display:table>
	
<h4><b><spring:message code="administrator.dashboard.top3ManagersWithMoreServicesCancelled"/></b></h4>
	<display:table pagesize="5"  class="displaytag" keepStatus="true" name="managersWithMoreServicesCancelled" id="row" requestURI="actor/admin/dashboard.do">
		<acme:column code="actor.name" path="name"/>
		<acme:column code="actor.surname" path="surname"/>
		<acme:column code="actor.email" path="email"/>
	</display:table>

<br>




<h4><b><spring:message code="administrator.dashboard.averageNumberCategoriesPerRendezvous"/></b></h4>
<p><acme:out code="administrator.dashboard.average" path="${averageNumberCategoriesPerRendezvous}"/></p>


<h4><b><spring:message code="administrator.dashboard.averageRatioServicesPerCategory"/></b></h4>
<p><acme:out code="administrator.dashboard.average" path="${averageRatioServicesPerCategory}"/></p>



<br>


<h4><b><spring:message code="administrator.dashboard.servicesRequestedPerRendezvous"/></b></h4>
	<p><acme:out code="administrator.dashboard.average" path="${averageServicesRequestedPerRendezvous}"/></p>
	<p><acme:out code="administrator.dashboard.min" path="${minServicesRequestedPerRendezvous}"/></p>
	<p><acme:out code="administrator.dashboard.max" path="${maxServicesRequestedPerRendezvous}"/></p>
		<fmt:formatNumber var="standardDeviationServicesRequestedPerRendezvous" type="number" minFractionDigits="2" maxFractionDigits="2" value="${standardDeviationServicesRequestedPerRendezvous}" />
	<p><acme:out code="administrator.dashboard.standardDeviation" path="${standardDeviationServicesRequestedPerRendezvous}"/></p>

<br>

<h4><b><spring:message code="administrator.dashboard.top3SellingServices"/></b></h4>

<display:table pagesize="5"  class="displaytag" keepStatus="true" name="top3SellingServices" id="row" requestURI="actor/admin/dashboard.do">
	<acme:column code="service.name" path="name"/>
	<acme:column code="service.description" path="description"/>
</display:table>
<br><br>

<acme:button url="/" code="master.page.home"/>

