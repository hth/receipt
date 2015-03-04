<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
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
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm" style="color: red">
                            <%--show alert when email not validated--%>
                            <%--http://dabblet.com/gist/1576546--%>
                            <sec:authentication property="principal.username" />
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
<c:choose>
<c:when test="${!empty notificationForm.notifications}">
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            <fmt:message key="notification.title" /><c:if test="${notificationForm.notifications.size() gt 1}">S</c:if>
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder">
        <ul>
            <c:forEach var="notification" items="${notificationForm.notifications}" varStatus="status">
            <li>
                <span class="rightside-li-right-text counter-li-text"><fmt:formatNumber value="${status.count}" pattern="00"/></span>
                <span class="rightside-li-date-text full-li-date-text"><fmt:formatDate value="${notification.created}" type="both"/></span>
                ${notification.notificationMessage}
            </li>
            </c:forEach>
        </ul>
    </div>
</c:when>
<c:otherwise>
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            <fmt:message key="notification.title" />
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder">
        <div class="first ajx-content">
            <img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>
            <p><strong>There are no notification.</strong></p>
        </div>
    </div>
</c:otherwise>
</c:choose>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>
</body>
</html>
