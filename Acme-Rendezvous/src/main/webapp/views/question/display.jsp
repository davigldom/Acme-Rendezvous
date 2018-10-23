<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<display:table pagesize="5" class="displaytag" keepStatus="true" name="questions" requestURI="${requestURI }" id="row">

	<jstl:set var="isAnswered" value="false" />

	<jstl:forEach var="answer" items="${answers}">
		<jstl:if test="${answer.question.id == row.id }">
			<jstl:set var="answerOfQuestion" value="${answer.response }" />
			<jstl:set var="isAnswered" value="true" />
		</jstl:if>
	</jstl:forEach>
	
	<acme:columnOut code="question.statement" path="${row.statement}" sortable="true"/>
	
	<jstl:if test="${yourAnswers == true}">
		<spring:message code="question.response" var="responseHeader" />
	</jstl:if>
	<jstl:if test="${yourAnswers == false}">
		<spring:message code="question.response2" var="responseHeader" />
	</jstl:if>
	
	<display:column title="${responseHeader}" sortable="true">
		<jstl:if test="${yourAnswers == true}">
			<jstl:choose>
				<jstl:when test="${isAnswered == false }">
					<form:form action="answer/user/edit.do?questionId=${row.id }" modelAttribute="answer" method = "post">
						<form:hidden path="id"/>
						<form:hidden path="version"/>
						
						<form:input path="response" size="50" />
						<form:errors path="response" cssClass="error" />
							
						<acme:submit code="question.answer" name="saveRSVP" />
					</form:form>
				</jstl:when>
				<jstl:otherwise>
					<jstl:out value="${answerOfQuestion }" />
				</jstl:otherwise>
			</jstl:choose>
		</jstl:if>
		<jstl:if test="${yourAnswers == false}">
			<jstl:choose>
				<jstl:when test="${isAnswered == false }">
					<jstl:out value="" />
				</jstl:when>
				<jstl:otherwise>
					<jstl:out value="${answerOfQuestion }" />
				</jstl:otherwise>
			</jstl:choose>
		</jstl:if>
	</display:column>
	
</display:table>

<jstl:if test="${yourAnswers == true}">
	<spring:message code="question.must" var="must" />
	<b><jstl:out value="${must }" /></b>
	<br><br>
	<acme:button url="/rendezvous/list.do" code="rendezvous.back"/>
</jstl:if>
<br>

<jstl:if test="${yourAnswers == false}">
	<acme:button url="/rendezvous/display.do?rendezvousId=${rendezvousId}" code="rendezvous.back"/>
</jstl:if>
