<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="receipt.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.admin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.0.0/jquery-migrate.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<div class="clear"></div>
<spring:eval expression="pageContext.request.userPrincipal.principal.userLevel ge T(com.receiptofi.domain.types.UserLevelEnum).SUPERVISOR" var="hasAccess" />
<div>
    <div class="header_main">
        <div class="header_wrappermain">
            <div class="header_wrapper">
                <div class="header_left_contentmain">
                    <div id="logo">
                        <h1><a href="/access/landing.htm"><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/>Receiptofi</a></h1>
                    </div>
                </div>
                <div class="header_right_login">
                    <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                        <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                            <input type="submit" value="LOG OUT" class="logout_btn"/>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        </form>
                    </a>
                    <c:if test="${hasAccess}">
                    <a class="top-account-bar-text" href="/emp/receiptQuality.htm">RECEIPT QUALITY</a>
                    </c:if>
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
                Pending Receipt(s)
            </h1>
        </div>
        <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: auto; min-height: auto;">
            <div class="down_form" style="width: 95%;">
                <c:if test="${!empty pending}">
                <table style="width: 600px" class="etable">
                    <tbody>
                    <tr>
                        <th style="padding:5px;"></th>
                        <th style="padding:5px;">User Type</th>
                        <th style="padding:5px;">Created</th>
                        <th style="padding:5px;">Pending Since</th>
                        <th style="padding:5px;">Edit</th>
                    </tr>
                    </tbody>
                    <c:forEach var="receipt" items="${pending}"  varStatus="status">
                        <tr>
                            <td style="padding:5px; text-align: right">${status.count}</td>
                            <td style="padding:5px;">
                                <spring:eval expression="receipt.level" />
                            </td>
                            <td style="padding:5px;">
                                <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                            </td>
                            <td style="padding:5px;">
                                <span class="timestamp"><fmt:formatDate value="${receipt.created}" type="both"/></span>
                            </td>
                            <td style="padding:5px; text-align: right" title="${receipt.documentId}">
                                <a href="${pageContext.request.contextPath}/emp/update/${receipt.documentId}.htm" target="_blank">
                                    Open
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                </c:if>
            </div>
        </div>

        <div class="rightside-title rightside-title-less-margin">
            <h1 class="rightside-title-text">
                Queued Receipt(s)
            </h1>
        </div>
        <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: auto; min-height: auto;">
            <div class="down_form" style="width: 95%;">
                <c:if test="${!empty queue}">
                <table style="width: 600px" class="etable">
                    <tbody>
                    <tr>
                        <th style="padding:5px;"></th>
                        <th style="padding:5px;">User Type</th>
                        <th style="padding:5px;">Created</th>
                        <th style="padding:5px;">Pending Since</th>
                        <th style="padding:5px;">Edit</th>
                    </tr>
                    </tbody>
                    <c:forEach var="receipt" items="${queue}" varStatus="status">
                        <tr>
                            <td style="padding:5px;">${status.count}</td>
                            <td style="padding:5px;">
                                <spring:eval expression="receipt.level" />
                            </td>
                            <td style="padding:5px;">
                                <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                            </td>
                            <td style="padding:5px;">
                                <span class="timestamp"><fmt:formatDate value="${receipt.created}" type="both"/></span>
                            </td>
                            <td style="padding:5px; text-align: right" title="${receipt.documentId}">
                                <a href="${pageContext.request.contextPath}/emp/update/${receipt.documentId}.htm">
                                    Open
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                </c:if>
            </div>
        </div>

        <div class="rightside-title rightside-title-less-margin">
            <h1 class="rightside-title-text">
                Re-Check Pending Receipt(s)
            </h1>
        </div>
        <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: auto; min-height: auto;">
            <div class="down_form" style="width: 95%;">
                <c:if test="${!empty recheckPending}">
                <table style="width: 600px" class="etable">
                    <tbody>
                    <tr>
                        <th style="padding:5px;"></th>
                        <th style="padding:5px;">User Type</th>
                        <th style="padding:5px;">Created</th>
                        <th style="padding:5px;">Pending Since</th>
                        <th style="padding:5px;">Edit</th>
                    </tr>
                    </tbody>
                    <c:forEach var="receipt" items="${recheckPending}"  varStatus="status">
                        <tr>
                            <td style="padding:5px;">${status.count}</td>
                            <td style="padding:5px;">
                                <spring:eval expression="receipt.level" />
                            </td>
                            <td style="padding:5px;">
                                <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                            </td>
                            <td style="padding:5px;">
                                <span class="timestamp"><fmt:formatDate value="${receipt.created}" type="both"/></span>
                            </td>
                            <td style="padding:5px; text-align: right" title="${receipt.documentId}">
                                <a href="${pageContext.request.contextPath}/emp/recheck/${receipt.documentId}.htm">
                                    Open
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                </c:if>
            </div>
        </div>

        <div class="rightside-title rightside-title-less-margin">
            <h1 class="rightside-title-text">
                Re-Check Receipt(s)
            </h1>
        </div>
        <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: auto; min-height: auto;">
            <div class="down_form" style="width: 95%;">
                <c:if test="${!empty recheck}">
                <table style="width: 600px" class="etable">
                    <tbody>
                    <tr>
                        <th style="padding:5px;"></th>
                        <th style="padding:5px;">User Type</th>
                        <th style="padding:5px;">Created</th>
                        <th style="padding:5px;">Pending Since</th>
                        <th style="padding:5px;">Edit</th>
                    </tr>
                    </tbody>
                    <c:forEach var="receipt" items="${recheck}"  varStatus="status">
                        <tr>
                            <td style="padding:5px;">${status.count}</td>
                            <td style="padding:5px;">
                                <spring:eval expression="receipt.level" />
                            </td>
                            <td style="padding:5px;">
                                <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                            </td>
                            <td style="padding:5px;">
                                <span class="timestamp"><fmt:formatDate value="${receipt.created}" type="both"/></span>
                            </td>
                            <td style="padding:5px; text-align: right" title="${receipt.documentId}">
                                <a href="${pageContext.request.contextPath}/emp/recheck/${receipt.documentId}.htm">
                                    Open
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                </c:if>
            </div>
        </div>

        <div class="footer-tooth clearfix">
            <div class="footer-tooth-middle"></div>
            <div class="footer-tooth-right"></div>
        </div>
    </div>

    <div class="detail-view-container">

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