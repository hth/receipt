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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/5.0.0/highcharts.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
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
            All items for expense tag: ${expenseForm.name}
        </h1>
    </div>
    <c:choose>
    <c:when test="${!empty expenseForm.items}">
    <div class="rightside-list-holder full-list-holder">
        <p class="analysis-text">
            Found <b>${expenseForm.items.size()}</b>
            item<c:if test="${expenseForm.items.size() gt 1}">s</c:if>
            tagged under expense tag ${expenseForm.name}.
        </p>
        <ul>
            <form:form method="post" action="expenses.htm" modelAttribute="expenseForm">
            <c:forEach items="${expenseForm.items}" var="item" varStatus="status">
            <li>
                <span class="rightside-li-right-text counter-li-text"><fmt:formatNumber value="${status.count}" pattern="00"/></span>
                <span class="rightside-li-date-text" style="width: 100px;"><fmt:formatDate value="${item.receipt.receiptDate}" pattern="MMM dd, yyyy"/></span>
                <span style="background-color: ${item.expenseTag.tagColor}" title="${item.expenseTag.tagName}">&nbsp;&nbsp;&nbsp;</span>
                <c:choose>
                    <c:when test="${item.receipt.billedStatus eq 'NB'}">
                        <a href="/access/userprofilepreference/i.htm#tabs-3"
                                class="rightside-li-middle-text" style="width: 250px;">
                            <c:choose>
                                <c:when test="${item.receipt.bizName.businessName.length() gt 34}">
                                    <spring:eval expression="item.receipt.bizName.businessName.substring(0, 34)"/>...
                                </c:when>
                                <c:otherwise>
                                    <spring:eval expression="item.receipt.bizName.businessName"/>
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/access/receipt/${item.receipt.id}.htm"
                                class="rightside-li-middle-text" style="width: 250px;">
                            <c:choose>
                                <c:when test="${item.receipt.bizName.businessName.length() gt 34}">
                                    <spring:eval expression="item.receipt.bizName.businessName.substring(0, 34)"/>...
                                </c:when>
                                <c:otherwise>
                                    <spring:eval expression="item.receipt.bizName.businessName"/>
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm" style="width: 250px;">
                    ${item.nameAbb}
                </a>
                <span class="rightside-li-right-text" style="width: 50px;">
                    <c:choose>
                        <c:when test="${item.tax gt 0}">
                            <spring:eval expression='item.tax'/>&nbsp;(T)
                        </c:when>
                        <c:otherwise>
                            &nbsp;
                        </c:otherwise>
                    </c:choose>
                </span>
                <span class="rightside-li-right-text" style="width: 80px;">
                    <spring:eval expression='item.priceString'/>
                </span>
                <span class="receipt-tag" style="margin-left: -5px;">
                    <form:select path="items[${status.index}].expenseTag.id">
                        <form:option value="NONE" label="SELECT" />
                        <form:options items="${expenseForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                    </form:select>
                </span>
            </li>
            </c:forEach>
            </form:form>
        </ul>
    </div>
    </c:when>
    <c:otherwise>
    <div class="r-error">
        No item tagged under expense tag: <b>${expenseForm.name}</b>
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
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</body>
</html>
