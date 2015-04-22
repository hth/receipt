<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>Receiptofi, Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.1.4/highcharts.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.1.4/modules/exporting.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
</head>

<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/display/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/display/cronStats.htm">STATS</a>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username"/>
                </a>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
    <div class="rightside-list-holder" style="height: 850px; width: 940px;">
        <c:forEach items="${cronStatsForm.taskStats}" var="stats" varStatus="status">
        <div class="receipt-detail-holder border" style="padding-bottom: 20px;">
            <p class="analysis" style="padding-bottom: 10px;">${stats.key}</p>

            <table width="99%" style="margin-left: 4px; margin-right: 4px;">
                <tr style="border-bottom: 1px dotted #919191;">
                    <th class="analysis">DATE</th>
                    <th class="analysis">RUN</th>
                    <th class="analysis">DURATION</th>
                    <c:forEach items="${stats.value}" var="cronStatsValue1" varStatus="status">
                        <c:forEach items="${cronStatsValue1.stats}" var="statKey" varStatus="status">
                            <th class="analysis">${statKey.key}</th>
                        </c:forEach>
                    </c:forEach>
                </tr>
                <c:forEach items="${stats.value}" var="cronStatsValue2" varStatus="status">
                <tr>
                    <td class="analysis"><fmt:formatDate value="${cronStatsValue2.created}" pattern="MMM. dd HH:mm:ss:SSS" /></td>
                    <td class="analysis">${cronStatsValue2.processStatus}</td>
                    <td class="analysis">${cronStatsValue2.duration}</td>
                    <c:forEach items="${cronStatsValue2.stats}" var="statValue" varStatus="status">
                        <td class="analysis">${statValue.value}</td>
                    </c:forEach>
                </tr>
                </c:forEach>
            </table>

        </div>
        </c:forEach>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-left"></div>
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>