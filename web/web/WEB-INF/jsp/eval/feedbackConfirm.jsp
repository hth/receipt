<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <sec:authorize access="hasRole('ROLE_ADMIN')" var="isAdmin" />
    <sec:authorize access="hasRole('ROLE_BUSINESS')" var="isBusiness" />
    <sec:authorize access="hasRole('ROLE_ENTERPRISE')" var="isEnterprise" />
    <sec:authorize access="hasRole('ROLE_SUPERVISOR')" var="isSupervisor" />
    <sec:authorize access="hasRole('ROLE_TECHNICIAN')" var="isTechnician" />
    <sec:authorize access="hasRole('ROLE_USER')" var="isUser" />
    <spring:eval expression="pageContext.request.userPrincipal.principal.userLevel eq T(com.receiptofi.domain.types.UserLevelEnum).BUSINESS_SMALL" var="isSmallBusiness"  />

    <c:choose>
        <c:when test="${isAdmin}">
            <!-- Higher to lower roles -->
            <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/admin/landing.htm">
        </c:when>
        <c:when test="${isBusiness}">
            <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/business/landing.htm">
        </c:when>
        <c:when test="${isEnterprise}">
            <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/enterprise/landing.htm">
        </c:when>
        <c:when test="${isSupervisor}">
            <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/emp/landing.htm">
        </c:when>
        <c:when test="${isTechnician}">
            <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/emp/landing.htm">
        </c:when>
        <c:otherwise>
            <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/access/landing.htm">
        </c:otherwise>
    </c:choose>

    <title><fmt:message key="feedback.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/raty/jquery.raty.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>

                <c:choose>
                    <c:when test="${isAdmin}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:when test="${isBusiness && !isSmallBusiness}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                    </c:when>
                    <c:when test="${isSmallBusiness}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:when test="${isEnterprise}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                    </c:when>
                    <c:when test="${isSupervisor}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:when test="${isTechnician}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:otherwise>
                </c:choose>
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
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            <fmt:message key="feedback.title"/>
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder">
        <h2 class="h2">Thank you for providing valuable feedback.</h2>

        <br/><br/><br/>

        <h3 class="h3">
            <c:choose>
                <c:when test="${isAdmin}">
                <a href="${pageContext.request.contextPath}/admin/landing.htm">
                    Redirecting to home page in couple of seconds... If not redirected then please click here
                </a>
                </c:when>
                <c:when test="${isBusiness}">
                <a href="${pageContext.request.contextPath}/business/landing.htm">
                    Redirecting to home page in couple of seconds... If not redirected then please click here
                </a>
                </c:when>
                <c:when test="${isEnterprise}">
                <a href="${pageContext.request.contextPath}/enterprise/landing.htm">
                    Redirecting to home page in couple of seconds... If not redirected then please click here
                </a>
                </c:when>
                <c:when test="${isSupervisor}">
                <a href="${pageContext.request.contextPath}/emp/landing.htm">
                    Redirecting to home page in couple of seconds... If not redirected then please click here
                </a>
                </c:when>
                <c:when test="${isTechnician}">
                <a href="${pageContext.request.contextPath}/emp/landing.htm">
                    Redirecting to home page in couple of seconds... If not redirected then please click here
                </a>
                </c:when>
                <c:otherwise>
                <a href="${pageContext.request.contextPath}/access/landing.htm">
                    Redirecting to home page in couple of seconds... If not redirected then please click here
                </a>
                </c:otherwise>
            </c:choose>
        </h3>
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
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>