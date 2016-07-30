<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:choose>
    <c:when test="${!empty landingForm.receiptForMonth.receipts}">
    <div class="rightside-list-holder mouseScroll temp_offset" id="receiptListId_refreshReceiptForMonthId">
    <ul>
        <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
        <li>
            <c:choose>
                <c:when test="${!empty receipt.expenseReportInFS or receipt.splitCount gt 1}">
                    <span class="rightside-li-date-text rightside-li-date-text-short"><fmt:formatDate value="${receipt.date}" pattern="MMM. dd"/></span>
                    <c:choose>
                        <c:when test="${!empty receipt.expenseReportInFS and receipt.splitCount gt 1}">
                            <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                <a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receipt.id}.htm' style="margin-top: -2px;">
                                    <img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png'
                                            width='15' height='16' title='Download Expense Report' class='downloadIcon'>
                                </a>
                                <c:choose>
                                <c:when test="${receipt.ownReceipt}">
                                    <span class="member" style="background-color: #00529B; width: 25px; height: 20px; margin-top: 15px;">
                                        <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="member" style="background-color: #606060; width: 25px; height: 20px; margin-top: 15px;">
                                        <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                    </span>
                                </c:otherwise>
                                </c:choose>
                            </p>
                        </c:when>
                        <c:when test="${!empty receipt.expenseReportInFS}">
                            <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                <a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receipt.id}.htm' style="margin-top: -2px;">
                                    <img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png'
                                            width='15' height='16' title='Download Expense Report' class='downloadIcon'>
                                </a>
                            </p>
                        </c:when>
                        <c:when test="${!empty receipt.splitCount}">
                            <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                <c:choose>
                                    <c:when test="${receipt.ownReceipt}">
                                        <span class="member" style="background-color: #00529B; width: 25px; height: 20px; margin-top: 3px;">
                                            <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="member" style="background-color: #606060; width: 25px; height: 20px; margin-top: 3px;">
                                            <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </c:when>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
                </c:otherwise>
            </c:choose>
            <span style="background-color: ${receipt.expenseColor};">&nbsp;&nbsp;</span>
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
            <span class="rightside-li-right-text"><spring:eval expression='receipt.splitTotal'/></span>
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
<script src="//cdnjs.cloudflare.com/ajax/libs/randomcolor/0.3.0/randomColor.min.js"></script>
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