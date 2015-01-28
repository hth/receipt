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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin1.css"/>
</head>
<body>
<div class="main_wrapper">
    <div class="header">
        <div class="header_wrapper">
            <div class="header_left_content">
                <div id="logo">
                    <h1>Receiptofi</h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" href="#">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">PROFILE</a>
                <a class="top-account-bar-text" href="#">REPORT</a>
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
<c:choose>
<c:when test="${!empty pendingReceiptForm.pending}">
<div class="rightside-title">
    <h1 class="rightside-title-text">
        Pending Document<c:if test="${pendingReceiptForm.pending.size() gt 1}">s</c:if>
    </h1>
</div>
<div class="rightside-list-holder full-list-holder">
    <ul>
        <c:forEach items="${pendingReceiptForm.pending}" var="receipt" varStatus="status">
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
<div class="rightside-title">
    <h1 class="rightside-title-text">
        No Pending Document
    </h1>
</div>
<div class="rightside-list-holder full-list-holder">
    <div class="first ajx-content">
        <img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>
        <p><strong>Please upload valid document to see something here.</strong></p>
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
