<%@ include file="../include.jsp"%>
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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/admin/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username"/>
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<header>
</header>
<div class="main clearfix">
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            User Search
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: 750px;">
        <div class="down_form" style="width: 930px;">
            <h2 class="h2" style="padding-bottom:5px; text-decoration: underline;">Send notification message to user. This is not a Push Notification</h2>
            <form:form method="post" modelAttribute="notificationSendForm" action="notificationSend.htm">

                <c:if test="${!empty notificationSendForm.errorMessage}">
                <div class="r-error" style="width: 98%; margin: 0 0 0 0;">
                    <c:out value="${notificationSendForm.errorMessage}" />
                </div>
                </c:if>

                <c:if test="${!empty notificationSendForm.successMessage}">
                <div class="r-success" style="width: 98%; margin: 0 0 0 0;">
                    <c:out value="${notificationSendForm.successMessage}" />
                </div>
                </c:if>

                <spring:hasBindErrors name="notificationSendForm">
                <div class="r-validation" style="width: 98%; margin: 0 0 0 0;">
                    <c:if test="${errors.hasFieldErrors('rid')}">
                        <form:errors path="rid" /><br>
                    </c:if>
                    <c:if test="${errors.hasFieldErrors('message')}">
                        <form:errors path="message" /><br>
                    </c:if>
                </div>
                </spring:hasBindErrors>

                <div class="row_field">
                    <label class="profile_label">RID</label>
                    <form:input path="rid" id="rid" size="15" cssClass="name_txt" />
                </div>
                <div class="row_field">
                    <label class="profile_label">Message</label>
                    <form:input path="message" id="message" size="100" cssClass="name_txt" />
                </div>

                <input type="submit" value="Send Notification" class="gd-button" style="width: 150px; margin: 20px 5px 10px 0; float: left;" name="sendNotification" />
            </form:form>
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED. (<spring:eval expression="@environmentProperty.getProperty('build.version')" />.<spring:eval expression="@environmentProperty.getProperty('server')" />)
    </div>
</div>
</body>
</html>