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
</head>
<body>
<header>
    <div class="top-account-bar">
        <ul>
            <li><a class="top-account-bar-text" href="#">LOG OUT</a></li>
            <li><a class="top-account-bar-text" href="#">PROFILE</a></li>
            <li>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
            </li>
        </ul>
    </div>
    <div class="nav-hold">
        <h1>Receiptofi</h1>
    </div>
</header>
<div class="main clearfix">
<c:choose>
    <c:when test="${!empty pendingReceiptForm.rejected}">
        <div class="rightside-title">
            <h1 class="rightside-title-text">
                Rejected Document<c:if test="${pendingReceiptForm.rejected.size() gt 1}">s</c:if>
            </h1>
        </div>
        <div class="rightside-list-holder full-list-holder">
            <ul>
                <c:forEach items="${pendingReceiptForm.rejected}" var="receipt" varStatus="status">
                <li>
                <span class="rightside-li-right-text counter-li-text">
                    ${status.count}
                </span>
                <span class="rightside-li-date-text full-li-date-text">
                    <fmt:formatDate value="${receipt.documentEntity.created}" type="both"/>
                </span>
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
                No Rejected Document
            </h1>
        </div>
        <div class="rightside-list-holder full-list-holder">
            When document is unclear or not a valid receipt it will show up here.
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
