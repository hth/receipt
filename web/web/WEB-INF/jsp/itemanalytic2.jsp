<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.0.4/highcharts.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" href="#">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">PROFILE</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT</a>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
<div class="report_sidebar" id="reportSidebarId">
    <div class="sidebar-top-summary">
        <div class="sidebar-top-summary-upper clearfix">
            <ul>
                <c:set var="yearVar" value="" />
                <c:forEach var="item" items="${reportAnalysisForm.receiptGroupedByMonths}"  varStatus="status">
                    <c:if test="${empty yearVar || yearVar ne item.year}">
                        <c:set var="yearVar" value="${item.year}" />
                        <c:if test="${yearVar eq item.year}">
                            <span class="ll-h">${item.year}</span>
                        </c:if>
                    </c:if>

                    <li>
                        <a href="${pageContext.request.contextPath}/access/landing/report/<spring:eval expression='item.dateTime.toString("MMM, yyyy")' />.htm" class="ll-t" target="_blank">
                        <spring:eval expression='item.dateTime.toString("MMM")' /> &nbsp;&nbsp; <spring:eval expression="item.total" />
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>
<!-- cd-popup -->
<%--<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css">--%>
<!-- Resource style -->
<%--<script src="${pageContext.request.contextPath}/static/js/modernizr.js"></script>--%>
<!-- Modernizr -->
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<!-- Resource jQuery -->
</body>
</html>
