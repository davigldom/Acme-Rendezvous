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
<script src="https://cdn.jsdelivr.net/npm/js-cookie@2/src/js.cookie.min.js"></script>


<spring:message code="rendezvous.name" var="name" />
<b><jstl:out value="${name }: " /></b>
<jstl:out value="${rendezvous.name }" />
<br>
<br>

<spring:message code="rendezvous.description" var="description" />
<b><jstl:out value="${description }: " /></b>
<jstl:out value="${rendezvous.description }" />
<br>
<br>

<spring:message code="rendezvous.moment" var="moment" />
<b><jstl:out value="${moment }: " /></b>
<jstl:out value="${momentFormated }" />
<br>
<br>


<spring:message code="rendezvous.picture" var="picture" />
<b><jstl:out value="${picture }: " /></b>
<br>
<jstl:choose>
	<jstl:when test="${rendezvous.picture != '' }">
		<img src="<jstl:out value="${rendezvous.picture }" />"
			alt="${picture }" height="200" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="rendezvous.noPicture" var="noPicture" />
		<jstl:out value="${noPicture }" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<spring:message code="rendezvous.coordinates" var="coordinates" />
<b><jstl:out value="${coordinates }: " /></b>
<jstl:choose>
	<jstl:when
		test="${rendezvous.coordinates.latitude != null && rendezvous.coordinates.longitude != null}">
		<jstl:out value="${rendezvous.coordinates.latitude }" />, <jstl:out
			value="${rendezvous.coordinates.longitude }" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="rendezvous.noCoordinates" var="noCoordinates" />
		<jstl:out value="${noCoordinates }" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<spring:message code="rendezvous.creator" var="creator" />
<b><jstl:out value="${creator }: " /></b>
<jstl:out value="${rendezvous.creator.name}" />
<jstl:out value="${rendezvous.creator.surname}" />
<acme:button url="actor/display.do?actorId=${rendezvous.creator.id}"
	code="rendezvous.seeProfile" />
<br>
<br>
<br>

<div>
	<ul id="menu">
		<li>		
			<a id="showQuestions" href="javascript:show('questions')" ><spring:message code="rendezvous.showQuestions" /></a>
		</li>
		<li>
			<a id="showAttendants" href="javascript:show('attendants')" ><spring:message code="rendezvous.showAttendants" /></a>
		</li>
		<li>
			<a id="showSimilars" href="javascript:show('similars')" ><spring:message code="rendezvous.showSimilars" /></a>
		</li>
		<li>
			<a id="showServices" href="javascript:show('services')" ><spring:message code="rendezvous.showServices" /></a>
		</li>
	</ul>
</div>



<div id='questions' style='display:none;'>
<h2>
	<b><spring:message code="question.list" /></b>
</h2>

<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="questions"
	requestURI="/rendezvous/display.do"
	id="row">

	<jstl:set var="isAnswered" value="false" />

	<security:authorize access="hasRole('USER')">
		<jstl:forEach var="answer" items="${answers}">
			<jstl:if test="${answer.question.id == row.id }">
				<jstl:set var="answerOfQuestion" value="${answer.response }" />
				<jstl:set var="isAnswered" value="true" />
			</jstl:if>
		</jstl:forEach>
	</security:authorize>

	<acme:columnOut code="question.statement" path="${row.statement}"
		sortable="true" />

	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal.username" var="user" />
	</security:authorize>

	<jstl:if test="${isRSVPing == true }">
		<spring:message code="question.response" var="responseHeader" />
		<display:column title="${responseHeader}" sortable="true">
			<jstl:choose>
				<jstl:when test="${isAnswered == false }">
					<form:form action="answer/user/edit.do?questionId=${row.id }"
						modelAttribute="answer" method="post">
						<form:hidden path="id" />
						<form:hidden path="version" />

						<form:input path="response" size="50" />
						<form:errors path="response" cssClass="error" />

						<acme:submit code="question.answer" name="save" />
					</form:form>
				</jstl:when>
				<jstl:otherwise>
					<jstl:out value="${answerOfQuestion }" />
				</jstl:otherwise>
			</jstl:choose>
		</display:column>
	</jstl:if>

	<jstl:if test="${user == row.creator.userAccount.username }">
		<display:column>
			<acme:button url="question/user/edit.do?questionId=${row.id}"
				code="rendezvous.edit" />
		</display:column>
	</jstl:if>

</display:table>

<jstl:if test="${user == rendezvous.creator.userAccount.username }">
	<br>
	<form:form action="question/user/edit.do?rendezvousId=${rendezvousId }"
		modelAttribute="question" method="post">
		<form:hidden path="id" />
		<form:hidden path="version" />

		<acme:textbox code="question.addQuestion" path="statement" />

		<acme:submit code="question.add" name="saveCreate" />
	</form:form>
</jstl:if>
</div>


<div id='attendants' style='display:none;'>
<h2>
	<b><spring:message code="actor.attendants" /></b>
</h2>
<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="rendezvous.attendants" requestURI="/rendezvous/display.do"
	id="attendant">
	<spring:message code="actor.name" var="nameHeader" />
	<display:column property="name" title="${nameHeader}" sortable="true" />

	<spring:message code="actor.surname" var="surnameHeader" />
	<display:column property="surname" title="${surnameHeader}"
		sortable="true" />

	<acme:columnButton url="actor/display.do?actorId=${attendant.id}"
		code="rendezvous.seeProfile" />

	<acme:columnButton
		url="question/display.do?rendezvousId=${rendezvousId }&userId=${attendant.id}"
		code="rendezvous.seeAnswers" />

</display:table>
</div>


<div id='similars' style='display:none;'>
<h2>
	<b><spring:message code="rendezvous.list.linked" /></b>
</h2>

<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="linkedRendezvouses" requestURI="/rendezvous/display.do" id="rowSimilar">
	<!-- Attributes -->

	<acme:columnOut code="rendezvous.name" path="${rowSimilar.name }"/>

	<acme:columnMoment code="rendezvous.moment" property="moment"/>
		
	<jstl:choose>
		<jstl:when
			test="${rowSimilar.coordinates.latitude != null && rowSimilar.coordinates.longitude != null}">
			<acme:columnOut code="rendezvous.coordinates"
				path="${rowSimilar.coordinates.latitude }, ${rowSimilar.coordinates.longitude }" />
		</jstl:when>
		<jstl:otherwise>
			<acme:columnOut code="rendezvous.coordinates" path="" />
		</jstl:otherwise>
	</jstl:choose>

	<acme:columnOut code="rendezvous.creator" path="${rowSimilar.creator.name } ${rowSimilar.creator.surname }"/>

	<acme:columnButton url="rendezvous/display.do?rendezvousId=${rowSimilar.id}"
		code="rendezvous.display" />
	
<security:authorize access="isAuthenticated()">
	<security:authentication property="principal.username" var="user" />

	<jstl:if test="${user == rowSimilar.creator.userAccount.username }">
	<display:column>
		<form action="rendezvous/user/edit.do" method="post">
			<input type="hidden" name="rendezvousIdMain" value="${rendezvous.id}" />
			<input type="hidden" name="rendezvousIdAux" value="${rowSimilar.id}" />
			<acme:submit name="unlink" code="rendezvous.unlink" />
		</form>
	</display:column>
	</jstl:if>
</security:authorize>

</display:table>


<jstl:if test="${not empty notLinked}">
	<br>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal.username" var="user" />
	</security:authorize>
	<jstl:if test="${user == rendezvous.creator.userAccount.username }">
		<h2>
			<b><spring:message code="rendezvous.link.ask" /></b>
		</h2>
		<form action="rendezvous/user/edit.do" method="post">
			<input type="hidden" name="rendezvousId" value="${rendezvous.id}" />
			<select required multiple="multiple" name="linkedId">
				<jstl:forEach items="${notLinked}" var="rndvs">
					<option value="${rndvs.id}">
						<jstl:out value="${rndvs.name}" />
					</option>
				</jstl:forEach>
			</select> <br>
			<acme:submit name="link" code="rendezvous.link" />
		</form>
	</jstl:if>
</jstl:if>
</div>


<div id='services' style='display:none;'>
<h2>
	<b><spring:message code="rendezvous.list.services" /></b>
</h2>

<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="requests" requestURI="/rendezvous/display.do" id="rowRequest">
	e
	<acme:columnOut code="request.service" path="${rowRequest.service.name }"/>
	<acme:columnOut code="service.category" path="${rowRequest.service.category.name}"/>
	<fmt:formatNumber var="priceFormat" type="number" minFractionDigits="1" maxFractionDigits="2" value="${rowRequest.service.price}" />
	<acme:columnOut code="service.price" path="${priceFormat}"/>
	<acme:columnOut code="request.comments" path="${rowRequest.comments }"/>
	
	<security:authorize access="isAuthenticated()">
		<acme:columnButton url="/acme-service/display.do?serviceId=${rowRequest.service.id }" code="request.seeService"/>
	</security:authorize>

</display:table>
</div>

<br>
<br>

<acme:button url="comment/list.do?rendezvousId=${rendezvous.id}"
	code="rendezvous.comments" />
	
<acme:button url="announcement/list.do?rendezvousId=${rendezvous.id}"
	code="rendezvous.announcements" />

<security:authorize access="hasRole('ADMIN')">
	<acme:submit name="delete" code="rendezvous.delete"
		onclick="javascript: window.location.replace('rendezvous/admin/delete.do?rendezvousId=${rendezvous.id}')" />
</security:authorize>
<br><br>

<acme:button url="rendezvous/list.do"
	code="rendezvous.back" />

<script type="text/javascript">

function show(contents){
	Cookies.set('ccshowQuestions','white');
	Cookies.set('ccshowAttendants','white');
	Cookies.set('ccshowSimilars','white');
	Cookies.set('ccshowServices','white');
	
	Cookies.set('ccquestions','none');
	Cookies.set('ccattendants','none');
	Cookies.set('ccsimilars','none');
	Cookies.set('ccservices','none');
	
	document.getElementById('showQuestions').style.color = 'white';
	document.getElementById('showAttendants').style.color = 'white';
	document.getElementById('showSimilars').style.color = 'white';
	document.getElementById('showServices').style.color = 'white';
	
	document.getElementById('questions').style.display = 'none';
	document.getElementById('attendants').style.display = 'none';
	document.getElementById('similars').style.display = 'none';
	document.getElementById('services').style.display = 'none';
	
	if (contents == "questions"){
		Cookies.set('ccshowQuestions','#7CAFB7');
		Cookies.set('ccquestions','block');
		document.getElementById('showQuestions').style.color = '#7CAFB7';
		document.getElementById('questions').style.display = 'block';
	} else if (contents == "attendants") {
		Cookies.set('ccshowAttendants','#7CAFB7');
		Cookies.set('ccattendants','block');
		document.getElementById('showAttendants').style.color = '#7CAFB7';
		document.getElementById('attendants').style.display = 'block';
	} else if (contents == "similars") {
		Cookies.set('ccshowSimilars','#7CAFB7');
		Cookies.set('ccsimilars','block');
		document.getElementById('showSimilars').style.color = '#7CAFB7';
		document.getElementById('similars').style.display = 'block';
	} else if (contents == "services") {
		Cookies.set('ccshowServices','#7CAFB7');
		Cookies.set('ccservices','block');
		document.getElementById('showServices').style.color = '#7CAFB7';
		document.getElementById('services').style.display = 'block';
	}
}

function initial(){
	document.getElementById('showQuestions').style.color = Cookies.get('ccshowQuestions');
	document.getElementById('showAttendants').style.color = Cookies.get('ccshowAttendants');
	document.getElementById('showSimilars').style.color = Cookies.get('ccshowSimilars');
	document.getElementById('showServices').style.color = Cookies.get('ccshowServices');
	
	document.getElementById('questions').style.display = Cookies.get('ccquestions');
	document.getElementById('attendants').style.display = Cookies.get('ccattendants');
	document.getElementById('similars').style.display = Cookies.get('ccsimilars');
	document.getElementById('services').style.display = Cookies.get('ccservices');
}

window.onload=initial;

</script>


