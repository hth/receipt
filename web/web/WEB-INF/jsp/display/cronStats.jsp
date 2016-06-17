<%@ include file="../include.jsp"%>
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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/highcharts.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/modules/exporting.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
</head>

<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/display/landing.htm">Receiptofi</a></h1>
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
        <c:forEach items="${cronStatsForm.taskStats}" var="taskStats" varStatus="status">
        <div class="receipt-detail-holder border" style="padding-bottom: 20px;">
            <p class="analysis" style="padding-bottom: 10px;">${taskStats.key}</p>

            <table width="99%" style="margin-left: 4px; margin-right: 4px;">
                <tr style="border-bottom: 1px dotted #919191;">
                    <th class="analysis">DATE</th>
                    <th class="analysis">RUN</th>
                    <th class="analysis">DURATION</th>
                    <c:set var="cronStatsValue1" value="${taskStats.value.get(0)}" property="T(com.receiptofi.domain.CronStatsEntity)"/>
                    <c:forEach items="${cronStatsValue1.stats}" var="statKey" varStatus="status">
                        <th class="analysis">${statKey.key}</th>
                    </c:forEach>
                </tr>
                <c:forEach items="${taskStats.value}" var="cronStatsValue2" varStatus="status">
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
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>