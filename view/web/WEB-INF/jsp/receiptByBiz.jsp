<%@ include file="include.jsp"%>
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
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            ${receiptByBizForm.bizNameForTitle}&nbsp;<fmt:message key="receipt.by.biz" />
                <c:if test="${receiptByBizForm.receiptLandingViews.size() gt 1}">s</c:if>
                for ${receiptByBizForm.monthYear}
        </h1>
    </div>
    <c:choose>
    <c:when test="${!empty receiptByBizForm.receiptLandingViews}">
    <div class="rightside-list-holder full-list-holder">
        <p class="analysis-text">
            <b>${receiptByBizForm.receiptLandingViews.size()}</b>
                transaction<c:if test="${receiptByBizForm.receiptLandingViews.size() gt 1}">s</c:if>
                occurred at ${receiptByBizForm.bizName} in the month.
        </p>

        <ul>
            <c:forEach var="receipt" items="${receiptByBizForm.receiptLandingViews}" varStatus="status">
            <li>
                <span class="rightside-li-right-text counter-li-text"><fmt:formatNumber value="${status.count}" pattern="00"/></span>
                <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
                <span style="background-color: ${receipt.expenseColor}" title="${receipt.expenseTag}">&nbsp;&nbsp;&nbsp;</span>
                <c:choose>
                <c:when test="${receipt.billedStatus eq 'NB'}">
                    <a href="/access/userprofilepreference/i.htm#tabs-3"
                            class="rightside-li-middle-text">
                        <c:choose>
                        <c:when test="${receipt.name.length() gt 34}">
                            <spring:eval expression="receipt.name.substring(0, 34)"/>...
                        </c:when>
                        <c:otherwise>
                            <spring:eval expression="receipt.name"/>
                        </c:otherwise>
                        </c:choose>
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm"
                            class="rightside-li-middle-text">
                        <c:choose>
                        <c:when test="${receipt.name.length() gt 34}">
                            <spring:eval expression="receipt.name.substring(0, 34)"/>...
                        </c:when>
                        <c:otherwise>
                            <spring:eval expression="receipt.name"/>
                        </c:otherwise>
                        </c:choose>
                    </a>
                </c:otherwise>
                </c:choose>
                <span class="rightside-li-right-text" style="width: 140px;">
                    <c:choose>
                        <c:when test="${receipt.splitTax gt 0}">
                            <spring:eval expression='receipt.splitTaxString'/>&nbsp;&nbsp;&nbsp;(T)
                        </c:when>
                        <c:otherwise>
                            &nbsp;&nbsp;&nbsp;
                        </c:otherwise>
                    </c:choose>
                </span>
                <span class="rightside-li-right-text" style="width: 140px;">
                    <spring:eval expression='receipt.splitTotalString'/>
                </span>
            </li>
            </c:forEach>
        </ul>
    </div>
    </c:when>
    <c:otherwise>
        <div style="height: 605px;">
            <div class="r-info">
                No receipt submitted with this business name for the selected month.
            </div>
        </div>
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
</body>
</html>
