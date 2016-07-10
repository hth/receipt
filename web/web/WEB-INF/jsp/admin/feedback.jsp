<%@ include file="../include.jsp"%>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.admin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.0.0/jquery-migrate.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
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
                            <sec:authentication property="principal.username"/>
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username"/>
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
            Feedback
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder"
            style="overflow-y: hidden; height: 700px; margin-left: 0; padding-left: 0;">
        <div class="down_form" style="width: 96%;">
            <c:choose>
            <c:when test="${!empty eval}">
            <table width="100%" style="margin: 0 4px 0 4px">
                <tr>
                    <th style="text-align: left;"></th>
                    <th style="text-align: left;">User</th>
                    <th style="text-align: left;">Feedback</th>
                    <th style="text-align: left;">Rating</th>
                    <th style="text-align: left;">Last Modified</th>
                </tr>
                <c:forEach items="${eval}" var="item" varStatus="status">
                <tr>
                    <td style="padding: 10px; border: 1px solid #ccc">${status.count}&nbsp;</td>
                    <td style="padding: 10px; border: 1px solid #ccc">${item.receiptUserId}</td>
                    <td style="padding: 10px; border: 1px solid #ccc">${item.feedback}</td>
                    <td style="padding: 10px; border: 1px solid #ccc">${item.rating}</td>
                    <td style="padding: 10px; border: 1px solid #ccc">
                        <span style="color: #6E6E6E;font-weight: normal;"><fmt:formatDate pattern="MMMM dd, yyyy" value="${item.updated}"/></span>
                    </td>
                </tr>
                </c:forEach>
            </table>
            </c:when>
            <c:otherwise>
                There are no feedback's to read.
            </c:otherwise>
            </c:choose>
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED. (<fmt:message key="build.version" />)
    </div>
</div>
</body>
</html>