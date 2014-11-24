<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="title"/></title>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui-1.10.4.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/jquery/jquery-ui-1.10.4.custom.min.js"></script>

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
            <div class="divOfCell50" style="height: 46px">
                <img src="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/access/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                <sec:authentication property="principal.username" />
                                <img src="${pageContext.request.contextPath}/static/images/gear.png" width="18px" height="15px" style="float: right;"/>
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/access/userprofilepreference/i.htm">Profile And Preferences</a></li>
                                <li>
                                    <a href="#">
                                        <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                                            <input type="submit" value="Log out" class="button"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        </form>
                                    </a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/access/eval/feedback.htm">Send Feedback</a></li>
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
                            <a href="${pageContext.request.contextPath}/access/receipt/${item.receipt.id}.htm">
                                ${item.receipt.bizName.businessName}
                            </a>
                        </td>
                        <td style="padding: 3px;">
                            <fmt:formatDate value="${item.receipt.receiptDate}" type="date"/>
                        </td>
                        <td style="padding: 3px;">
                            <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">
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
                            <form:select path="items[${status.index}].expenseTag.id">
                                <form:option value="NONE" label="--- Select ---" />
                                <form:options items="${expenseForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                            </form:select>
                        </td>
                    </tr>
                </c:forEach>
            </form:form>
        </table>
        </c:when>
        <c:otherwise>
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width:410px;">
                        No data available for selected expense type: <b>${expenseForm.name}</b>
                    </span>
                </p>
            </div>
        </div>
        </c:otherwise>
    </c:choose>

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>