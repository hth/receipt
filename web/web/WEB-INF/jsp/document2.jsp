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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
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
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">PROFILE</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
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

    <c:choose>
    <c:when test="${!empty receiptDocumentForm.errorMessage}">
    <div class="r-error" id="existingErrorMessage">
        ${receiptDocumentForm.errorMessage}
    </div>
    </c:when>
    <c:otherwise>
    <div class="blank-space">&nbsp;</div>
    </c:otherwise>
    </c:choose>

    <c:choose>
    <c:when test="${empty receiptDocumentForm.receiptDocument.referenceDocumentId}">
        <div class="margin-left">
        <form:form method="post" action="delete.htm" modelAttribute="receiptDocumentForm">
            <form:hidden path="receiptDocument.documentStatus"/>
            <form:hidden path="receiptDocument.referenceDocumentId"/>
            <form:hidden path="receiptDocument.id"/>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button class="gd-button" name="delete" id="deleteId">DELETE</button>
        </form:form>
        </div>
    </c:when>
    <c:otherwise>
        <div class="r-info">
            This document is in the process of being Re-Checked.
        </div>
    </c:otherwise>
    </c:choose>

    <div class="rightside-list-holder full-list-holder">
        <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
        <div id="holder_${status.index}" style="height: 850px; border-color:#ff0000 #0000ff;"></div>
        </c:forEach>
    </div>
</c:when>
<c:otherwise>
<div class="r-error">
<c:choose>
    <c:when test="${isTech}">
        Oops! Seems like user has deleted this receipt recently.
    </c:when>
    <c:otherwise>
        No document found. Please hit back button and submit a valid request.
    </c:otherwise>
</c:choose>
</div>
<div class="rightside-list-holder full-list-holder">&nbsp;</div>
</c:otherwise>
</c:choose>

<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>
</body>
</html>
