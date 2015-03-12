<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="item.analytic.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.0.4/highcharts.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
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
            Historical Analysis
        </h1>
    </div>
    <c:if test="${!empty itemAnalyticForm.message}">
    <div class="r-info">
        ${itemAnalyticForm.message}
    </div>
    </c:if>
    <c:choose>
    <c:when test="${!empty itemAnalyticForm.yourHistoricalItems}">
    <div class="rightside-list-holder" style="height: 850px; width: 940px;">
        <div class="receipt-detail-holder border">
            <p class="analysis-text">
                Below is the analysis for <b>${itemAnalyticForm.item.name}</b> bought on
                <b><fmt:formatDate value="${itemAnalyticForm.item.receipt.receiptDate}" type="date"/></b>. The chart
                shows pricing of the same item bought by you with respect to same item bought by loyal customers like
                you over same ${itemAnalyticForm.days} days period.
            </p>
            <p class="analysis-text">
                Your purchase history shows, you have purchased <b>${itemAnalyticForm.item.name}</b> at least
                <b>${itemAnalyticForm.historicalCount}</b>
                time<c:if test="${itemAnalyticForm.historicalCount gt 1}">s</c:if>.
            </p>

            <table width="95%" style="margin-left: 4px; margin-right: 4px;">
                <tr style="border-bottom: 1px dotted #919191;">
                    <th class="analysis" style="width: 50px;">Date</th>
                    <th class="analysis">Business</th>
                    <th class="analysis">Location</th>
                    <th class="analysis">Price</th>
                    <th class="analysis">Your Average</th>
                    <th class="analysis">Site's Average</th>
                </tr>
                <tr>
                    <td class="analysis" style="width: 60px;">
                        <fmt:formatDate value="${itemAnalyticForm.item.receipt.receiptDate}" type="date"/>
                        <a href="/access/userprofilepreference/i.htm#tabs-2" class="expense-tag" title="${item.expenseTag.tagName} Expense Tag">
                        <span style="background-color: ${itemAnalyticForm.item.expenseTag.tagColor}; margin-left: 15px;">&nbsp;&nbsp;</span>
                        </a>
                    </td>
                    <td class="analysis">
                        <a href="${pageContext.request.contextPath}/access/receipt/${itemAnalyticForm.item.receipt.id}.htm" style="color: #007AFF">
                            ${itemAnalyticForm.item.receipt.bizName.businessName}
                        </a>
                    </td>
                    <td class="analysis">
                        ${itemAnalyticForm.item.receipt.bizStore.location}
                    </td>
                    <td class="analysis">
                        <spring:eval expression="itemAnalyticForm.item.price" />
                    </td>
                    <td class="analysis">
                        <fmt:formatNumber value="${itemAnalyticForm.yourAveragePrice}" type="currency" />
                    </td>
                    <td class="analysis">
                        <fmt:formatNumber value="${itemAnalyticForm.siteAveragePrice}" type="currency" />
                    </td>
                </tr>
            </table>

            <div id="container" style="min-width: 600px; max-width: 905px; height: 275px; margin: 35px 10px 20px 0px;"></div>

            <c:if test="${!empty itemAnalyticForm.yourHistoricalItems}">
            <c:choose>
                <c:when test="${itemAnalyticForm.yourHistoricalItems.size() gt 10}">
                    <h2 class="h2" style="padding-top: 3%;">
                        Your historical purchase<c:if test="${itemAnalyticForm.yourHistoricalItems.size() gt 1}">s</c:if>
                        of ${itemAnalyticForm.item.name}.
                    </h2>
                    <p class="analysis-text">
                        Table below shows ${itemAnalyticForm.yourHistoricalItems.size()} of ${itemAnalyticForm.historicalCount} items.
                    </p>
                </c:when>
                <c:otherwise>
                    <h2 class="h2" style="padding-bottom:3%; padding-top: 3%;">
                        Your historical purchase<c:if test="${itemAnalyticForm.yourHistoricalItems.size() gt 1}">s</c:if>
                        of ${itemAnalyticForm.item.name}.
                    </h2>
                </c:otherwise>
            </c:choose>

            <table width="95%" style="margin-left: 4px; margin-right: 4px;">
                <tr style="border-bottom: 1px dotted #919191;">
                    <th class="analysis" style="width: 5px !important;"></th>
                    <th class="analysis" style="width: 40px !important;">Date</th>
                    <th class="analysis">Business</th>
                    <th class="analysis">Location</th>
                    <th class="analysis">Price</th>
                </tr>
                <c:forEach items="${itemAnalyticForm.yourHistoricalItems}" var="item" varStatus="status">
                <tr>
                    <td class="analysis" style="width: 5px !important;">
                        ${status.count}.
                    </td>
                    <td class="analysis" style="width: 40px !important;">
                        <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                        <a href="/access/userprofilepreference/i.htm#tabs-2" class="expense-tag" title="${item.expenseTag.tagName} Expense Tag">
                        <span style="background-color: ${item.expenseTag.tagColor}; margin-left: 15px;">&nbsp;&nbsp;</span>
                        </a>
                    </td>
                    <td class="analysis">
                        <a href="${pageContext.request.contextPath}/access/receipt/${item.receipt.id}.htm" style="color: #007AFF">
                            ${item.receipt.bizName.businessName}
                        </a>
                    </td>
                    <td class="analysis">
                        ${item.receipt.bizStore.location}
                    </td>
                    <td class="analysis">
                        <spring:eval expression="item.price" />
                        <spring:eval expression="item.taxed == T(com.receiptofi.domain.types.TaxEnum).TAXED" var="isValid" />
                        <c:choose>
                            <c:when test="${!isValid}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                &nbsp; + (TAX)
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                </c:forEach>
            </table>
            </c:if>
        </div>
    </div>
</c:when>
<c:otherwise>
    <div class="r-info">
        No item found. Please hit back button and submit a valid request.
    </div>
    <div class="rightside-list-holder full-list-holder" style="width: 95%;">&nbsp;</div>
</c:otherwise>
</c:choose>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>

<c:if test="${!empty itemAnalyticForm.yourHistoricalItems}">
<script>
    $(function () {
        $('#container').highcharts({
            chart: {
                type: 'spline'
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Site ${itemAnalyticForm.days} days vs. Historical ' +
                '${itemAnalyticForm.days} days for ${itemAnalyticForm.item.name}'
            },
            xAxis: {
                type: 'datetime',
                dateTimeLabelFormats: { // don't display the dummy year
                    month: '%e. %b',
                    year: '%b'
                }
            },
            yAxis: {
                title: {
                    text: 'Price'
                },
                min: 0
            },
            plotOptions: {
                spline: {
                    lineWidth: 3,
                    states: {
                        hover: {
                            lineWidth: 4
                        }
                    },
                    marker: {
                        enabled: true
                    }
                }
            },
            series: [
                {
                    name: 'Your ${itemAnalyticForm.days} days average',
                    data: [
                        <c:forEach items="${itemAnalyticForm.yourAverageItems}" var="item" varStatus="status">
                        [Date.UTC(${item.receipt.year},  ${item.receipt.month - 1}, ${item.receipt.day}), ${item.price} ],
                        </c:forEach>
                    ],
                    color: '${itemAnalyticForm.item.expenseTag.tagColor}'
                },
                {
                    name: 'Site\'s ${itemAnalyticForm.days} days average',
                    // Define the data points. All series have a dummy year
                    // of 1970/71 in order to be compared on the same x axis. Note
                    // that in JavaScript, months start at 0 for January, 1 for February etc.
                    data: [
                        <c:forEach items="${itemAnalyticForm.siteAverageItems}" var="item" varStatus="status">
                        [Date.UTC(${item.receipt.year},  ${item.receipt.month - 1}, ${item.receipt.day}), ${item.price} ],
                        </c:forEach>
                    ]
                }
            ]
        });
    });
</script>
</c:if>
<!-- cd-popup -->
<%--<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css">--%>
<!-- Resource style -->
<%--<script src="${pageContext.request.contextPath}/static/js/modernizr.js"></script>--%>
<!-- Modernizr -->
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<!-- Resource jQuery -->
</body>
</html>
