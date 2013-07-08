<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css' />

    <script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../jquery/js/highcharts.js"></script>

</head>
<body>
<c:choose>
    <c:when test="${!empty landingForm.receiptForMonth.receipts}">
        <table>
            <tr>
                <td style="vertical-align: top">
                    <table style="width: 470px" class="etable">
                        <tr>
                            <th style="padding: 3px;"></th>
                            <th style="padding: 3px;">Business</th>
                            <th style="padding: 3px;">Receipt Date</th>
                            <th style="padding: 3px;">Tax</th>
                            <th style="padding: 3px;">Total</th>
                        </tr>
                        <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}"  varStatus="status">
                            <tr>
                                <td style="padding: 3px; text-align: right">
                                    ${status.count}
                                </td>
                                <td style="padding: 3px;">
                                    <spring:eval expression="receipt.name" />
                                </td>
                                <td style="padding: 3px;">
                                    <fmt:formatDate value="${receipt.date}" type="date"/>
                                </td>
                                <td style="padding: 3px; text-align: right">
                                    <spring:eval expression="receipt.tax" />
                                </td>
                                <td style="padding: 3px; text-align: right">
                                    <a href="${pageContext.request.contextPath}/receipt.htm?id=${receipt.id}">
                                        <spring:eval expression='receipt.total' />
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
                <td style="vertical-align: top">
                    <div id="containerRefresh" style="min-width: 525px; height: 275px; margin: 0 auto"></div>
                </td>
            </tr>
        </table>
    </c:when>
    <c:otherwise>
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width:410px;">
                        No receipt(s) submitted or transformed for this month
                    </span>
                </p>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<c:if test="${!empty bizByExpenseTypes}">
<!-- Biz by expense -->
<script>
    $(function () {

        var colors = Highcharts.getOptions().colors,
                categories = [${bizNames}],
                name = 'Receipt Expenses',
                data = [
                    <c:forEach var="item" items="${bizByExpenseTypes}"  varStatus="status">
                    {
                        y: ${item.total},
                        color: colors[${status.count-1}],
                        url: 'http://bing.com/search?q=foo',
                        drilldown: {
                            name: '${item.bizName}',
                            categories: [${item.expenseTypes}],
                            data: [${item.expenseValues}],
                            color: colors[${status.count-1}],
                            url: 'http://bing.com/search?q=foo'
                        }
                    },
                    </c:forEach>
                ];


        // Build the data arrays
        var bizNames = [];
        var expenseTypes = [];
        for (var i = 0; i < data.length; i++) {

            // add browser data
            bizNames.push({
                name: categories[i],
                y: data[i].y,
                color: data[i].color
            });

            // add version data
            for (var j = 0; j < data[i].drilldown.data.length; j++) {
                var brightness = 0.2 - (j / data[i].drilldown.data.length) / 5;
                expenseTypes.push({
                    name: data[i].drilldown.categories[j],
                    y: data[i].drilldown.data[j],
                    color: Highcharts.Color(data[i].color).brighten(brightness).get()
                });
            }
        }

        // Create the chart
        $('#containerRefresh').highcharts({
            chart: {
                type: 'pie'
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Business By Expense, ${landingForm.receiptForMonth.monthYear}'
            },
            yAxis: {
                title: {
                    text: 'Total expense'
                }
            },
            plotOptions: {
                pie: {
                    shadow: false,
                    center: ['50%', '50%']
                }
            },
            tooltip: {
                valueSuffix: '$',
                formatter: function() {
                    return this.point.name + ": " + this.point.y + "$";
                }
            },
            series: [
                {
                    name: 'Total',
                    data: bizNames,
                    size: '60%',
                    dataLabels: {
                        formatter: function () {
                            return this.y > 5 ? this.point.name : null;
                        },
                        color: 'white',
                        distance: -30
                    },
                    point: {
                        events: {
                            click: function(e) {
                                location.href = e.point.series.options.url; //proper path 2)
                                e.preventDefault();
                            }
                        }
                    },
                    allowPointSelect: true,
                    cursor: 'pointer'
                },
                {
                    name: 'Total',
                    data: expenseTypes,
                    size: '80%',
                    innerSize: '60%',
                    dataLabels: {
                        formatter: function () {
                            // display only if larger than 1
                            return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + this.y + '$' : null;
                        }
                    },
                    point: {
                        events: {
                            click: function(e) {
                                location.href = e.point.series.options.url; //proper path 2)
                                e.preventDefault();
                            }
                        }
                    },
                    allowPointSelect: true,
                    cursor: 'pointer'
                }
            ]
        });
    });
</script>
</c:if>

</body>
</html>