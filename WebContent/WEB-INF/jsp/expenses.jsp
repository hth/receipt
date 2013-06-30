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
            <div class="divOfCell50">
                <img src="images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="40px">
            </div>
            <div class="divOfCell75">
                <h3><a href="${pageContext.request.contextPath}/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown">
                        <a class="account" style="color: #065c14">${sessionScope['userSession'].emailId}</a>

                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">Profile And Preferences</a></li>
                                <li><a href="${pageContext.request.contextPath}/logout.htm">Logout</a></li>
                                <li><a href="${pageContext.request.contextPath}/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <p>&nbsp;</p>

    <c:choose>
        <c:when test="${!empty expenseForm.items}">
        <table style="width: 750px" class="etable">
            <tbody>
            <tr>
                <th style="padding: 3px;"></th>
                <th style="padding: 3px;">Business</th>
                <th style="padding: 3px;">Transaction Date</th>
                <th style="padding: 3px;">Name</th>
                <th style="padding: 3px;">Price</th>
                <th style="padding: 3px;">Tax</th>
                <th style="padding: 3px;">Expense Type</th>
            </tr>
            </tbody>
            <form:form method="post" action="expenses.htm" modelAttribute="expenseForm">
                <c:forEach items="${expenseForm.items}" var="item" varStatus="status">
                    <tr>
                        <td style="padding:3px;" align="right">
                            ${status.count}
                        </td>
                        <td style="padding: 3px;">
                            <a href="${pageContext.request.contextPath}/receipt.htm?id=${item.receipt.id}">
                                ${item.receipt.bizName.name}
                            </a>
                        </td>
                        <td style="padding: 3px;">
                            <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                        </td>
                        <td style="padding: 3px;">
                            <a href="${pageContext.request.contextPath}/itemanalytic.htm?id=${item.id}">
                                ${item.name}
                            </a>
                        </td>
                        <td style="padding: 3px; text-align: right;">
                            <spring:eval expression="item.price" />
                        </td>
                        <td style="padding: 3px; text-align: left;">
                            ${item.taxed.description}
                        </td>
                        <td style="padding: 3px; text-align: left;">
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
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>