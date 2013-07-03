<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="receipt.admin.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

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
                <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="40px">
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
                                <li><a href="${pageContext.request.contextPath}/signoff.htm">Sign off</a></li>
                                <li><a href="${pageContext.request.contextPath}/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <p>&nbsp;</p>

    <h2 class="demoHeaders">Pending Receipt(s)</h2>
    <c:if test="${!empty pending}">
        <table style="width: 400px" class="etable">
            <tbody>
            <tr>
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
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

    <h2 class="demoHeaders">Queued Receipt(s)</h2>
    <c:if test="${!empty queue}">
        <table style="width: 400px" class="etable">
            <tbody>
            <tr>
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
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

    <h2 class="demoHeaders">Re-Check Pending Receipt(s)</h2>
    <c:if test="${!empty recheckPending}">
        <table style="width: 400px" class="etable">
            <tbody>
            <tr>
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
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

    <h2 class="demoHeaders">Re-Check Receipt(s)</h2>
    <c:if test="${!empty recheck}">
        <table style="width: 400px" class="etable">
            <tbody>
            <tr>
                <th style="padding:3px;"></th>
                <th style="padding:3px;">User Type</th>
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
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>
</html>