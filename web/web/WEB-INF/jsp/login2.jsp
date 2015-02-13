<!DOCTYPE html>
<html>
<head>
    <title>OLA_OLA l sign up</title>
    <link type="text/css" rel="stylesheet" href="static/css/stylelogin.css"></link>
    <script src="static/js/jquery.min.js" type="text/javascript"></script>
    <script src="static/js/login.js" type="text/javascript"></script>
</head>
<body>
<div class="main_wrapper">
    <div class="header">
        <div class="header_wrapper">
            <div class="header_left_content">
                <div id="logo">
                    <h1 style="font-weight: 500;margin-top: 0.55em;">OLA OLA</h1>
                </div>
            </div>
            <div class="header_right_login">
                <div class="sing_up"><a href="">Sign Up</a></div>
                <a id="loginButton" class=""><span>Log In</span></a>

                <div id="loginBox" style="display:none;">
                    <form id="loginForm" action="" method="POST">
                        <fieldset id="body">
                            <div class="login_main_arrow">
                                <img src="static/img/rec1.png">
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
                            <label for="checkbox"><input name="remember" value="1" id="checkbox" type="checkbox">Remember me</label>
                        </fieldset>
                        <span><a href="">Forgot your password?</a></span>
                    </form>
                </div>
                <p></p>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
<div class="signup_containerl">
    <div class="signup_mainl">
        <div class="loginl">
            <h2 class="bold">Sign up, it's free</h2>
            <label class="sign_uplabel"><strong class="bold">First name</strong></label>
            <input class="text" type="text" placeholder="Name"></input>
            <label class="sign_uplabel"><strong class="bold">Last name</strong></label>
            <input class="text" type="text" placeholder="Last name"></input>
            <label class="sign_uplabel"><strong class="bold">Email</strong></label>
            <input class="text" maxlength="80" id="email" type="text" placeholder="name@address.com"></input>
            <label class="sign_uplabel"><strong class="bold">Password</strong></label>
            <input class="text" name="password" value="" id="password" required="" placeholder="password" type="password">

            <div class="chkmain">
                <input class="chk" type="checkbox"></input>
                <span class="blurb">I agree to the OLA_OLA terms</span>
            </div>
            <input class="right btnsignup" id="login" type="submit" value="SIGN UP"></input>
            <div class="clear"></div>
        </div>
    </div>
</div>
</body>
</html>