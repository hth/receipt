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
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="../images/gear.png" width="18px" height="15px" style="float: right;">
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

    <h2>Search users to change profile settings</h2>
    <form:form method="post" modelAttribute="userSearchForm" action="landing.htm">
        <table style="width: 325px" class="etable">
            <tr>
                <th style="padding: 3px;">Search Name: </th>
                <td style="padding: 3px;">&nbsp;<form:input path="userName" size="27" /></td>
            </tr>
            <c:if test="${!empty users}">
                <table style="width: 325px" class="etable">
                    <tbody>
                        <tr>
                            <th style="padding: 3px;"></th>
                            <th style="padding: 3px;">Level</th>
                            <th style="padding: 3px;">Last, First Name</th>
                            <th style="padding: 3px">Mail Id</th>
                        </tr>
                    </tbody>
                    <c:forEach var="user" items="${users}"  varStatus="status">
                    <tr>
                        <td style="padding: 3px;" align="center">
                            ${status.count}
                        </td>
                        <td style="padding: 3px;" align="center" title="${user.level}">
                            <spring:eval expression="user.level.description" />
                        </td>
                        <td style="padding: 3px;" align="left" title="${user.userName}">
                            <a href="${pageContext.request.contextPath}/userprofilepreference/their.htm?id=${user.id}">
                                <spring:eval expression="user.userName" />
                            </a>
                        </td>
                        <td style="padding: 3px;">
                            ${user.emailId}
                        </td>
                    </tr>
                    </c:forEach>
                </table>
                </c:if>
        </table>
    </form:form>

    <p>&nbsp;</p>

    <h2>Add new Business or Stores to existing business</h2>
    <form:form method="post" modelAttribute="bizForm" action="addBusiness.htm">
        <form:errors path="bizError" cssClass="error" />
        <form:errors path="bizSuccess" cssClass="success" />
        <table style="width: 700px" class="etable">
            <tr>
                <td style="padding: 3px;" colspan="2">
                    <form:label for="bizName.name" path="bizName.name" cssErrorClass="error">Biz Name: </form:label>
                    <form:input path="bizName.name" id="name" size="52"/>
                    <form:errors path="bizName.name" cssClass="error" />
                </td>
            </tr>
            <tr>
                <td style="padding: 3px;">
                    <form:label for="bizStore.address" path="bizStore.address" cssErrorClass="error">Address: &nbsp;</form:label>
                    <form:input path="bizStore.address" id="name" size="65"/>
                    <form:errors path="bizStore.address" cssClass="error" />
                </td>
                <td style="padding: 3px;">
                    <form:label for="bizStore.phone" path="bizStore.phone" cssErrorClass="error">Phone: </form:label>
                    <form:input path="bizStore.phone" id="name" size="18"/>
                </td>
            </tr>
            <tr>
                <td style="padding: 3px;" colspan="2">
                    <input type="submit" value="Add Store or New Business" name="add"/>
                </td>
            </tr>
        </table>
    </form:form>

    <c:if test="${!empty bizStore}">
    <br/>
    Added...
    <table style="width: 650px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;">Store Name</th>
            <th style="padding:3px;">Address</th>
            <th style="padding:3px;">Lat, Lng</th>
            <th style="padding:3px;">Phone</th>
            <th style="padding:3px;">Created</th>
        </tr>
        </tbody>
        <tr>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.bizName.name" />
            </td>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.addressWrappedMore" />
            </td>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.lat" />, <spring:eval expression="bizStore.lng" />
            </td>
            <td style="padding:3px;" align="left">
                <spring:eval expression="bizStore.phone" />
            </td>
            <td style="padding:3px;" align="left">
                <fmt:formatDate value="${bizStore.created}" type="both" />
            </td>
        </tr>
    </table>
    </c:if>

    <c:if test="${!empty last10BizStore}">
    <br/>
    Last 10 records for same business
    <table style="width: 650px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;">Store Name</th>
            <th style="padding:3px;">Address</th>
            <th style="padding:3px;">Lat, Lng</th>
            <th style="padding:3px;">Phone</th>
            <th style="padding:3px;">Created</th>
        </tr>
        </tbody>
        <c:forEach var="bizStore" items="${last10BizStore}"  varStatus="status">
        <tr>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.bizName.name" />
            </td>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.addressWrappedMore" />
            </td>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.lat" />, <spring:eval expression="bizStore.lng" />
            </td>
            <td style="padding:3px;" align="left">
                <spring:eval expression="bizStore.phone" />
            </td>
            <td style="padding:3px;" align="left">
                <fmt:formatDate value="${bizStore.created}" type="both" />
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

<script type="text/javascript">
    function split(val) {
        return val.split(/,\s*/);
    }
    function extractLast(term) {
        return split(term).pop();
    }

    $(document).ready(function() {

        $( "#userName" ).autocomplete({
            source: "${pageContext. request. contextPath}/admin/find_user.htm"
            /* source : ["Alex,Agnes,Alan,Bjok,Bill,John,Jason,Maria,Man"] */
        });

    });
</script>
</body>
</html>