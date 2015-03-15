<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="login.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/css/stylelogin.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/login.js"></script>
</head>
<body>
<div class="main_wrapper">
    <div class="header">
        <div class="header_wrapper">
            <div class="header_left_content">
                <div id="logo">
                    <h1 style="font-weight: 500;margin-top: 0.55em;">Receiptofi</h1>
                </div>
            </div>
            <div class="header_right_login">
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/login.htm">Login</a></div>
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a></div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
<div class="signup_containerl">
    <div class="signup_mainl">
        <div class="loginl">
            <h1 class="h1 spacing" style="margin-bottom: 15px;">Sign up</h1>
            <h4 class="h4" style="margin-bottom: 40px;">Start analyzing your receipts in 30 seconds or less</h4>

            <form:form method="post" modelAttribute="userRegistrationForm" action="registration.htm" autocomplete="true">
                <spring:hasBindErrors name="userRegistrationForm">
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
                    <c:if test="${errors.hasFieldErrors('password')}">
                        <li><form:errors path="password" /></li>
                    </c:if>
                    <c:if test="${errors.hasFieldErrors('acceptsAgreement')}">
                        <li><form:errors path="acceptsAgreement" /></li>
                    </c:if>
                    </ul>
                </div>
                </spring:hasBindErrors>

                <form:label for="firstName" path="firstName" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">I like to call myself as:</form:label>
                <form:input path="firstName" maxlength="80" placeholder="First name" cssClass="text" />

                <form:label for="lastName" path="lastName" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">And last name is:</form:label>
                <form:input path="lastName" maxlength="80" placeholder="Last name" cssClass="text" />

                <form:label for="birthday" path="birthday" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">How old are you?</form:label>
                <form:input path="birthday" maxlength="80" placeholder="Age" cssClass="text" />

                <form:label for="mail" path="mail" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">Valid email address as your login:</form:label>
                <form:input path="mail" maxlength="80" placeholder="Email address" cssClass="text" />

                <form:label for="password" path="password" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">Password:</form:label>
                <form:password path="password" maxlength="80" placeholder="Password" cssClass="text" />

                <div class="checkbox">
                    <form:checkbox path="acceptsAgreement" id="acceptsAgreement" cssClass="chk"/>
                    <form:label for="acceptsAgreement" path="acceptsAgreement" cssClass="checkbox_txt"
                            cssErrorClass="checkbox_txt lb_error">I fully agree to the Receiptofi terms</form:label>
                </div>
                <div class="clear"></div>

                <c:choose>
                    <c:when test="${userRegistrationForm.accountExists}">
                        <input id="recover_btn_id" type="submit" value="RECOVER PASSWORD" name="recover" class="submit_btn" style="width: 200px; float: left;" />
                    </c:when>
                    <c:otherwise>
                        <input id="recover_btn_id" type="submit" value="RECOVER PASSWORD" name="recover" class="submit_btn" style="display: none; width: 200px; float: left;" />
                    </c:otherwise>
                </c:choose>
                <input id="submit_btn_id" type="submit" value="SIGN ME UP" name="signup" class="submit_btn" />

                <c:if test="${!registrationTurnedOn}">
                <div class="registrationWhenTurnedOff">
                Registration is open, but site is not accepting new users. When site starts accepting new users,
                    you will be notified through email and your account would be turned active.
                </div>
                </c:if>
            </form:form>
            <div class="clear"></div>
        </div>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
<script type="text/javascript">

    $(document).ready(function() {
        // check name availability on focus lost
        $('#mail').blur(function() {
            if ($('#mail').val()) {
                checkAvailability();
            } else {
                $("#recover_btn_id").css({'display': 'none'});
            }
        });
    });

    function checkAvailability() {
        $.ajax({
            type: "POST",
            url: '${pageContext. request. contextPath}/open/registration/availability.htm',
            beforeSend: function(xhr) {
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            },
            data: JSON.stringify({
                mail: $('#mail').val()
            }),
            contentType: 'application/json;charset=UTF-8',
            mimeType: 'application/json',
            dataType:'json',
            success: function (data) {
                console.log('response=', data);
                fieldValidated(data);
            }
        });
    }

    function fieldValidated(result) {
        if (result.valid === true) {
            $("#mailErrors")
                    .html("Verification email will be sent to above email address")
                    .css({'display': 'inline', 'background-color' : '#34B120', 'color' : '#FFF', 'font-family': 'Helvetica Neue, Helvetica, Arial, sans-serif'});
            $("#mail.errors")
                    .css({'display': 'none'});
        } else {
            $("#mailErrors")
                    .html(result.message)
                    .css({'display': 'inline'})
                    .css("background-color","")
                    .css("color","");
            //Add the button for recovery and hide button for SignUp
            $("#recover_btn_id")
                    .css({'display': 'inline'});
        }
    }
</script>
</body>
</html>