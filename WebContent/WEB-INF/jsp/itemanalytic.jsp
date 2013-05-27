<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="item.analytic.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
</head>
<body>
    <div id="content" style='width:210px;'>
        <div id="leftcolumn" style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div id="rightcolumn" style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

	<br/>

	<table style="width: 550px" class="etable">
		<tbody>
			<tr>
				<th>Name</th>
				<th>Price</th>
				<th>Average Price Paid By Other(s)</th>
			</tr>
		</tbody>
		<tr>
			<td align="left">
	    		${itemAnalyticForm.item.name}
			</td>
			<td align="right">
	    		<spring:eval expression="itemAnalyticForm.item.price" />
			</td>
			<td align="right">
				<spring:eval expression="itemAnalyticForm.averagePrice"/>
			</td>
		</tr>
	</table>

    <h2 class="demoHeaders">Historical Purchases of similar Item(s)</h2>
    <c:if test="${itemAnalyticForm.items.size() > 0}">
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
            <form:form method="post" action="itemanalytic.htm" modelAttribute="itemAnalyticForm">
                <c:forEach items="${itemAnalyticForm.items}" var="item" varStatus="status">
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
                            <spring:eval expression="item.receipt.receiptDate" />
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
                                <form:options items="${itemAnalyticForm.expenseTypes}" itemValue="id" itemLabel="expName" />
                            </form:select>
                        </td>
                    </tr>
                </c:forEach>
            </form:form>
        </table>
    </c:if>

</body>
</html>