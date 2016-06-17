<%@ include file="../../../jsp/include.jsp"%>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<spring:eval expression="pageContext.request.userPrincipal.principal.userLevel eq T(com.receiptofi.domain.types.UserLevelEnum).BUSINESS_SMALL" var="hasAccess" />
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/business/landing.htm">Receiptofi</a></h1>
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
                <c:if test="${hasAccess}">
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                </c:if>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username" />
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username" />
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
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
        <sec:authorize access="hasRole('ROLE_BUSINESS')">
        <div class="business_reg">
            <div class="down_form" style="width: 90%">
                <form:form commandName="businessRegistration">
                    <h1 class="h1">Please complete your profile</h1>
                    <hr>
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

                    <spring:hasBindErrors name="businessRegistration">
                    <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                        <ul>
                            <c:if test="${errors.hasFieldErrors('firstName')}">
                                <li><form:errors path="firstName" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('lastName')}">
                                <li><form:errors path="lastName" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('address')}">
                                <li><form:errors path="address" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('phone')}">
                                <li><form:errors path="phone" /></li>
                            </c:if>
                        </ul>
                    </div>
                    </spring:hasBindErrors>

                    <div class="row_field">
                        <form:label path="firstName" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">First name</form:label>
                        <form:input path="firstName" size="20" cssClass="name_txt" />
                    </div>
                    <div class="row_field">
                        <form:label path="lastName" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Last name</form:label>
                        <form:input path="lastName" size="20" cssClass="name_txt" />
                    </div>
                    <div class="row_field">
                        <form:label path="address" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Your Address</form:label>
                        <form:input path="address" size="200" cssClass="name_txt" cssStyle="width: 600px;" />
                    </div>
                    <div class="row_field">
                        <form:label path="phone" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Your Phone</form:label>
                        <form:input path="phone" size="20" cssClass="name_txt" />
                    </div>

                    <c:if test="${!businessRegistration.emailValidated}">
                    <div class="profile_label profile_label_note">
                        <p>
                        Your email address <span style="color: red; font-weight: bold">${businessRegistration.email}</span>
                        has not been validated. Please validated email address to continue business account registration.
                        </p>
                        <p style="padding-top: 10px;">
                        To resend account validation email, click here.
                        </p>
                    </div>
                    </c:if>

                    <div class="full">
                        <c:if test="${businessRegistration.emailValidated}">
                        <input type="submit" value="NEXT" class="read_btn" name="_eventId_submit"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        </c:if>
                        <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel"
                                style="background: #FC462A; margin: 77px 10px 0 0;">
                    </div>
                </form:form>
            </div>
        </div>
        </sec:authorize>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>