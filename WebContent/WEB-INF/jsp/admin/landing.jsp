<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.admin.title" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

	<script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

</head>
<body>
<div class="wrapper">
    <div style='width:229px;'>
        <div style='width:19.25px; height: 19.25px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .05em;'>
            <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="19px" width="19px">
        </div>
        <div style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            &nbsp;&nbsp;&nbsp;
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="../images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <h2>Search users to change profile settings</h2>
    <form:form method="post" modelAttribute="userSearchForm" action="landing.htm">
        <table style="width: 220px" class="etable">
            <tr>
                <th>Search Name: </th>
                <td><form:input path="userName" /></td>
            </tr>
            <c:if test="${users.size() > 0}">
                <table style="width: 250px" class="etable">
                    <tbody>
                        <tr style="background-color:orange;color:white;">
                            <th style="padding:3px;"></th>
                            <th style="padding:3px;">Level</th>
                            <th style="padding:3px;">Name</th>
                        </tr>
                    </tbody>
                    <c:forEach var="user" items="${users}"  varStatus="status">
                    <tr>
                        <td style="padding:3px;" align="center">
                            ${status.count}
                        </td>
                        <td style="padding:3px;" align="center" title="${user.level}">
                            <spring:eval expression="user.level.description" />
                        </td>
                        <td style="padding:3px;" align="left" title="${user.userName}">
                            <a href="${pageContext.request.contextPath}/userprofilepreference/their.htm?id=${user.id}">
                                <spring:eval expression="user.userName" />
                            </a>
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
        <table style="width: 650px" class="etable">
            <tr>
                <td colspan="2">
                    <form:label for="bizName.name" path="bizName.name" cssErrorClass="error">Biz Name: </form:label>
                    <form:input path="bizName.name" id="name" size="52"/>
                    <form:errors path="bizName.name" cssClass="error" />
                </td>
            </tr>
            <tr>
                <td>
                    <form:label for="bizStore.address" path="bizStore.address" cssErrorClass="error">Address: </form:label>
                    <form:input path="bizStore.address" id="name" size="70"/>
                    <form:errors path="bizStore.address" cssClass="error" />
                </td>
                <td>
                    <form:label for="bizStore.phone" path="bizStore.phone" cssErrorClass="error">Phone: </form:label>
                    <form:input path="bizStore.phone" id="name" size="20"/>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="Add Store or New Business" name="add"/>
                </td>
            </tr>
        </table>
    </form:form>

    <c:if test="${bizStore != null}">
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
                <spring:eval expression="bizStore.address" />
            </td>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.lat" />, <spring:eval expression="bizStore.lng" />
            </td>
            <td style="padding:3px;" align="left">
                <spring:eval expression="bizStore.phone" />
            </td>
            <td style="padding:3px;" align="left">
                ${bizStore.created}
            </td>
        </tr>
    </table>
    </c:if>

    <c:if test="${last10BizStore != null}">
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
                <spring:eval expression="bizStore.address" />
            </td>
            <td style="padding:3px;" align="center">
                <spring:eval expression="bizStore.lat" />, <spring:eval expression="bizStore.lng" />
            </td>
            <td style="padding:3px;" align="left">
                <spring:eval expression="bizStore.phone" />
            </td>
            <td style="padding:3px;" align="left">
                 ${bizStore.created}
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