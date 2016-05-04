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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<div class="clear"></div>
<div>
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
                Receipt
            </h1>
        </div>
        <div style="height: 605px;">
            <div class="r-success" style="display: none;"></div>
            <div class="r-error" style="display: none;"></div>
        </div>
        <div class="footer-tooth clearfix">
            <div class="footer-tooth-middle"></div>
            <div class="footer-tooth-right"></div>
        </div>
    </div>

    <div class="detail-view-container">

    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</body>
</html>