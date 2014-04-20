<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<title><fmt:message key="receipt.admin.title" /></title>

    <link rel="icon" type="image/x-icon" href="../static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../static/images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='../static/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../static/jquery/css/receipt.css'>
    <link rel='stylesheet' type='text/css' href='../static/jquery/js/alpixel/jMenu.jquery.css'  />

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script type="text/javascript" src="../static/jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type="text/javascript" src="../static/jquery/js/alpixel/jMenu.jquery.js"></script>

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

        $(document).ready(function() {
            $("#jMenu").jMenu();
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="../static/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="../static/images/gear.png" width="18px" height="15px" style="float: right;"/>
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">Profile And Preferences</a></li>
                                <li><a href="${pageContext.request.contextPath}/signoff.htm">Sign off</a></li>
                                <li><a href="${pageContext.request.contextPath}/eval/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <ul id="jMenu">
        <li>
            <a class="fNiv">User</a>
            <ul>
                <li class="arrow"></li>
                <li>
                    <a href="landing.htm">Search</a>
                </li>
            </ul>
        </li>

        <li>
            <a class="fNiv">Business &nbsp;&nbsp;&nbsp;</a>
            <ul>
                <li class="arrow"></li>
                <li>
                    <a href="business.htm">Search & Add</a>
                </li>
            </ul>
        </li>
    </ul>

    <p>&nbsp;</p>

    <h2>Search users to change profile settings</h2>
    <form:form method="post" modelAttribute="userSearchForm" action="landing.htm">
        <table style="width: 400px" class="etable">
            <tr>
                <th style="padding: 3px;">Search Name: </th>
                <td style="padding: 3px;">&nbsp;<form:input path="userName" size="25" /></td>
            </tr>
            <tr>
                <td colspan="2">
                    Enter at least 3 characters to find a specific user or else its list all the user below.
                    Would change this later as the number of users increases.
                </td>
            </tr>
            <c:if test="${!empty users}">
                <table style="width: 400px" class="etable">
                    <tbody>
                        <tr>
                            <th style="padding: 3px;"></th>
                            <th style="padding: 3px;">Level</th>
                            <th style="padding: 3px;">First, Last Name</th>
                            <th style="padding: 3px">Mail Id</th>
                        </tr>
                    </tbody>
                    <c:forEach var="user" items="${users}"  varStatus="status">
                    <tr>
                        <td style="padding: 3px;text-align: left; vertical-align: top">
                            ${status.count}
                        </td>
                        <td style="padding: 3px; text-align: left; vertical-align: top" title="${user.level}">
                            <spring:eval expression="user.level.description" />
                        </td>
                        <td style="padding: 3px; text-align: left; vertical-align: top" title="${user.userName}">
                            <a href="${pageContext.request.contextPath}/userprofilepreference/their.htm?id=${user.id}">
                                <spring:eval expression="user.userName" />
                            </a>
                        </td>
                        <td style="padding: 3px; text-align: left; vertical-align: top">
                            ${user.emailId}
                        </td>
                    </tr>
                    </c:forEach>
                </table>
            </c:if>
        </table>
    </form:form>

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved. (<fmt:message key="build.version" />)</p>
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