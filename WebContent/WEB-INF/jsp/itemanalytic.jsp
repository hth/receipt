<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="item.analytic.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type='text/javascript' src="jquery/js/highcharts.js"></script>

</head>
<body>
<div class="wrapper">
    <div style='width:243px;'>
        <div style='width:20.25px; height: 20.25px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 0em .0em; padding: .14em;'>
            <img src="images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="19px" width="19px">
        </div>
        <div style='width:65px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em .0em .0em; padding: .5em;'>
            &nbsp;&nbsp;&nbsp;
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 0em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <p>&nbsp;</p>

    <c:choose>
    <c:when test="${!empty itemAnalyticForm.yourHistoricalItems}">
    <table style="width: 900px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;">Business</th>
            <th style="padding:3px;">Location</th>
            <th style="padding:3px;">Date</th>
            <th style="padding:3px;">Item</th>
            <th style="padding:3px;">Price</th>
            <th style="padding:3px;">${itemAnalyticForm.days} days Your Average</th>
            <th style="padding:3px;">Compared with ${itemAnalyticForm.days} days Site Average</th>
        </tr>
        </tbody>
        <tr>
            <td style="padding:3px;">
                <a href="${pageContext.request.contextPath}/receipt.htm?id=${itemAnalyticForm.item.receipt.id}">
                ${itemAnalyticForm.item.receipt.bizName.name}
                </a>
            </td>
            <td style="padding:3px;">
                ${itemAnalyticForm.item.receipt.bizStore.addressWrapped}
            </td>
            <td style="padding:3px;">
                <fmt:formatDate value="${itemAnalyticForm.item.receipt.receiptDate}" type="date"/>
            </td>
            <td style="padding:3px; text-align: left;">
                ${itemAnalyticForm.item.name}
            </td>
            <td style="padding:3px; text-align: right;">
                <spring:eval expression="itemAnalyticForm.item.price" />
            </td>
            <td style="padding:3px; text-align: right;">
                <fmt:formatNumber value="${itemAnalyticForm.yourAveragePrice}" type="currency" />
            </td>
            <td style="padding:3px; text-align: right;">
                <fmt:formatNumber value="${itemAnalyticForm.siteAveragePrice}" type="currency" />
            </td>
        </tr>
    </table>

    <div id="container" style="min-width: 525px; height: 275px; margin: 25px auto;"></div>

    <h2 class="demoHeaders">Your historical purchases of similar Item(s)</h2>
    <c:if test="${!empty itemAnalyticForm.yourHistoricalItems}">
    <table style="width: 900px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;"></th>
            <th style="padding:3px;">Business</th>
            <th style="padding:3px;">Location</th>
            <th style="padding:3px;">Date</th>
            <th style="padding:3px;">Item</th>
            <th style="padding:3px;">Price</th>
            <th style="padding:3px;">Tax</th>
            <th style="padding:3px;">Expense Type</th>
        </tr>
        </tbody>
        <form:form method="post" action="itemanalytic.htm" modelAttribute="itemAnalyticForm">
            <c:forEach items="${itemAnalyticForm.yourHistoricalItems}" var="item" varStatus="status">
                <tr>
                    <td style="padding:3px; text-align: right;">
                        ${status.count}
                    </td>
                    <td style="padding:3px;">
                        <a href="${pageContext.request.contextPath}/receipt.htm?id=${item.receipt.id}">
                        ${item.receipt.bizName.name}
                        </a>
                    </td>
                    <td style="padding:3px;">
                        ${item.receipt.bizStore.addressWrapped}
                    </td>
                    <td style="padding:3px; width: 75px;">
                        <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                    </td>
                    <td style="padding:3px;">
                        <a href="${pageContext.request.contextPath}/itemanalytic.htm?id=${item.id}">
                        ${item.name}
                        </a>
                    </td>
                    <td style="padding:3px; text-align: right; width: 80px;">
                        <spring:eval expression="item.price" />
                    </td>
                    <td style="padding:3px; text-align: right; width: 70px;">
                        ${item.taxed.description}
                    </td>
                    <td style="padding:3px; text-align: left; width: 100px">
                        <form:select path="yourHistoricalItems[${status.index}].expenseType.id">
                            <form:option value="NONE" label="--- Select ---" />
                            <form:options items="${itemAnalyticForm.expenseTypes}" itemValue="id" itemLabel="expName" />
                        </form:select>
                    </td>
                </tr>
            </c:forEach>
        </form:form>
    </table>
    </c:if>
    </c:when>
    <c:otherwise>
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
            <p>
            <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
            <span style="display:block; width:700px;">
                No item found. Please hit back button and submit a valid request.
            </span>
            </p>
        </div>
    </div>
    </c:otherwise>
    </c:choose>

</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
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
                text: 'Site ${itemAnalyticForm.days} days vs. Historical for ${userSession.emailId}'
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

            series: [{
                name: 'Site ${itemAnalyticForm.days} days',
                // Define the data points. All series have a dummy year
                // of 1970/71 in order to be compared on the same x axis. Note
                // that in JavaScript, months start at 0 for January, 1 for February etc.
                data: [
                    <c:forEach items="${itemAnalyticForm.siteAverageItems}" var="item" varStatus="status">
                    [Date.UTC(${item.receipt.year},  ${item.receipt.month - 1}, ${item.receipt.day}), ${item.price} ],
                    </c:forEach>
                ]
            }, {
                name: 'Historical for ${userSession.emailId}',
                data: [
                    <c:forEach items="${itemAnalyticForm.yourAverageItems}" var="item" varStatus="status">
                    [Date.UTC(${item.receipt.year},  ${item.receipt.month - 1}, ${item.receipt.day}), ${item.price} ],
                    </c:forEach>
                ]
            }]
        });
    });
</script>
</c:if>

</body>
</html>