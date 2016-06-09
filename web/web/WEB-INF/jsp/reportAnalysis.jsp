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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/highcharts.js"></script>

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
<div class="report_sidebar" id="reportSidebarId">
    <div class="sidebar-top-summary">
        <div class="sidebar-top-summary-upper clearfix">
            <ul>
                <c:set var="yearVar" value="" />
                <c:forEach var="receiptGrouped" items="${reportAnalysisForm.receiptGroupedByMonths}"  varStatus="status">
                <c:if test="${empty yearVar || yearVar ne receiptGrouped.year}">
                    <c:set var="yearVar" value="${receiptGrouped.year}" />
                    <c:if test="${yearVar eq receiptGrouped.year}">
                        <span class="ll-h">${receiptGrouped.year}</span>
                    </c:if>
                </c:if>

                <li>
                    <a href="${pageContext.request.contextPath}/access/landing/report/<spring:eval expression='receiptGrouped.dateTime.toString("MMM, yyyy")' />.htm" class="ll-t" target="_blank">
                        <spring:eval expression='receiptGrouped.dateTime.toString("MMM")' /> &nbsp;&nbsp; <spring:eval expression="receiptGrouped.splitTotal" />
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
    <div id="tabs" class="nav-list" style="width: 750px;">
        <ul class="nav-block">
            <li><a href="#tab1" onclick="reportTabClicked();">REPORT</a></li>
            <li><a href="#tab2" onclick="analysisTabClicked();">ANALYSIS</a></li>
        </ul>
        <div id="tab1" class="report-content">
            <c:choose>
            <c:when test="${!empty reportAnalysisForm.receiptListViews}">
            <c:forEach var="receipts" items="${reportAnalysisForm.receiptListViews}" varStatus="status">
            <div class="rightside-title report-title">
                <h1 class="rightside-title-text left">
                    <fmt:formatDate value="${receipts.date}" pattern="MMMM, yyyy"/>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <span style="color: #007aff;"><spring:eval expression="receipts.splitTotal" /></span>
                </h1>
            </div>
            <div class="rightside-list-holder rightside-list-holder-report">
                <ul>
                    <c:forEach var="receipt" items="${receipts.receiptListViewGroupedList}" varStatus="status">
                    <li>
                        <c:choose>
                            <c:when test="${receipt.splitCount gt 1}">
                                <span class="rightside-li-date-text rightside-li-date-text-short"><fmt:formatDate value="${receipt.date}" pattern="MMM. dd"/></span>
                                <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                    <c:choose>
                                    <c:when test="${receipt.ownReceipt}">
                                        <span class="member" style="background-color: #00529B; width: 25px; height: 25px; margin-top: 3px;">
                                            <span class="member-initials" style="line-height: 25px;">+${receipt.splitCount - 1}</span>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="member" style="background-color: #606060; width: 25px; height: 25px; margin-top: 3px;">
                                            <span class="member-initials" style="line-height: 25px;">+${receipt.splitCount - 1}</span>
                                        </span>
                                    </c:otherwise>
                                    </c:choose>
                                </p>
                            </c:when>
                            <c:otherwise>
                                <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
                            </c:otherwise>
                        </c:choose>
                        <span style="background-color: ${receipt.expenseColor}" title="${receipt.expenseTagName}">&nbsp;&nbsp;&nbsp;</span>
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
                                    class="rightside-li-middle-text" target="_blank">
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
                        <span class="rightside-li-right-text">
                            <spring:eval expression='receipt.splitTotal'/>
                        </span>
                    </li>
                    </c:forEach>
                </ul>
            </div>
            </c:forEach>
            </c:when>
            <c:otherwise>
            <div class="r-info">
                No report available as no receipt submitted or transformed.
            </div>
            </c:otherwise>
            </c:choose>
        </div>
        <div id="tab2" class="report-content">
            <c:choose>
                <c:when test="${!empty reportAnalysisForm.receiptGroupedByMonths}">
                <div id="monthly" style="min-width: 575px; max-width: 740px; height: 425px; padding-left: 5px; margin: 35px 10px 30px 0;"></div>
                    <c:choose>
                        <c:when test="${!empty reportAnalysisForm.thisYearExpenseByTags && reportAnalysisForm.expensesForThisYearPopulated}">
                            <div id="itemsByTags" style="min-width: 575px; height: 420px; margin: 0 auto"></div>
                        </c:when>
                        <c:otherwise>
                            <div class="r-info">
                                Chart 'Items by Tag' not available as no receipt submitted for this year.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                <div class="r-info">
                    No expense analysis available as no receipt submitted.
                </div>
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
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>

<script>
    $("#analysisSidebarId").hide();

    <c:if test="${!empty reportAnalysisForm.receiptGroupedByMonths}">
    <!-- Monthly expense graph -->
    $(function () {
        "use strict";

        $('#monthly').highcharts({
            chart: {
                type: 'column',
                margin: [ 50, 50, 100, 60]
            },
            title: {
                text: 'Monthly Expenses for ${reportAnalysisForm.receiptGroupedByMonths.size()} months of ' +
                '${reportAnalysisForm.receiptGroupedByMonths.get(0).year} - ' +
                '${reportAnalysisForm.receiptGroupedByMonths.get(reportAnalysisForm.receiptGroupedByMonths.size() - 1).year}'
            },
            credits: {
                enabled: false
            },
            xAxis: {
                categories: [
                    <c:forEach var="month" items="${reportAnalysisForm.receiptGroupedByMonths}"  varStatus="status">
                    '${month.monthName}',
                    </c:forEach>
                ],
                labels: {
                    rotation: -45,
                    align: 'right',
                    style: {
                        fontSize: '13px',
                        fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif"
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
                    <c:forEach var="month" items="${reportAnalysisForm.receiptGroupedByMonths}" varStatus="status">
                    {y: ${month.stringTotal}, color: '#7CB5EC'},
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
                        fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif"
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

    <c:if test="${!empty reportAnalysisForm.thisYearExpenseByTags && reportAnalysisForm.expensesForThisYearPopulated}">
    $(function () {
        "use strict";

        Highcharts.setOptions({
            colors: [${reportAnalysisForm.tagColors}]
        });

        $('#itemsByTags').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Items by Tag'
            },
            subtitle: {
                text: 'For ${reportAnalysisForm.itemsForYear}'
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
                    <c:when test="${!empty reportAnalysisForm.thisYearExpenseByTags}">
                    <c:set var="first" value="false"/>
                    <c:forEach var="item" items="${reportAnalysisForm.thisYearExpenseByTags}"  varStatus="status">
                    <c:choose>
                    <c:when test="${first eq false}">
                    {
                        name: '${item.tagName}',
                        y: ${item.percentage},
                        sliced: true,
                        selected: true,
                        url: '${pageContext.request.contextPath}/access/expenses/${item.tagName}.htm'
                    },
                    <c:set var="first" value="true"/>
                    </c:when>
                    <c:otherwise>
                    {
                        name: '${item.tagName}',
                        y: ${item.percentage},
                        sliced: false,
                        selected: false,
                        url: '${pageContext.request.contextPath}/access/expenses/${item.tagName}.htm'
                    },
                    </c:otherwise>
                    </c:choose>
                    </c:forEach>
                    </c:when>
                    <c:otherwise>
                    <c:forEach var="item" items="${reportAnalysisForm.thisYearExpenseByTags}"  varStatus="status">
                    {
                        name: '${item.tagName}',
                        y: ${item.percentage},
                        sliced: false,
                        selected: false,
                        url: '${pageContext.request.contextPath}/access/expenses/${item.tagName}.htm'
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
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</body>
</html>
