<%--
 * header.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>


<div>
	<a title="Logo" href="/Acme-Rendezvous"><img id="banner"
		src="${banner}" alt="Acme Rendezvous" height="140px" /></a>
</div>

<div>
	<ul id="jMenu">
		<li><a class="fNiv"> <span class="	glyphicon glyphicon-tent"></span>
				<spring:message code="master.page.rendezvous" />
		</a>
			<ul>
				<li class="arrow"></li>
				<li><a href="rendezvous/list.do"><spring:message
							code="master.page.rendezvous.listAll" /></a></li>

				<security:authorize access="hasRole('USER')">
					<li><a href="rendezvous/user/create.do"><spring:message
								code="master.page.rendezvous.create" /> </a></li>
					<li><a href="rendezvous/user/list.do"><spring:message
								code="master.page.rendezvous.listRSVPd" /></a></li>
					<li><a href="rendezvous/user/listCreated.do"><spring:message
								code="master.page.rendezvous.listCreated" /></a></li>
				</security:authorize>

			</ul></li>

		<security:authorize access="isAuthenticated()">
			<li><a class="fNiv"> <span
					class="glyphicon glyphicon-th-list"></span> <spring:message
						code="master.page.service" />
			</a>
				<ul>
					<li class="arrow"></li>
					<li><a href="acme-service/list.do"><spring:message
								code="master.page.service.listAll" /></a></li>
					<security:authorize access="hasRole('MANAGER')">
						<li><a href="acme-service/manager/create.do"><spring:message
									code="master.page.service.create" /> </a></li>
						<li><a href="acme-service/manager/list.do"><spring:message
									code="master.page.service.listCreated" /> </a></li>
					</security:authorize>

				</ul></li>
		</security:authorize>

		<li><a class="fNiv"> <span class="	glyphicon glyphicon-tag"></span>
				<spring:message code="master.page.category" />
		</a>
			<ul>
				<li class="arrow"></li>
				<li><a href="category/list.do"><spring:message
							code="master.page.category.listAll" /></a></li>
				<security:authorize access="hasRole('ADMIN')">
					<li><a href="category/admin/create.do"><spring:message
								code="master.page.category.create" /></a></li>
				</security:authorize>
			</ul></li>

		<li><a class="fNiv"> <span class="glyphicon glyphicon-user"></span>
				<spring:message code="master.page.users" /></a>
			<ul>
				<li class="arrow"></li>
				<li><a href="actor/list.do"><spring:message
							code="master.page.users.list" /> </a></li>
			</ul></li>



		<security:authorize access="isAuthenticated()">
			<li id="logout"><a class="fNiv" href="j_spring_security_logout">
					<span class="glyphicon glyphicon-off"></span> <spring:message
						code="master.page.logout" />
			</a></li>
			<li><a class="fNiv"> <span class="glyphicon glyphicon-cog"></span>
					<spring:message code="master.page.profile" /> (<security:authentication
						property="principal.username" />)
			</a>

				<ul>
					<li class="arrow"></li>
					<li><a href="actor/display-principal.do"><spring:message
								code="master.page.display" /></a></li>
					<security:authorize access="hasRole('ADMIN')">
						<li><a href="actor/admin/dashboard.do"><spring:message
									code="master.page.administrator.dashboard" /></a></li>
						<li><a href="system-config/admin/edit.do"><spring:message
									code="master.page.sysconfig.edit" /></a></li>
					</security:authorize>
				</ul></li>
		</security:authorize>
		<security:authorize access="isAnonymous()">
			<li id="logout"><a class="fNiv" href="security/login.do"><span
					class="glyphicon glyphicon-log-in"></span> <spring:message
						code="master.page.login" /></a></li>
		</security:authorize>




	</ul>
</div>

<div id="languages">
	<a href="?language=en">en</a> | <a href="?language=es">es</a>
</div>

