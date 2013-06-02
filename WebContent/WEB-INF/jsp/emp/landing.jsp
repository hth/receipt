<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="receipt.admin.title" /></title>
    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

    <link rel='stylesheet' type='text/css' href='../jquery/fullcalendar/fullcalendar.css' />
    <link rel='stylesheet' type='text/css' href='../jquery/fullcalendar/fullcalendar.print.css' media='print' />
    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type='text/javascript' src="../jquery/fullcalendar/fullcalendar.min.js"></script>

</head>
<body>
<div class="wrapper">
    <div style='width:231px;'>
        <div style='width:14px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <img src="../images/circle-leaf.jpg" alt="receipt-o-fi logo" height="12px" width="12px">&nbsp;&nbsp;&nbsp;
        </div>
        <div style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="../images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <h2>Pending Receipt(s)</h2>
    <c:if test="${pending.size() > 0}">
        <table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
            <tbody>
            <tr style="background-color:orange;color:white;">
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
                <th style="padding:3px;">Description</th>
                <th style="padding:3px;">Created</th>
                <th style="padding:3px;">Pending Since</th>
                <th style="padding:3px;">Edit</th>
            </tr>
            </tbody>
            <c:forEach var="receipt" items="${pending}"  varStatus="status">
                <tr>
                    <td style="padding:3px;" align="right">
                        ${status.count}
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.level" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.description" />
                    </td>
                    <td style="padding:3px;">
                        <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.since" />
                    </td>
                    <td style="padding:3px;" align="right" title="${receipt.idReceiptOCR}">
                        <a href="${pageContext.request.contextPath}/emp/update.htm?id=${receipt.idReceiptOCR}">
                            Open
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <h2>Queued Receipt(s)</h2>
    <c:if test="${queue.size() > 0}">
        <table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
            <tbody>
            <tr style="background-color:orange;color:white;">
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
                <th style="padding:3px;">Description</th>
                <th style="padding:3px;">Created</th>
                <th style="padding:3px;">Pending Since</th>
                <th style="padding:3px;">Edit</th>
            </tr>
            </tbody>
            <c:forEach var="receipt" items="${queue}"  varStatus="status">
                <tr>
                    <td style="padding:3px;">
                        ${status.count}
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.level" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.description" />
                    </td>
                    <td style="padding:3px;">
                        <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.since" />
                    </td>
                    <td style="padding:3px;" align="right" title="${receipt.idReceiptOCR}">
                        <a href="${pageContext.request.contextPath}/emp/update.htm?id=${receipt.idReceiptOCR}">
                            Open
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <h2>Re-Check Pending Receipt(s)</h2>
    <c:if test="${recheckPending.size() > 0}">
        <table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
            <tbody>
            <tr style="background-color:orange;color:white;">
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
                <th style="padding:3px;">Description</th>
                <th style="padding:3px;">Created</th>
                <th style="padding:3px;">Pending Since</th>
                <th style="padding:3px;">Edit</th>
            </tr>
            </tbody>
            <c:forEach var="receipt" items="${recheckPending}"  varStatus="status">
                <tr>
                    <td style="padding:3px;">
                        ${status.count}
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.level" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.description" />
                    </td>
                    <td style="padding:3px;">
                        <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.since" />
                    </td>
                    <td style="padding:3px;" align="right" title="${receipt.idReceiptOCR}">
                        <a href="${pageContext.request.contextPath}/emp/recheck.htm?id=${receipt.idReceiptOCR}">
                            Open
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <h2>Re-Check Receipt(s)</h2>
    <c:if test="${recheck.size() > 0}">
        <table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
            <tbody>
            <tr style="background-color:orange;color:white;">
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
                <th style="padding:3px;">Description</th>
                <th style="padding:3px;">Created</th>
                <th style="padding:3px;">Pending Since</th>
                <th style="padding:3px;">Edit</th>
            </tr>
            </tbody>
            <c:forEach var="receipt" items="${recheck}"  varStatus="status">
                <tr>
                    <td style="padding:3px;">
                        ${status.count}
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.level" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.description" />
                    </td>
                    <td style="padding:3px;">
                        <fmt:formatDate value="${receipt.created}" type="both" dateStyle="long" timeStyle="long" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.since" />
                    </td>
                    <td style="padding:3px;" align="right" title="${receipt.idReceiptOCR}">
                        <a href="${pageContext.request.contextPath}/emp/recheck.htm?id=${receipt.idReceiptOCR}">
                            Open
                        </a>
                    </td>
                </tr>
            </c:forEach>
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