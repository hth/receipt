<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="title" /></title>

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
        <c:when test="${expenseForm.items.size() > 0}">
            <table style="width: 650px" class="etable">
                <tbody>
                <tr>
                    <th></th>
                    <th>Business</th>
                    <th>Transaction Date</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Tax</th>
                    <th>Expense Type</th>
                </tr>
                </tbody>
                <form:form method="post" action="expenses.htm" modelAttribute="expenseForm">
                    <c:forEach items="${expenseForm.items}" var="item" varStatus="status">
                        <tr>
                            <td style="padding:3px;" align="right">
                                ${status.count}
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/receipt.htm?id=${item.receipt.id}">
                                    ${item.receipt.bizName.name}
                                </a>
                            </td>
                            <td>
                                <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/itemanalytic.htm?id=${item.id}">
                                    ${item.name}
                                </a>
                            </td>
                            <td style="text-align: right;">
                                <spring:eval expression="item.price" />
                            </td>
                            <td style="text-align: left;">
                                ${item.taxed.description}
                            </td>
                            <td style="text-align: left;">
                                <form:select path="items[${status.index}].expenseType.id">
                                    <form:option value="NONE" label="--- Select ---" />
                                    <form:options items="${expenseForm.expenseTypes}" itemValue="id" itemLabel="expName" />
                                </form:select>
                            </td>
                        </tr>
                    </c:forEach>
                </form:form>
            </table>
        </c:when>
        <c:otherwise>
            No data available for selected expense type: ${expenseForm.name}
        </c:otherwise>
    </c:choose>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>Copyright &copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>