<%@ include file="include.jsp"%>
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">

    <link href='//fonts.googleapis.com/css?family=Open+Sans:400,300|Merriweather' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="//receiptofi.com/css/reset.css"> <!-- CSS reset -->
    <link rel="stylesheet" href="//receiptofi.com/css/style.css"> <!-- Resource style -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin-nn.css"> <!-- Resource style -->
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.css" rel="stylesheet">
    <script async src="//receiptofi.com/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script> <!-- Modernizr -->

    <title>Receiptofi - Park Your Receipts Here</title>
</head>
<body>
<!--[if lt IE 8]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->
<header class="cd-header">
    <div id="cd-logo">
        <a href="index.html"><div id="cd-logo-img"></div></a>
    </div>

    <h3>Receiptofi</h3>

    <nav class="cd-main-nav">
        <ul>
            <!-- inser more links here -->
            <li><a href="#0">Sign In</a></li>
            <li><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a></li>
        </ul>
    </nav> <!-- cd-main-nav -->
</header>

<section class="cd-fixed-background" style="background-color: #93a748" data-type="slider-item">
    <div class="cd-content">
        <fieldset class="cd-form floating-labels" id="login-title-fieldset">
            <h2>Manage your receipts</h2>
            <p>Traveling, Budgeting, Expensing. Just snap it and we do the rest. Paperless.</p>
        </fieldset>

        <fieldset class="cd-form floating-labels">
            <legend>Social Sign In</legend>

            <c:if test="${deniedSignup}">
                <div class="error-message">
                    <p>You have been registered, but we are currently not accepting new users.</p>
                    <p>Will notify you through email when we start accepting new users and will automatically grant access to you.</p>
                    <p>User: ${user}</p>
                    <p>Registration: ${pid}</p>
                </div>
            </c:if>

            <!-- FACEBOOK SIGNIN -->
            <form:form name="fb_signin" id="fb_signin" action="${pageContext.request.contextPath}/signin/facebook.htm" method="POST">
                <input type="hidden" name="scope" value="email,public_profile,user_friends,user_about_me,user_birthday" />
                <%--<input type="hidden" name="scope" value="email,public_profile,user_friends,user_activities,user_education_history,user_likes" />--%>
                <div style="float: left; margin: 5px 0 15px 0 !important;">
                    <button type="submit" class="btn btn-success" style="background: #3B5998;">
                        <i class="fa fa-facebook fa-2x social-awesome-icon"></i> <span class="social-text">Facebook</span>
                    </button>
                </div>
            </form:form>

            <!-- GOOGLE SIGNIN -->
            <form:form name="g_signin" id="g_signin" action="${pageContext.request.contextPath}/signin/google.htm" method="POST">
                <input type="hidden" name="scope" value="email https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/tasks https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/latitude.all.best" />
                <input type="hidden" name="request_visible_actions" value="http://schemas.google.com/AddActivity http://schemas.google.com/BuyActivity http://schemas.google.com/CheckInActivity http://schemas.google.com/CommentActivity http://schemas.google.com/CreateActivity http://schemas.google.com/DiscoverActivity http://schemas.google.com/ListenActivity http://schemas.google.com/ReserveActivity http://schemas.google.com/ReviewActivity http://schemas.google.com/WantActivity"/>
                <input type="hidden" name="access_type" value="offline"/>
                <div style="float: right; margin: 5px 0 15px 0 !important;">
                    <button type="submit" class="btn btn-success" style="background: #dd4b39;">
                        <i class="fa fa-google-plus fa-2x social-awesome-icon"></i> <span class="social-text">Google</span>
                    </button>
                </div>
            </form:form>
        </fieldset>

        <form:form class="cd-form floating-labels"  method="post" modelAttribute="userLoginForm" action="/login" autocomplete="on">
            <fieldset>
                <legend>Sign in to continue</legend>
                <c:if test="${!empty param.loginFailure and param.loginFailure eq '--' and !empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
                    <div class="r-error" style="margin-left: 0; width: 100%">
                        Login not successful. Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                    </div>
                    <c:remove var="SPRING_SECURITY_LAST_EXCEPTION" scope="session"/>
                </c:if>

                <div class="icon">
                    <label class="cd-label" for="emailId">Email</label>
                    <input class="email" type="email" name="emailId" id="emailId" required>
                </div>

                <div class="icon">
                    <label class="cd-label" for="password">Password</label>
                    <input class="password" type="password" name="password" id="password" required>
                </div>

                <div class="icon" style="text-align: right">
                    <span class="cd-link"><a href="${pageContext.request.contextPath}/open/forgot/password.htm">Forgot your password?</a></span>
                </div>
            </fieldset>

            <ul class="cd-form-list">
                <li>
                    <input type="checkbox" name="remember-me" id="cd-checkbox-1">
                    <label for="cd-checkbox-1">Remember me on this device</label>
                </li>
            </ul>

            <fieldset>
                <div>
                    <input type="submit" value="SIGN  IN">
                </div>
            </fieldset>
        </form:form>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#64; 2015 Receiptofi, Inc. <a href="termsofuse">Terms</a> and <a href="privacypolicy">Privacy</a>.<br>
        All other trademarks and logos belong to their respective owners.
    </footer>
</div>

<script async src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.1.7/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
<script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/fineupload.js"></script>
<script>
    // For login
    if ($('.floating-labels').length > 0) {
        floatLabels();
    }

    function floatLabels() {
        var inputFields = $('.floating-labels .cd-label').next();
        inputFields.each(function() {
            var singleInput = $(this);
            // check if user is filling one of the form fields
            checkVal(singleInput);
            singleInput.on('change keyup', function() {
                checkVal(singleInput);
            });
        });
    }

    function checkVal(inputField) {
        (inputField.val() == '') ? inputField.prev('.cd-label').removeClass('float') : inputField.prev('.cd-label').addClass('float');
    }
</script>
<script>
    (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
            function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
        e=o.createElement(i);r=o.getElementsByTagName(i)[0];
        e.src='//www.google-analytics.com/analytics.js';
        r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
    ga('create','UA-65975717-1','auto');ga('send','pageview');
</script>
</body>
</html>