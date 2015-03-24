<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:choose>
    <c:when test="${!empty landingForm.receiptForMonth.receipts}">
    <div class="rightside-list-holder mouseScroll temp_offset" id="receiptListId_refreshReceiptForMonthId">
    <ul>
        <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
        <li>
            <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
            <span style="background-color: ${receipt.expenseColor};">&nbsp;&nbsp;</span>
            <c:choose>
            <c:when test="${receipt.billedStatus eq 'NB'}">
                <a href="/access/userprofilepreference/i.htm#tabs-3"
                        class="rightside-li-middle-text">
                    <spring:eval expression="receipt.name"/>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm"
                        class="rightside-li-middle-text" target="_blank">
                    <spring:eval expression="receipt.name"/>
                </a>
            </c:otherwise>
            </c:choose>
            <span class="rightside-li-right-text"><spring:eval expression='receipt.total'/></span>
        </li>
        </c:forEach>
    </ul>
    </div>
    </c:when>
    <c:otherwise>
    <div class="r-info temp_offset" id="noReceiptId">
        <strong>No receipt data available for this month.</strong>
    </div>
    </c:otherwise>
</c:choose>

<c:if test="${!empty landingForm.bizByExpenseTypes}">
<!-- Biz by expense -->
<script src="//cdnjs.cloudflare.com/ajax/libs/randomcolor/0.1.1/randomColor.min.js"></script>
<script>
    $(function () {
        "use strict";

        var colors = randomColor({hue: 'blue', luminosity: 'bright', count: ${landingForm.bizByExpenseTypes.size()}});
        var categories = [${landingForm.bizNames}];
        var data = [
            <c:forEach var="item" items="${landingForm.bizByExpenseTypes}" varStatus="status">
            {
                y: ${item.total},
                color: colors[${status.count-1}],
                url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}/${landingForm.receiptForMonth.monthYear}.htm',
                id: '${item.bizNameForId}',
                drilldown: {
                    name: '${item.bizName}',
                    categories: [${item.expenseTags}],
                    data: [${item.expenseValues}],
                    color: colors[${status.count-1}],
                    url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}/${landingForm.receiptForMonth.monthYear}.htm',
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