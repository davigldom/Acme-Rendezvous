
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



<display:table name="announcements" id="row" requestURI="${requestURI }"
	pagesize="5" class="displaytag" keepStatus="true">

	<acme:columnOut code="announcement.rendezvous"
		path="${row.rendezvous.name}" />

	<acme:columnOut code="announcement.title" path="${row.title}" />

	<acme:columnMoment code="announcement.moment" property="moment" />

	<acme:columnOut code="announcement.description"
		path="${row.description}" />

	<security:authorize access="hasRole('ADMIN')">
		<acme:columnButton
			url="announcement/admin/edit.do?announcementId=${row.id}"
			code="announcement.delete" />
	</security:authorize>

</display:table>
<br />

<jstl:if test="${rendezvousId!=null }">
	<acme:button url="rendezvous/display.do?rendezvousId=${rendezvousId}"
		code="announcement.backToRendezvous" />
</jstl:if>
<jstl:if test="${rendezvousId==null }">
	<acme:button url="rendezvous/user/list.do"
		code="announcement.backToRSVPRendezvouses" />
</jstl:if>