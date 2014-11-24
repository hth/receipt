<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="feedback.title"/></title>

    <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/access/landing.htm">

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui-1.10.4.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>
    <link rel='stylesheet' type='text/css' href="${pageContext.request.contextPath}/static/jquery/fineuploader/fineuploader-3.6.3.css"/>

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
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <p>&nbsp;</p>

    <div>
        <div class='ui-widget'>
            <div class='ui-state-highlight ui-corner-all alert-success' style='margin-top: 0px; padding: 0 .7em;'>
                <p>
                   <span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;' title='Shows number of pending receipt(s) to be processed'></span>
                   <span style='display:block; width:310px;'>
                       <p>Thank you for providing valuable feedback.</p>
                   </span>
                </p>
            </div>
        </div>
    </div>

    <p>
        <br/><br/><br/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        Redirecting to home page in couple of seconds... If not redirected then please <a href="${pageContext.request.contextPath}/access/landing.htm">click here</a>
    </p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>