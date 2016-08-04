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
    <c:if test="${businessRegistration.businessUser.businessUserRegistrationStatus eq 'C'}">
    <meta http-equiv="Refresh" content="3;url=${pageContext.request.contextPath}/open/login.htm">
    </c:if>
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
            <fieldset>
                <div class="business_reg">
                    <div class="down_form" style="width: 90%">
                        <c:choose>
                            <c:when test="${register.registerBusiness.businessUser.businessUserRegistrationStatus eq 'C'}">
                                <legend>Registration complete</legend>
                                <p>Your details are being verified. Would notify you once the verification is complete.</p>
                                <p>&nbsp;</p>
                                <p>
                                    Redirecting to home page in couple of seconds... If not redirected then
                                    <span class="cd-link"><a href="${pageContext.request.contextPath}/open/login.htm">please click here</a></span>
                                </p>
                            </c:when>
                            <c:otherwise>
                                <legend>Registration not complete</legend>

                                <p>
                                    Failed to complete your registration. Contact us at support@receiptofi.com,
                                    list out the steps and the original link you had clicked.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </fieldset>
        </fieldset>
    </div>
</section>

<div class="footer-container">
    <footer class="wrapper fine-print">
        &#169; 2016 Receiptofi, Inc. <a href="//receiptofi.com/termsofuse">Terms</a> and <a href="//receiptofi.com/privacypolicy">Privacy</a>.<br>
        All other trademarks and logos belong to their respective owners.<br>
    </footer>
</div>
</body>
</html>