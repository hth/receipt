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
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/0.5.0/sweet-alert.css"/>

    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/raphael/2.1.2/raphael-min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/0.5.0/sweet-alert.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>

    <script>
        /* add background color to holder in tr tag */
        window.onload = function () {
            <c:choose>
                <c:when test="${!empty receiptDocumentForm.receiptDocument.referenceDocumentId}">
                    <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
                        fetchReceiptImage(
                        'https://s3-us-west-2.amazonaws.com/<spring:eval expression="@environmentProperty.getProperty('aws.s3.bucketName')" />/<spring:eval expression="@environmentProperty.getProperty('aws.s3.bucketName')" />/${arr.key}',
                        "holder_" + ${status.index},
                        '${arr.id}',
                        ${arr.imageOrientation},
                        '${arr.blobId}',
                        '${receiptDocumentForm.receiptDocument.receiptUserId}');
                    </c:forEach>
                </c:when>
                <c:otherwise>
                <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
                    fetchReceiptImage(
                        '${pageContext.request.contextPath}/access/filedownload/receiptimage/${arr.blobId}.htm',
                        "holder_" + ${status.index},
                        '${arr.id}',
                        ${arr.imageOrientation},
                        '${arr.blobId}',
                        '${receiptDocumentForm.receiptDocument.receiptUserId}');
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        };
    </script>
    <script>
        $(document).ready(function() {
            confirmBeforeAction();
        });
    </script>
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
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
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
            <button class="gd-button" name="delete" id="deletePendingDocument" style="float: left !important;">DELETE</button>
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
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</body>
</html>
