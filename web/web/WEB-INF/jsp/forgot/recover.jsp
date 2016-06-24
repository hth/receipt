<%@ include file="../include.jsp"%>
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
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.css">

    <script src="//receiptofi.com/js/vendor/modernizr-2.8.3-respond-1.4.2.min.js"></script> <!-- Modernizr -->

    <title>Receiptofi - ReceiptApp to park your Receipts</title>
</head>
<body>
<!--[if lt IE 8]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->
<header class="cd-header">
    <div id="cd-logo">
        <a href="${pageContext.request.contextPath}/open/login.htm"><div id="cd-logo-img"></div></a>
    </div>

    <h3>Receiptofi</h3>

    <nav class="cd-main-nav">
        <ul>
            <!-- inser more links here -->
            <li><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></li>
            <li><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a></li>
        </ul>
    </nav> <!-- cd-main-nav -->
</header>

<section class="cd-fixed-background" style="background-color: #93a748" data-type="slider-item">
    <div class="cd-content">
        <fieldset class="cd-form floating-labels">
            <h2><fmt:message key="account.recover.title" /></h2>
            <p><fmt:message key="account.recover.sub.title" /></p>
        </fieldset>

        <form:form class="cd-form floating-labels" method="post" modelAttribute="forgotRecoverForm" action="password.htm">
            <form:hidden path="mail" />

            <p style="display:none;visibility:hidden;">
                <form:label for="captcha" path="captcha" cssErrorClass="error">Captcha:</form:label>
                <form:input path="captcha" disabled="true" />
                <form:errors path="captcha" cssClass="error" />
            </p>

            <spring:hasBindErrors name="forgotRecoverForm">
            <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                <ul>
                    <c:if test="${errors.hasFieldErrors('mail')}">
                        <li><form:errors path="mail" /></li>
                    </c:if>
                </ul>
            </div>
            </spring:hasBindErrors>

            <fieldset>
                <div class="icon">
                    <form:label for="mail" path="mail" cssClass="cd-label float">Email</form:label>
                    <form:input path="mail" cssClass="email" readonly="true" disabled="true" required="required"
                            cssStyle="background-color: inherit !important; color: #f2e6cd;" cssErrorClass="email error" />
                </div>
            </fieldset>

            <fieldset>
                <div>
                    <input type="submit" value="Send Recovery Email" name="forgot_password">
                </div>
            </fieldset>
        </form:form>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#169; 2016 Receiptofi, Inc. <a href="//receiptofi.com/termsofuse">Terms</a> and <a href="//receiptofi.com/privacypolicy">Privacy</a>.<br>
        All other trademarks and logos belong to their respective owners.
    </footer>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.0.0/jquery-migrate.min.js"></script>
<script src="//receiptofi.com/js/main.min.js"></script>
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