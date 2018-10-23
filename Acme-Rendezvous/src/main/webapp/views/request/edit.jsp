
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<script src="https://cdn.jsdelivr.net/npm/js-cookie@2/src/js.cookie.min.js"></script>


<form:form action="request/user/edit.do?serviceId=${serviceId }" modelAttribute="request" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<acme:textarea
		code="request.comments"
		path="comments" />
	
	<acme:textbox placeholder="1234 1234 1234 1234"
		code="request.creditCard"
		path="creditCardNumber" id="ccnumbertag"/>
	
	<acme:textbox
		placeholder="MM"
		code="request.month"
		path="expirationMonth" id="ccmonthtag"/>
		
	<acme:textbox
		placeholder="YYYY"
		code="request.year"
		path="expirationYear" id="ccyeartag"/>
		
	<acme:textbox
		code="request.securityNumber"
		path="securityNumber" id="ccsecuritytag"/>
		
	<label>
		<spring:message code="request.rendezvous" />
	</label>
	<jstl:if test="${errorRendezvous != null}">
		<span class="message"><spring:message code="${errorRendezvous}" /></span>
	</jstl:if>
	<br/>
	<jstl:choose>
		<jstl:when test="${not empty rendezvouses }">
			<select id="rendezvouses" name="rendezvousId">
				<option value="0" label="----" />	
				<jstl:forEach items="${rendezvouses}" var="rendezvous">
					<option value="${rendezvous.id}">
						<jstl:out value="${rendezvous.name}" />
					</option>
				</jstl:forEach>	
			</select>
			<br><br>
					
			<acme:submit
				code="request.save"
				name="save" onclick="javascript: setCookiesValue();"/>
				
		</jstl:when>
		<jstl:otherwise>
			<spring:message code="request.noRendezvous" var="noRendezvous" />
			<jstl:out value="${noRendezvous }" />
			<br><br>
		</jstl:otherwise>
	</jstl:choose>
	
	<acme:button url="acme-service/list.do" code="actor.cancel"/>
	
</form:form>

<!-- Cookies do not store the security number of the last used credit card in order to improve the security -->
<script type="text/javascript">

	function setCookiesValue(){
		Cookies.set('ccnumber',$("#ccnumbertag").val());
		Cookies.set('ccmonth',$("#ccmonthtag").val());
		Cookies.set('ccyear',$("#ccyeartag").val());
	}
	
	$(function(){
		$("#ccnumbertag").val(Cookies.get('ccnumber'));
		$("#ccmonthtag").val(Cookies.get('ccmonth'));
		$("#ccyeartag").val(Cookies.get('ccyear'));
		$("#ccsecuritytag").val(null);
	});
	
</script>

