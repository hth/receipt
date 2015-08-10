<%@ include file="include.jsp"%>
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">

    <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300|Merriweather' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="https://receiptofi.com/css/reset.css"> <!-- CSS reset -->
    <link rel="stylesheet" href="https://receiptofi.com/css/style.css"> <!-- Resource style -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin-nn.css"> <!-- Resource style -->
    <script async src="https://receiptofi.com/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script> <!-- Modernizr -->

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
    <div class="cd-content" style="left: 30%; top: 40%;">
        <h2>Manage your receipts</h2>
        <p>Traveling, Budgeting, Expensing. Just snap it and we do the rest. Paperless.</p>

        <form class="cd-form floating-labels" autocomplete="off">
            <fieldset>
                <legend>Sign in to continue</legend>
                <div class="icon">
                    <label class="cd-label" for="cd-email">Email</label>
                    <input class="email" type="email" name="cd-email" id="cd-email" required>
                </div>

                <div class="icon">
                    <label class="cd-label" for="cd-password">Password</label>
                    <input class="password" type="password" name="cd-password" id="cd-password" required>
                </div>
            </fieldset>

            <fieldset>
                <div>
                    <input type="submit" value="Sign In">
                </div>
            </fieldset>
        </form>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#64; 2015 Receiptofi, Inc. <a href="termsofuse">Terms</a> and <a href="privacypolicy">Privacy</a>.<br>
        All other trademarks and logos belong to their respective owners.
    </footer>
</div>

<script async src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
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