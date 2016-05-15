<%@ include file="include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="title"/></title>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui-1.10.4.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
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
                <img src="${pageContext.request.contextPath}/favicon.ico" alt="Receiptofi logo" height="46px"/>
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
    <c:when test="${!empty receipts}">
    <table style="width: 650px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;"></th>
            <th style="padding:3px;">Title</th>
            <th style="padding:3px;">Receipt Date</th>
            <th style="padding:3px;">Tax</th>
            <th style="padding:3px;">Total</th>
        </tr>
        </tbody>
        <c:forEach var="receipt" items="${receipts}"  varStatus="status">
            <tr>
                <td style="padding:3px;" align="right">
                    ${status.count}
                </td>
                <td style="padding:3px;">
                    <spring:eval expression="receipt.bizName.businessName" />
                </td>
                <td style="padding:3px;">
                    <fmt:formatDate value="${receipt.receiptDate}" type="both"/>
                </td>
                <td style="padding:3px;" align="right">
                    <spring:eval expression="receipt.tax" />
                </td>
                <td style="padding:3px;" align="right">
                    <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm">
                        <spring:eval expression="receipt.total" />
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
    </c:when>
    <c:otherwise>
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
            <p>
                <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width:410px;">
                    No receipt submitted for this day
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
    <p>&#169; 2016 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>