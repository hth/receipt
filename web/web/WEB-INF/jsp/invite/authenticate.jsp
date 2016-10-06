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
            <li><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></li>
            <li><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a></li>
        </ul>
    </nav> <!-- cd-main-nav -->
</header>

<section class="cd-fixed-background" style="background-color: #93a748" data-type="slider-item">
    <div class="cd-content">
        <fieldset class="cd-form floating-labels">
            <h2><fmt:message key="account.register.title" /></h2>
            <p><fmt:message key="account.invite.sub.title" /></p>
        </fieldset>

        <fieldset class="cd-form floating-labels">
            <form:form method="post" modelAttribute="inviteAuthenticateForm" action="authenticate.htm" autocomplete="true">
                <form:hidden path="forgotAuthenticateForm.receiptUserId" />
                <form:hidden path="forgotAuthenticateForm.authenticationKey" />

                <spring:hasBindErrors name="inviteAuthenticateForm">
                    <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                        <ul>
                            <c:if test="${errors.hasFieldErrors('firstName')}">
                                <li><form:errors path="firstName" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('lastName')}">
                                <li><form:errors path="lastName" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('mail')}">
                                <li><form:errors path="mail" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('forgotAuthenticateForm.password')}">
                                <li><form:errors path="forgotAuthenticateForm.password" /></li>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('acceptsAgreement')}">
                                <li><form:errors path="acceptsAgreement" /></li>
                            </c:if>
                        </ul>
                    </div>
                </spring:hasBindErrors>

                <fieldset>
                    <legend>Registration</legend>

                    <div class="icon">
                        <form:label for="firstName" path="firstName" cssClass="cd-label">I like to call myself as:</form:label>
                        <form:input path="firstName" cssClass="user" required="required" cssErrorClass="user error" />
                    </div>

                    <div class="icon">
                        <form:label for="lastName" path="lastName" cssClass="cd-label">And last name is:</form:label>
                        <form:input path="lastName" cssClass="user" required="required" cssErrorClass="user error" />
                    </div>

                    <div class="icon">
                        <form:label for="birthday" path="birthday" cssClass="cd-label">How old are you?</form:label>
                        <form:input path="birthday" cssClass="user" cssErrorClass="user error" />
                    </div>

                    <div class="icon">
                        <form:label for="mail" path="mail" cssClass="cd-label">Valid email as your login:</form:label>
                        <form:input path="mail" cssClass="email" required="required" type="email" cssErrorClass="email error" />
                    </div>

                    <div class="icon">
                        <form:label for="password" path="forgotAuthenticateForm.password" cssClass="cd-label">Password</form:label>
                        <form:password path="forgotAuthenticateForm.password" cssClass="password" required="required" cssErrorClass="password error" />
                    </div>
                </fieldset>

                <ul class="cd-form-list">
                    <li>
                        <input type="checkbox" name="acceptsAgreement" id="acceptsAgreement">
                        <label for="acceptsAgreement">
                            <span class="cd-link"><a href="//receiptofi.com/termsofuse.html" target="_blank">Agree to Receiptofi Terms</a></span>
                        </label>
                    </li>
                </ul>

                <div id="mailErrors"></div>

                <fieldset>
                    <div>
                        <input type="submit" value="Complete Invitation" name="confirm_invitation">
                    </div>
                </fieldset>

                <c:if test="${!registrationTurnedOn}">
                    <div class="error-message">
                        <p>Registration is open, but site is not accepting new users. When site starts accepting new users,
                            you will be notified through email and your account would be turned active.</p>
                    </div>
                </c:if>
            </form:form>
        </fieldset>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#169; 2016 Receiptofi, Inc. <a href="//receiptofi.com/termsofuse">Terms</a> and <a href="//receiptofi.com/privacypolicy">Privacy</a>.<br>
        All other trademarks and logos belong to their respective owners.<br>
    </footer>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script>
    jQuery(document).ready(function ($) {
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
    });
</script>
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