<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.title" /></title>

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
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="images/gear.png" width="18px" height="15px" style="float: right;">
                            </a>
                        </div>
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

    <c:choose>
        <c:when test="${!empty pendingReceiptForm.pending}">
            <h2 class="demoHeaders">Pending receipt<c:if test="${pendingReceiptForm.pending.size() gt 1}">s</c:if></h2>

            <table>
                <tr>
                    <td style="vertical-align: top;">
                        <table style="width: 400px" class="etable">
                            <tr>
                                <th style="padding: 3px; text-align: left;">&nbsp;</th>
                                <th style="padding: 3px; text-align: left;">&nbsp;File Name</th>
                                <th style="padding: 3px; text-align: left;">&nbsp;Upload Date</th>
                            </tr>
                            <c:forEach items="${pendingReceiptForm.pending}" var="receipt" varStatus="status">
                            <tr>
                                <td style="padding: 3px; text-align: right;">
                                    ${status.count}
                                </td>
                                <td style="padding: 3px;">
                                    <a href="${pageContext.request.contextPath}/emp/update.htm?id=${receipt.receiptEntityOCR.id}">
                                    ${receipt.fileName}
                                    </a>
                                </td>
                                <td style="padding: 3px; text-align: left;">
                                    <fmt:formatDate value="${receipt.receiptEntityOCR.created}" type="both"/>
                                </td>
                            </tr>
                            </c:forEach>
                        </table>
                    </td>
                    <td>&nbsp;&nbsp;&nbsp;</td>
                    <td>
                     Empty. This should be removed
                    </td>
                </tr>
            </table>
        </c:when>
        <c:otherwise>
            <h2 class="demoHeaders">No pending receipt</h2>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${!empty pendingReceiptForm.rejected}">
            <h2 class="demoHeaders">Rejected receipt<c:if test="${pendingReceiptForm.rejected.size() gt 1}">s</c:if></h2>

            <table>
                <tr>
                    <td style="vertical-align: top;">
                        <table style="width: 400px" class="etable">
                            <tr>
                                <th style="padding: 3px; text-align: left;">&nbsp;</th>
                                <th style="padding: 3px; text-align: left;">&nbsp;File Name</th>
                                <th style="padding: 3px; text-align: left;">&nbsp;Upload Date</th>
                            </tr>
                            <c:forEach items="${pendingReceiptForm.rejected}" var="receipt" varStatus="status">
                                <tr>
                                    <td style="padding: 3px; text-align: right;">
                                            ${status.count}
                                    </td>
                                    <td style="padding: 3px;">
                                        <a href="${pageContext.request.contextPath}/emp/update.htm?id=${receipt.receiptEntityOCR.id}">
                                        ${receipt.fileName}
                                        </a>
                                    </td>
                                    <td style="padding: 3px; text-align: left;">
                                        <fmt:formatDate value="${receipt.receiptEntityOCR.created}" type="both"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </td>
                    <td>&nbsp;&nbsp;&nbsp;</td>
                    <td>
                        Empty. This should be removed
                    </td>
                </tr>
            </table>
        </c:when>
        <c:otherwise>
            <h2 class="demoHeaders">No rejected receipt to delete</h2>
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
</html>