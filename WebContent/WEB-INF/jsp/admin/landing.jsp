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

    <h2>Search users to change profile settings</h2>
    <form:form method="post" modelAttribute="userSearchForm" action="landing.htm">
        <table style="width: 220px" class="etable">
            <tr>
                <th>Search Name: </th>
                <td><form:input path="userName" /></td>
            </tr>
            <c:if test="${users.size() > 0}">
                <table border="1" style="background-color:#c5c021;border:1px dotted black;width:250px;border-collapse:collapse;">
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

    <br/>
    <br/>

    <h2>Add new Business or Stores to existing businesses</h2>
    <form:form method="post" modelAttribute="bizForm" action="addBusiness.htm">
        <table style="width: 450px" class="etable">
            <tr>
                <td colspan="2">
                    <form:label for="bizName.name" path="bizName.name" cssErrorClass="error">Biz Name: </form:label>
                    <form:input path="bizName.name" id="name" size="32"/>
                </td>
            </tr>
            <tr>
                <td>
                    <form:label for="bizStore.address" path="bizStore.address" cssErrorClass="error">Address: </form:label>
                    <form:input path="bizStore.address" id="name" size="40"/>
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