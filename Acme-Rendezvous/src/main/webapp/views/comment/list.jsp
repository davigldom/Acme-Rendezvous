
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- Añadida -->
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<jstl:set value="comment/list.do" var="requestURI"/>

<jstl:if test="${isRepliesList }">
<jstl:set value="comment/listReplies.do" var="requestURI"/>
	<spring:message code="comment.showing.replies" var="showing" />
	<jstl:out value="${showing }" />
	<br>
	<i><jstl:out
			value="'${comment.text}'   -${comment.user.userAccount.username }" /></i>
</jstl:if>

<display:table name="comments" id="row" requestURI="${requestURI }"
	pagesize="5" class="displaytag" keepStatus="false">

	<acme:columnOut code="comment.author" path="${row.user.name } ${row.user.surname }"/>

	<acme:columnMoment code="comment.moment" property="moment"/>

	<spring:message code="comment.text" var="textHeader" />
	<display:column title="${textHeader}">
		<jstl:out value="${row.text }" />
		<br>
		<br>
		<br>
		<jstl:if test="${row.picture!=null }">
			<img src="${row.picture }" />
		</jstl:if>
	</display:column>

	<display:column>
		<input type="button"
			value="<spring:message code="comment.replies" /> (${fn:length(row.replies)})"
			onclick="javascript: window.location.replace('comment/listReplies.do?commentId=${row.id}')" 
			class="btn btn-primary" />

		<security:authorize access="hasRole('USER')">
			<jstl:if test="${isAttendant }">
				<acme:button url="comment/user/createReply.do?commentId=${row.id}&rendezvousId=${row.rendezvous.id}" code="comment.reply"/>
			</jstl:if>
		</security:authorize>
	</display:column>

	<security:authorize access="hasRole('ADMIN')">
		<acme:columnButton url="comment/admin/edit.do?commentId=${row.id}" code="comment.delete"/>
	</security:authorize>
</display:table>
<br>

<security:authorize access="hasRole('USER')">
	<jstl:if test="${isAttendant && !isRepliesList }">
		<acme:button url="comment/user/create.do?rendezvousId=${rendezvousId}" code="comment.create"/>
	</jstl:if>
</security:authorize>


<jstl:if test="${isRepliesList }">
	<acme:button url="comment/list.do?rendezvousId=${rendezvousId}" code="comment.backToCommentsList"/>
</jstl:if>
<br><br>
<acme:button url="rendezvous/display.do?rendezvousId=${rendezvousId}" code="comment.backToRendezvous"/>
