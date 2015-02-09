<%@ include file="/WEB-INF/jsp/include.jsp"%>
<div class="rightside-list-holder" id="off_screen">
<c:choose>
    <c:when test="${!empty landingForm.receiptForMonth.receipts}">
    <ul>
        <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
        <li>
            <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
            <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm" class="rightside-li-middle-text">
                <spring:eval expression="receipt.name"/>
            </a>
            <span class="rightside-li-right-text"><spring:eval expression='receipt.total'/></span>
        </li>
        </c:forEach>
    </ul>
    </c:when>
    <c:otherwise>
        <div class="first first-small ajx-content">
            <strong>No receipt data available for this month.</strong>
        </div>
    </c:otherwise>
</c:choose>
</div>

<c:if test="${!empty landingForm.bizByExpenseTypes}">
<!-- Biz by expense -->
<script>
    $(function () {
        "use strict";

        var colors = Highcharts.getOptions().colors;
        var categories = [${landingForm.bizNames}];
        var data = [
            <c:forEach var="item" items="${landingForm.bizByExpenseTypes}" varStatus="status">
            {
                y: ${item.total},
                color: colors[${status.count-1}],
                url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}.htm',
                id: '${item.bizNameForId}',
                drilldown: {
                    name: '${item.bizName}',
                    categories: [${item.expenseTags}],
                    data: [${item.expenseValues}],
                    color: colors[${status.count-1}],
                    url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}.htm',
                    id: '${item.bizNameForId}'
                }
            },
            </c:forEach>
        ];

        // Build the data arrays
        var bizNames = [];
        var expenseTags = [];
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
                expenseTags.push({
                    name: data[i].drilldown.categories[j],
                    y: data[i].drilldown.data[j],
                    color: Highcharts.Color(data[i].color).brighten(brightness).get(),
                    url: data[i].drilldown.url,
                    id: data[i].drilldown.id
                });
            }
        }

        loadMonthlyExpensesByBusiness('${landingForm.receiptForMonth.monthYear}', bizNames, expenseTags);
    });
</script>
</c:if>

</body>
</html>