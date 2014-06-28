<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<title><fmt:message key="item.analytic.title" /></title>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type='text/javascript' src="${pageContext.request.contextPath}/static/jquery/js/highcharts.js"></script>

    <!-- For drop down menu -->
    <script>
        $(document).ready(function () {

            $(".account").click(function () {
                var X = $(this).attr('id');
                if (X == 1) {
                    $(".submenu").hide();
                    $(this).attr('id', '0');
                }
                else {
                    $(".submenu").show();
                    $(this).attr('id', '1');
                }

            });

            //Mouse click on sub menu
            $(".submenu").mouseup(function () {
                return false
            });

            //Mouse click on my account link
            $(".account").mouseup(function () {
                return false
            });

            //Document Click
            $(document).mouseup(function () {
                $(".submenu").hide();
                $(".account").attr('id', '');
            });
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/access/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                <sec:authentication property="principal.username" />
                                <img src="${pageContext.request.contextPath}/static/images/gear.png" width="18px" height="15px" style="float: right;"/>
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/access/userprofilepreference/i.htm">Profile And Preferences</a></li>
                                <li>
                                    <a href="#">
                                        <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                                            <input type="submit" value="Log out" class="button"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        </form>
                                    </a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/access/eval/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <p>&nbsp;</p>

    <c:if test="${!empty itemAnalyticForm.message}">
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
            <p>
                <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width:410px;">
                    ${itemAnalyticForm.message}
                </span>
            </p>
        </div>
    </div>
    </c:if>

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
            <th style="padding:3px;">Your ${itemAnalyticForm.days} days <br> Average</th>
            <th style="padding:3px;">Compared with Site's <br> ${itemAnalyticForm.days} days Average</th>
        </tr>
        </tbody>
        <tr>
            <td style="padding:3px;">
                <a href="${pageContext.request.contextPath}/access/receipt/${itemAnalyticForm.item.receipt.id}.htm">
                ${itemAnalyticForm.item.receipt.bizName.businessName}
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
                        <a href="${pageContext.request.contextPath}/access/receipt/${item.receipt.id}.htm">
                        ${item.receipt.bizName.businessName}
                        </a>
                    </td>
                    <td style="padding:3px;">
                        ${item.receipt.bizStore.addressWrapped}
                    </td>
                    <td style="padding:3px; width: 75px;">
                        <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                    </td>
                    <td style="padding:3px;">
                        <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">
                        ${item.name}
                        </a>
                    </td>
                    <td style="padding:3px; text-align: right; width: 80px;">
                        <spring:eval expression="item.price" />
                    </td>
                    <td style="padding:3px; text-align: right; width: 70px;">
                        <spring:eval expression="item.taxed == T(com.receiptofi.domain.types.TaxEnum).TAXED" var="isValid" />
                        <c:choose>
                            <c:when test="${!isValid}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <spring:eval expression="item.tax"/> (T)
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px; text-align: left; width: 100px">
                        <form:select path="yourHistoricalItems[${status.index}].expenseTag.id">
                            <form:option value="NONE" label="--- Select ---" />
                            <form:options items="${itemAnalyticForm.expenseTags}" itemValue="id" itemLabel="tagName" />
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

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
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
                text: 'Site ${itemAnalyticForm.days} days vs. Historical ${itemAnalyticForm.days} days for ${userSession.emailId}'
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