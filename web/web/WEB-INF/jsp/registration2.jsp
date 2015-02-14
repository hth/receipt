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
                <div class="sing_up"><a href="${pageContext.request.contextPath}/login.htm">Login</a></div>
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
                <form:label for="firstName" path="firstName" cssClass="signup_label signup_label_text">I like to call myself as:</form:label>
                <form:input path="firstName" maxlength="80" placeholder="First name" cssClass="text" />
                <spring:hasBindErrors name="userRegistrationForm">
                    <form:label path="firstName" cssClass="signup_label first"><form:errors path="firstName" /></form:label>
                </spring:hasBindErrors>
                <div class="clear"></div>

                <form:label for="lastName" path="lastName" cssClass="signup_label signup_label_text">And last name is:</form:label>
                <form:input path="lastName" maxlength="80" placeholder="Last name" cssClass="text" />
                <form:label for="birthday" path="birthday" cssClass="signup_label signup_label_text">How old are you?</form:label>
                <form:input path="birthday" maxlength="80" placeholder="Age" cssClass="text" />
                <form:label for="mail" path="mail" cssClass="signup_label signup_label_text">Email address to stay in touch with:</form:label>
                <form:input path="mail" maxlength="80" placeholder="Email address" cssClass="text" />
                <spring:hasBindErrors name="userRegistrationForm">
                    <form:label id="mailErrors" path="mail" cssClass="signup_label first"><form:errors path="mail" /></form:label>
                </spring:hasBindErrors>
                <div class="clear"></div>

                <form:label for="password" path="password" cssClass="signup_label signup_label_text">Password:</form:label>
                <form:input path="password" maxlength="80" placeholder="Password" cssClass="text" />
                <spring:hasBindErrors name="userRegistrationForm">
                    <form:label path="password" cssClass="signup_label first"><form:errors path="password" /></form:label>
                </spring:hasBindErrors>
                <div class="clear"></div>

                <div class="checkbox">
                    <input class="chk" type="checkbox" />
                    <span class="checkbox_txt">I agree to the Receiptofi terms</span>
                </div>

                <input id="recoverId" type="submit" value="RECOVER PASSWORD" name="recover" style="display: none; width: 200px; float: left;" class="submit_btn" />
                <input class="right submit_btn" id="login" type="submit" value="SIGN ME UP" name="signup" />
            </form:form>
            <div class="clear"></div>
        </div>
    </div>
</div>
<script type="text/javascript">

    // check name availability on focus lost
    $(document).on('change', $("#mail"), function() {
        if ($('#mail').val()) {
            checkAvailability();
        } else {
            $("#recoverId").css({'display': 'none'});
        }
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
                fieldValidated("mail", data);
            }
        });
    }

    function fieldValidated(field, result) {
        if (result.valid === "true") {
            $("#mailErrors").html("<span id='mail.errors' style='color:#065c14;'>Verification email will be sent to specified email address</span>");
            $("label[for='" + field + "']").removeAttr('class');
            $("#recoverId").css({'display': 'none'});
        } else {
            $("#mailErrors").html(result.message);
            //Add the button for recovery and hide button for SignUp
            $("#recoverId").css({'display': 'inline'});
        }
    }

    $(function () {
        $('#mailErrors').blur(function () {
            if ($(this).val().length == 0) {
                $("#mailErrors").html("<span id='mail.errors' style='color:#065c14;'>Verification email will be sent to specified email address</span>");
                $("#recoverId").css({'display': 'none'});
            }
        });
    });
</script>
</body>
</html>