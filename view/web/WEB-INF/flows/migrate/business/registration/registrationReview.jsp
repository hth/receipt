<%@ include file="../../../../jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/business/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username" />
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username" />
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<header>
</header>
<div class="main clearfix">
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
        <sec:authorize access="hasRole('ROLE_BUSINESS')">
        <div class="business_reg">
            <div class="down_form" style="width: 90%">
                <form:form commandName="register">
                    <h1 class="h1">Confirm your personal and business details</h1>
                    <hr>
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

                    <div class="row_field">
                        <form:label path="registerUser.firstName" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">First name</form:label>
                        <form:input path="registerUser.firstName" size="20" cssClass="name_txt" readonly="true" />
                    </div>
                    <div class="row_field">
                        <form:label path="registerUser.lastName" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Last name</form:label>
                        <form:input path="registerUser.lastName" size="20" cssClass="name_txt" readonly="true" />
                    </div>
                    <div class="row_field">
                        <form:label path="registerUser.address" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Your Address</form:label>
                        <form:input path="registerUser.address" size="200" cssClass="name_txt" readonly="true" style="width: 600px;" />
                    </div>
                    <div class="row_field">
                        <form:label path="registerUser.phone" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Your Phone</form:label>
                        <form:input path="registerUser.phone" size="20" cssClass="name_txt" readonly="true" />
                    </div>

                    <div class="row_field">
                        <form:label path="registerBusiness.name" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Business Name</form:label>
                        <form:input path="registerBusiness.name" size="20" cssClass="name_txt" readonly="true" />
                    </div>
                    <div class="row_field">
                        <form:label path="registerBusiness.businessTypes" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Business Type</form:label>
                        <form:select path="registerBusiness.businessTypes" cssClass="styled-select slate" multiple="true" style="height: 100px;">
                            <form:options items="${register.registerBusiness.availableBusinessTypes}"
                                    itemValue="name" itemLabel="description" disabled="true" />
                        </form:select>
                    </div>
                    <div class="row_field">
                        <form:label path="registerBusiness.address" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Business Address</form:label>
                        <form:input path="registerBusiness.address" size="200" cssClass="name_txt" readonly="true" style="width: 600px;" />
                    </div>
                    <div class="row_field">
                        <form:label path="registerBusiness.phone" cssClass="profile_label" cssStyle="width: 145px;"
                                cssErrorClass="profile_label lb_error">Business Phone</form:label>
                        <form:input path="registerBusiness.phone" size="20" cssClass="name_txt" readonly="true" />
                    </div>

                    <div class="full">
                        <c:if test="${register.registerUser.emailValidated}">
                            <input type="submit" value="CONFIRM" class="read_btn" name="_eventId_confirm"
                                    style="background: #2c97de; margin: 77px 10px 0 0;">
                        </c:if>
                        <input type="submit" value="REVISE" class="read_btn" name="_eventId_revise"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel"
                                style="background: #FC462A; margin: 77px 10px 0 0;" formnovalidate>
                    </div>
                </form:form>
            </div>
        </div>
        </sec:authorize>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2017 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>