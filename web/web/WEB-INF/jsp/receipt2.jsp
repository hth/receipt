<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>Detail</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css"/>
</head>
<body>

<span class="timestamp"></span>
<div class="clear"></div>
<div class=" is-visible" role="alert">
    <div class="cd-popup-container" style="box-shadow:none;overflow: hidden;">

        <form:form method="post" action="../receipt.htm" modelAttribute="receiptForm">
        <div style="float:left;width:55%;margin-right: 3%;">
            <h1 class="h1"><fmt:formatDate pattern="MMMM dd, yyyy" value="${receiptForm.receipt.receiptDate}"/>
                <span style="color: #6E6E6E;font-weight: normal;"><fmt:formatDate value="${receiptForm.receipt.receiptDate}" type="time"/></span>
            </h1>
            <hr style="width: 100%;">
            <div class="mar10px">
                <h1 class="font3em"><spring:eval expression="receiptForm.receipt.bizName.businessName" /></h1>
                <p class="address">
                    <spring:eval expression="receiptForm.receipt.bizStore.addressWrapped"/>
                    <br />
                    <spring:eval expression="receiptForm.receipt.bizStore.phoneFormatted"/>
                </p>
            </div>
            <div class="detailHead">
                <h1 class="font2em" style="margin-left: 5px;">Map-93 <span class="colorblue right"><spring:eval expression="receiptForm.receipt.total" /></span></h1>
            </div>
            <div>
                <table width="96%" style="margin-left: 4px; margin-right: 4px">
                    <tr>
                        <th width="60%"></th>
                        <th width="20%"></th>
                        <th width="20%"></th>
                    </tr>
                    <c:forEach items="${receiptForm.items}" var="item" varStatus="status">
                    <form:hidden path="items[${status.index}].id"/>
                    <tr>
                        <td class="rightside-li-middle-text">
                            <c:choose>
                                <c:when test="${item.quantity eq 1}">
                                    ${status.count}. <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">${item.name}</a>
                                </c:when>
                                <c:otherwise>
                                    ${status.count}. <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">${item.name}</a>
                                    <div style="margin-top: 5px">
                                        ${item.quantity} @ <fmt:formatNumber value="${item.price}" type="currency" pattern="###,###.####" /> each
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <form:select path="items[${status.index}].expenseTag.id" id="itemId">
                                <form:option value="NONE" label="--- Select ---" />
                                <form:options items="${receiptForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                            </form:select>
                        </td>
                        <td class="rightside-li-right-text">
                            <spring:eval expression="item.totalPriceWithoutTax" />
                            <spring:eval expression="item.taxed == T(com.receiptofi.domain.types.TaxEnum).TAXED" var="isValid" />
                            <c:choose>
                                <c:when test="${!isValid}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <spring:eval expression="item.totalTax"/> (T)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    </c:forEach>
                </table>
                <!-- second list starts-->
                <ul>
                    <li>
                        <span class="rightside-li-date-text">Sub Total</span>

                        <span class="rightside-li-right-text">$81.65</span>
                    </li>
                    <li>
                        <span class="rightside-li-date-text">Tax</span>
                        <span class="rightside-li-right-text"><spring:eval expression="receiptForm.receipt.tax" /></span>
                    </li>
                    <li style="border-bottom: 1px solid #919191;">
                        <span class="rightside-li-date-text">Grand Total</span>
                        <span class="rightside-li-right-text"><spring:eval expression="receiptForm.receipt.total" /></span>
                    </li>
                </ul>
                <!-- second list ends -->
                <h1 class="h1 address" style="padding-bottom:2%;">My notes</h1>
                <textarea style="width: 561px;height: 145px; padding:1%;" placeholder="Write notes here..."></textarea>
                <input type="button" value="DELETE" style="background:#FC462A;"></input>
                <input type="button" value="SAVE" style="background:#0079FF"></input>
            </div>
        </div>
        <div style="width:38%;float: left;padding-top: 4%;">
            <img style="width: 390px;height: 590px;padding-left: 8%;" src="static/img/details.JPG"/>
        </div>
        </form:form>
    </div>
    <!-- cd-popup-container -->
</div>

</body>
</html>