<%@ include file="include.jsp"%>
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
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
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
<c:choose>
<c:when test="${!empty pendingReceiptForm.rejected}">
<div class="rightside-title rightside-title-less-margin">
    <h1 class="rightside-title-text">
        Rejected Document<c:if test="${pendingReceiptForm.rejected.size() gt 1}">s</c:if>
    </h1>
</div>
<div class="rightside-list-holder full-list-holder">
    <ul>
        <c:forEach items="${pendingReceiptForm.rejected}" var="receipt" varStatus="status">
        <li>
            <span class="rightside-li-right-text counter-li-text">${status.count}</span>
            <span class="rightside-li-date-text full-li-date-text"><fmt:formatDate value="${receipt.documentEntity.updated}" type="both"/></span>
            <a href="${pageContext.request.contextPath}/access/document/${receipt.documentEntity.id}.htm" class="rightside-li-middle-text full-li-middle-text">
                ${receipt.fileName}
            </a>
        </li>
        </c:forEach>
    </ul>
</div>
</c:when>
<c:otherwise>
<div class="rightside-title rightside-title-less-margin">
    <h1 class="rightside-title-text">
        No Rejected Document
    </h1>
</div>
<div class="r-info">
    When document is unclear or not a valid receipt it will show up here. Currently there is no document rejected.
</div>
<div class="rightside-list-holder full-list-holder"></div>
</c:otherwise>
</c:choose>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>
