<%@ include file="/WEB-INF/jsp/include.jsp" %>
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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/jquery/jquery-ui-1.10.4.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/raphael/raphael-min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/dynamic_list_helper2.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>

    <script>
        /* add background color to holder in tr tag */
        window.onload = function () {
            <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
            fetchReceiptImage('${pageContext.request.contextPath}/access/filedownload/receiptimage/${arr.blobId}.htm', "holder_" + ${status.index}, '${arr.id}', ${arr.imageOrientation}, '${arr.blobId}', '${receiptDocumentForm.receiptDocument.receiptUserId}');
            </c:forEach>
        };
    </script>
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
                <a class="top-account-bar-text" href="#">PROFILE</a>
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
<c:when test="${!empty receiptDocumentForm.receiptDocument}">

    <spring:eval var="documentStat" expression="receiptDocumentForm.receiptDocument.documentStatus == T(com.receiptofi.domain.types.DocumentStatusEnum).REJECT"/>

    <div class="rightside-title">
        <h1 class="rightside-title-text">
            <c:choose>
                <c:when test="${!documentStat}">
                    Document pending
                </c:when>
                <c:otherwise>
                    Document rejected
                </c:otherwise>
            </c:choose>
        </h1>
    </div>

    <div class="blank-space">&nbsp;</div>

    <c:if test="${!empty receiptDocumentForm.errorMessage}">
        <%--Currently this section of code is not executed unless the error message is added to the form directly without using 'result' --%>
        <div class="first ajx-content" id="existingErrorMessage">
            <img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>
            <p><strong>${receiptDocumentForm.errorMessage}</strong></p>
        </div>
        <div class="blank-space">&nbsp;</div>
    </c:if>

    <div class="margin-left">
    <c:choose>
    <c:when test="${empty receiptDocumentForm.receiptDocument.referenceDocumentId}">
        <form:form method="post" action="delete.htm" modelAttribute="receiptDocumentForm">
            <form:hidden path="receiptDocument.documentStatus"/>
            <form:hidden path="receiptDocument.referenceDocumentId"/>
            <form:hidden path="receiptDocument.id"/>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button class="gd-button" name="delete" id="deleteId">DELETE</button>
        </form:form>
    </c:when>
    <c:otherwise>
        <div class="first ajx-content">
            <img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>
            <p><strong>This receipt is in the process of being Re-Checked.</strong></p>
        </div>
        <div class="blank-space">&nbsp;</div>
    </c:otherwise>
    </c:choose>
    </div>

    <div class="rightside-list-holder full-list-holder">
        <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
        <div id="holder_${status.index}" style="height: 850px; border-color:#ff0000 #0000ff;"></div>
        </c:forEach>
    </div>
</c:when>
<c:otherwise>
<div class="rightside-list-holder full-list-holder">
    <div class="first ajx-content">
        <img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>
        <c:choose>
        <c:when test="${isTech}">
        <p><strong>Oops! Seems like user has deleted this receipt recently.</strong></p>
        </c:when>
        <c:otherwise>
        <p><strong>No receipt found!! Please hit back button and submit a valid request</strong></p>
        </c:otherwise>
        </c:choose>
    </div>
    <div class="blank-space">&nbsp;</div>
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
