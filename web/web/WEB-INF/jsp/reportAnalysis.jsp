<%@ include file="/WEB-INF/jsp/include.jsp"%>
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
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>
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
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT</a>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
<div class="report_sidebar" id="reportSidebarId">
    <div class="sidebar-top-summary">
        <div class="sidebar-top-summary-upper clearfix">
            <ul>
                <c:set var="yearVar" value="" />
                <c:forEach var="item" items="${reportAnalysisForm.receiptGroupedByMonths}"  varStatus="status">
                <c:if test="${empty yearVar || yearVar ne item.year}">
                    <c:set var="yearVar" value="${item.year}" />
                    <c:if test="${yearVar eq item.year}">
                        <span class="ll-h">${item.year}</span>
                    </c:if>
                </c:if>

                <li>
                    <a href="${pageContext.request.contextPath}/access/landing/report/<spring:eval expression='item.dateTime.toString("MMM, yyyy")' />.htm" class="ll-t" target="_blank">
                        <spring:eval expression='item.dateTime.toString("MMM")' /> &nbsp;&nbsp; <spring:eval expression="item.total" />
                    </a>
                </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<div class="report_sidebar" id="analysisSidebarId">
    <div class="sidebar-top-summary">
        <div class="sidebar-top-summary-upper clearfix">
        </div>
    </div>
</div>

<div class="rightside-content">
    <div id="tabs" class="nav-list">
        <ul class="nav-block">
            <li><a href="#tab1" onclick="reportTabClicked();">REPORT</a></li>
            <li><a href="#tab2" onclick="analysisTabClicked();">ANALYSIS</a></li>
        </ul>
        <div id="tab1" class="report-content">
            <c:forEach var="receipts" items="${reportAnalysisForm.receiptListViews}"  varStatus="status">
            <div class="rightside-title report-title">
                <h1 class="rightside-title-text left">
                    <fmt:formatDate value="${receipts.date}" pattern="MMMM, yyyy"/>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <span style="color: #007aff;"><spring:eval expression="receipts.total" /></span>
                </h1>
            </div>
            <div class="rightside-list-holder rightside-list-holder-report">
                <ul>
                    <c:forEach var="receipt" items="${receipts.receiptListViewGroupedList}"  varStatus="status">
                    <li>
                        <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
                        <span style="background-color: ${receipt.expenseColor}" title="${receipt.expenseTagName}">&nbsp;&nbsp;&nbsp;</span>
                        <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm" class="rightside-li-middle-text">
                            <spring:eval expression="receipt.name"/>
                        </a>
                        <span class="rightside-li-right-text">
                            <spring:eval expression='receipt.total'/>
                        </span>
                    </li>
                    </c:forEach>
                </ul>
            </div>
            </c:forEach>
        </div>
        <div id="tab2" class="report-content">
            <c:choose>
                <c:when test="${!empty months}">
                    <div id="monthly" style="min-width: 475px; height: 425px; background-position: left 10px top;"></div>
                    <div id="allExpenseTypes" style="min-width: 525px; height: 420px; margin: 0 auto"></div>
                </c:when>
                <c:otherwise>
                    No expense analysis available as no receipt submitted or transformed
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>

<script>
    $("#analysisSidebarId").hide();

    <c:if test="${!empty months}">
    <!-- Monthly expense graph -->
    $(function () {
        "use strict";

        $('#monthly').highcharts({
            chart: {
                type: 'column',
                margin: [ 50, 50, 100, 50]
            },
            title: {
                text: 'Monthly Expenses for 13 months: ${months.get(months.size() - 1).year - 1} - ${months.get(months.size() - 1).year}'
            },
            credits: {
                enabled: false
            },
            xAxis: {
                categories: [
                    <c:forEach var="month" items="${months}"  varStatus="status">
                    '${month.monthName}',
                    </c:forEach>
                ],
                labels: {
                    rotation: -45,
                    align: 'right',
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Expenses in Dollar($)'
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b> ' +
                            'total expense : '+ Highcharts.numberFormat(this.y, 2) +
                            '$';
                }
            },
            series: [{
                name: 'Monthly Expense',
                data: [
                    <c:forEach var="month" items="${months}" varStatus="status">
                    {y: ${month.stringTotal}, color: 'skyblue'},
                    </c:forEach>
                ],
                dataLabels: {
                    enabled: false,
                    rotation: -90,
                    color: '#FFFFFF',
                    align: 'right',
                    x: 4,
                    y: 10,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    },
                    formatter:function(){
                        if(this.y > 0)
                            return this.y;
                    }
                }
            }]
        });
    });
    </c:if>

    <c:if test="${!empty itemExpenses}">
    $(function () {
        "use strict";

        $('#allExpenseTypes').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Expense Share'
            },
            subtitle: {
                text: 'For ${landingForm.receiptForMonth.year}'
            },
            tooltip: {
                formatter: function () {
                    return this.point.name + ': <b>' + Highcharts.numberFormat(this.percentage, 2) + '%</b>';
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        formatter: function() {
                            return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 2) +' %';
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Expense share',
                point: {
                    events: {
                        click: function(e) {
                            location.href = e.point.url;
                            e.preventDefault();
                        }
                    }
                },
                data: [

                    <c:choose>
                    <c:when test="${!empty itemExpenses}">
                    <c:set var="first" value="false"/>
                    <c:forEach var="item" items="${itemExpenses}"  varStatus="status">
                    <c:choose>
                    <c:when test="${first eq false}">
                    {
                        name: '${item.key}',
                        y: ${item.value},
                        sliced: true,
                        selected: true,
                        url: '${pageContext.request.contextPath}/access/expenses/${item.key}.htm'
                    },
                    <c:set var="first" value="true"/>
                    </c:when>
                    <c:otherwise>
                    {
                        name: '${item.key}',
                        y: ${item.value},
                        sliced: false,
                        selected: false,
                        url: '${pageContext.request.contextPath}/access/expenses/${item.key}.htm'
                    },
                    </c:otherwise>
                    </c:choose>
                    </c:forEach>
                    </c:when>
                    <c:otherwise>
                    <c:forEach var="item" items="${itemExpenses}"  varStatus="status">
                    {
                        name: '${item.key}',
                        y: ${item.value},
                        sliced: false,
                        selected: false,
                        url: '${pageContext.request.contextPath}/access/expenses/${item.key}.htm'
                    },
                    </c:forEach>
                    </c:otherwise>
                    </c:choose>
                ]
            }]
        });
    });
    </c:if>
</script>
<!-- cd-popup -->
<%--<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css">--%>
<!-- Resource style -->
<%--<script src="${pageContext.request.contextPath}/static/js/modernizr.js"></script>--%>
<!-- Modernizr -->
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<!-- Resource jQuery -->
</body>
</html>
