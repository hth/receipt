<%@ include file="../../../../jsp/include.jsp"%>
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

    <title>Receiptofi | Receipt App to park your Receipts</title>
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

<section class="cd-fixed-background" style="background-color: #93a748; min-height: 1054px;" data-type="slider-item">
    <div class="cd-content">
        <%--<fieldset class="cd-form floating-labels" id="login-title-fieldset">--%>
        <%--<h2><fmt:message key="account.register.title" /></h2>--%>
        <%--<p><fmt:message key="account.register.sub.title" /></p>--%>
        <%--</fieldset>--%>

        <fieldset class="cd-form floating-labels">
            <form:form modelAttribute="register.registerBusiness" autocomplete="true">
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

                <c:if test="${!empty flowRequestContext.messageContext.allMessages}">
                <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                    <ul>
                        <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                            <li>${message.text}</li>
                        </c:forEach>
                    </ul>
                </div>
                </c:if>

                <fieldset>
                    <legend>Registration for Accountant / CPA Business</legend>

                    <div class="icon">
                        <form:label path="name" cssClass="cd-label">Business Name:</form:label>
                        <form:input path="name" cssClass="company" required="required" cssErrorClass="company error" />
                    </div>

                    <div class="icon">
                        <form:label path="address" cssClass="cd-label">Business Address:</form:label>
                        <form:input path="address" size="200" cssClass="company" required="required" cssErrorClass="company error" />
                    </div>

                    <div class="icon">
                        <form:label path="phone" cssClass="cd-label">Business Phone:</form:label>
                        <form:input path="phone" size="20" cssClass="company" required="required" cssErrorClass="company error" />
                    </div>

                    <div>
                        <h4>Business Type:</h4>
                        <form:select path="businessTypes" multiple="true" required="required" style="height: 135px;" cssErrorClass="error">
                            <form:options items="${register.registerBusiness.availableBusinessTypes}" itemValue="name" itemLabel="description" />
                        </form:select>
                    </div>
                </fieldset>

                <fieldset>
                    <div>
                        <input type="submit" value="Next" class="read_btn" name="_eventId_submit"
                                style="background: #2c97de; margin: 17px 10px 0 0;">
                        <input type="submit" value="Cancel" class="read_btn" name="_eventId_cancel"
                                style="background: #FC462A; margin: 17px 10px 0 0;" formnovalidate>
                    </div>
                </fieldset>
            </form:form>
        </fieldset>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#169; 2017 Receiptofi, Inc. <a href="//receiptofi.com/termsofuse">Terms</a> and <a href="//receiptofi.com/privacypolicy">Privacy</a>.<br>
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