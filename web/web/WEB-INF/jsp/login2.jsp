<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="login.title"/></title>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/css/stylelogin.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/login.js"></script>
</head>
<body>
<div class="main_wrapper"><!--start_main_wrapper-->
    <!--start_header-->
    <div class="header">
        <div class="header_wrapper">
            <!--/header_left_content-->

            <div class="header_left_content">
                <div id="logo">
                    <h2>OLA_OLA</h2>
                </div>
            </div>


            <div class="header_right_login">
                <!--start_header_right_login-->
                <div class="sing_up"><a href="">Sign Up</a></div>
                <a id="loginButton" class=""><span>Log In</span></a>

                <div id="loginBox" style="display:none;">


                    <form id="loginForm" action="" method="POST">
                        <fieldset id="body">
                            <div class="login_main_arrow">
                                <img src="${pageContext.request.contextPath}/static/img/footer-pattern.png">

                            </div>
                            <fieldset>
                                <label for="email">Email</label>
                                <input name="login" value="" maxlength="80" id="email" placeholder="name@address.com" type="email">
                            </fieldset>
                            <fieldset>

                                <label for="password">Password</label>

                                <input name="password" value="" id="password" required="" placeholder="password" type="password">
                            </fieldset>
                            <input id="login" value="SIGN IN" type="submit">
                            <label for="checkbox">
                                <input name="remember" value="1" id="checkbox" type="checkbox">Remember me</label>
                        </fieldset>

                        <span><a href="">Forgot your password?</a></span>

                    </form>


                </div>
                <!-- /loginBox-->

                <p></p>
            </div>
            <!-- /header_right_login-->

        </div>
        <!--/header_wrapper -->
    </div>
</div>
<div class="clear"></div>

<!-- header ends -->


<div class="containerl">
    <!--  second starts -->
    <div class="mainl">

        <div class="loginl">
            <h2 class="bold">Sign up, it's free</h2>

            <p><strong class="bold">First name</strong></p>
            <input class="text" type="text" placeholder="Name"></input>

            <p><strong class="bold">Last name</strong></p>
            <input class="text" type="text" placeholder="Last name"></input>

            <p><strong class="bold">Email</strong></p>
            <input class="text" maxlength="80" id="email" type="text" placeholder="name@address.com"></input>

            <p><strong class="bold">Password</strong></p>
            <input class="text" name="password" value="" id="password" required="" placeholder="password" type="password">

            <div class="chkmain"><input class="chk" type="checkbox"></input>
                <span class="blurb">I agree to the OLA_OLA terms</span>
            </div>
            <input class="right btnlogin" id="login" type="submit" value="SIGN UP"></input>

            <div class="clear"></div>
        </div>
        <!-- second ends -->
    </div>
</div>
</body>
</html>