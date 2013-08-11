<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head></head>
<body>
<c:choose>
    <c:when test="${!empty landingForm.receiptForMonth.receipts}">
        <table>
            <tr>
                <td style="vertical-align: top">
                    <table style="width: 470px" class="etable" id="tableReceiptForMonth">
                        <tr>
                            <th style="padding: 3px;"></th>
                            <th style="padding: 3px;">Business</th>
                            <th style="padding: 3px;">Receipt Date</th>
                            <th style="padding: 3px;">Tax</th>
                            <th style="padding: 3px;">Total</th>
                        </tr>
                        <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
                        <tr id="${receipt.noSpaceBizName}">
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
                    <div id="container" style="min-width: 530px; height: 425px; margin: 0 auto"></div>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td style="vertical-align: top;">
                    <div>
                        <section class="chunk">
                            <fieldset>
                                <legend class="hd">
                                    <span class="text"><fmt:message key="business.name.abrev" /></span>
                                </legend>
                                <div class="bd">
                                    <c:forEach var="item" items="${bizByExpenseTypes}"  varStatus="status">
                                        <div class="divTable">
                                            <div class="divRow">
                                                <div class="divCell" style="background-color: #eee">
                                                    <fmt:formatNumber value="${status.count}" pattern="00"/>.
                                                    &nbsp; ${item.shortenedBizName4Display}
                                                </div>
                                                <div class="divOfCell300" style="background-color: #eee">
                                                    - &nbsp;${item.bizName}
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </fieldset>
                        </section>
                    </div>
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
                        No receipt(s) submitted or transformed for <b>${landingForm.receiptForMonth.monthYear}</b>
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
        data = [
            <c:forEach var="item" items="${bizByExpenseTypes}"  varStatus="status">
            {
                y: ${item.total},
                color: colors[${status.count-1}],
                url: 'receipt/biz.htm?id=${item.bizName}',
                id: '${item.noSpaceBizName}',
                drilldown: {
                    name: '${item.bizName}',
                    categories: [${item.expenseTypes}],
                    data: [${item.expenseValues}],
                    color: colors[${status.count-1}],
                    url: 'receipt/biz.htm?id=${item.bizName}',
                    id: '${item.noSpaceBizName}'
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
            color: data[i].color,
            url: data[i].url,
            id: data[i].id
        });

        // add version data
        for (var j = 0; j < data[i].drilldown.data.length; j++) {
            var brightness = 0.2 - (j / data[i].drilldown.data.length) / 5;
            expenseTypes.push({
                name: data[i].drilldown.categories[j],
                y: data[i].drilldown.data[j],
                color: Highcharts.Color(data[i].color).brighten(brightness).get(),
                url: data[i].drilldown.url,
                id: data[i].drilldown.id
            });
        }
    }

    // Create the chart
    $('#container').highcharts({
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
                center: ['50%', '50%'],
                slicedOffset: 0
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
                        return this.y > 1 ? this.point.name : null;
                    },
                    color: 'white',
                    distance: -30
                },
                point: {
                    events: {
                        click: function(e) {
                            console.log(this.options.url);
                            location.href = this.options.url;
                        },
                        mouseOver: function(e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).toggleClass('highlight');
                        },
                        mouseOut: function(e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).removeClass('highlight');
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
                            console.log(this.options.url);
                            location.href = this.options.url;
                        },
                        mouseOver: function(e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).toggleClass('highlight');
                        },
                        mouseOut: function(e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).removeClass('highlight');
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