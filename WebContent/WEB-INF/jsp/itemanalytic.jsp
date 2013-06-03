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

</head>
<body>
<div class="wrapper">
    <div style='width:229px;'>
        <div style='width:19.25px; height: 19.25px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .05em;'>
            <img src="images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="19px" width="19px">
        </div>
        <div style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            &nbsp;&nbsp;&nbsp;
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <p>&nbsp;</p>

    <table style="width: 650px" class="etable">
        <tbody>
            <tr>
                <th style="padding:3px;">Business</th>
                <th style="padding:3px;">Location</th>
                <th style="padding:3px;">Date</th>
                <th style="padding:3px;">Item</th>
                <th style="padding:3px;">Price</th>
                <th style="padding:3px;">${itemAnalyticForm.days} day Average</th>
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
            <td align="left" style="padding:3px;">
                ${itemAnalyticForm.item.name}
            </td>
            <td align="right" style="padding:3px;">
                <spring:eval expression="itemAnalyticForm.item.price" />
            </td>
            <td align="right" style="padding:3px;">
                <fmt:formatNumber value="${itemAnalyticForm.averagePrice}" type="currency" />
            </td>
        </tr>
    </table>

    <h2 class="demoHeaders">Your historical purchases of similar Item(s)</h2>
    <c:if test="${itemAnalyticForm.items.size() > 0}">
        <table style="width: 700px" class="etable">
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
                <c:forEach items="${itemAnalyticForm.items}" var="item" varStatus="status">
                    <tr>
                        <td align="right" style="padding:3px;">
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
                        <td style="padding:3px;">
                            <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                        </td>
                        <td style="padding:3px;">
                            <a href="${pageContext.request.contextPath}/itemanalytic.htm?id=${item.id}">
                            ${item.name}
                            </a>
                        </td>
                        <td style="text-align: right;" style="padding:3px;">
                            <spring:eval expression="item.price" />
                        </td>
                        <td style="text-align: left;" style="padding:3px;">
                            ${item.taxed.description}
                        </td>
                        <td style="text-align: left;" style="padding:3px;">
                            <form:select path="items[${status.index}].expenseType.id">
                                <form:option value="NONE" label="--- Select ---" />
                                <form:options items="${itemAnalyticForm.expenseTypes}" itemValue="id" itemLabel="expName" />
                            </form:select>
                        </td>
                    </tr>
                </c:forEach>
            </form:form>
        </table>
    </c:if>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>Copyright &copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>
</html>