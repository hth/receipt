<%@ include file="include.jsp"%>
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
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/0.5.0/sweet-alert.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/0.5.0/sweet-alert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/colpick.js" type="text/javascript"></script>

    <script>
        $(function () {
            $("#tabs").tabs();
        });

        <c:if test="${!empty showTab}">
        $(function () {
            <c:choose>
            <c:when test="${showTab eq '#tabs-2'}">
            $("#tabs").tabs({active: 1});
            </c:when>
            <c:when test="${showTab eq '#tabs-3'}">
            $("#tabs").tabs({active: 2});
            </c:when>
            <c:when test="${showTab eq '#tabs-4'}">
            $("#tabs").tabs({active: 3});
            </c:when>
            </c:choose>
        });
        </c:if>
    </script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/access/landing.htm">Receiptofi</a></h1>
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
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
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
        <div id="tabs" class="nav-list">
            <ul class="nav-block">
                <li><a href="#tabs-1">PROFILE</a></li>
                <li><a href="#tabs-2">PREFERENCES</a></li>
                <li><a href="#tabs-3">BILLING</a></li>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                <li><a href="#tabs-4">STATUS</a></li>
                </sec:authorize>
            </ul>

            <spring:eval expression="${profileForm.rid eq pageContext.request.userPrincipal.principal.rid}" var="isSameUser" />

            <div id="tabs-1" class="report_my ajx-content" style="display: block;">
                <h1 class="h1">PROFILE</h1>
                <hr>
                <div class="down_form">
                    <form:form modelAttribute="profileForm" method="post" action="i.htm">
                        <form:hidden path="rid"/>
                        <form:hidden path="updated"/>

                        <spring:hasBindErrors name="profileForm">
                        <div class="r-validation" style="width: 98%; margin: 0 0 0 0;">
                            <c:if test="${errors.hasFieldErrors('firstName')}">
                                <form:errors path="firstName" /><br>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('lastName')}">
                                <form:errors path="lastName" /><br>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('mail')}">
                                <form:errors path="mail" /><br>
                            </c:if>
                        </div>
                        </spring:hasBindErrors>

                        <c:if test="${!empty profileForm.successMessage}">
                        <div class="r-success" style="width: 98%; margin: 0 0 0 0;">
                            <c:out value="${profileForm.successMessage}" />
                        </div>
                        </c:if>
                        <c:if test="${!empty profileForm.errorMessage}">
                        <div class="r-error" style="width: 98%; margin: 0 0 0 0;">
                            <c:out value="${profileForm.errorMessage}" />
                        </div>
                        </c:if>

                        <div class="row_field">
                            <form:label for="firstName" path="firstName" cssClass="profile_label"
                                    cssErrorClass="profile_label lb_error">First name</form:label>
                            <form:input path="firstName" id="userProfile_firstName" size="20" cssClass="name_txt" readonly="true" />
                        </div>
                        <div class="row_field">
                            <form:label for="lastName" path="lastName" cssClass="profile_label"
                                    cssErrorClass="profile_label lb_error">Last name</form:label>
                            <form:input path="lastName" id="userProfile_lastName" size="20" cssClass="name_txt" readonly="true" />
                        </div>
                        <div class="row_field">
                            <form:label for="mail" path="mail" cssClass="profile_label"
                                    cssErrorClass="profile_label lb_error">Email address</form:label>
                            <form:input path="mail" id="userProfile_mail" size="20" cssClass="name_txt" readonly="true" />
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Email validated</label>
                            <label class="profile_label" style="width: 274px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <c:choose>
                                    <c:when test="${profileForm.accountValidated}">
                                        Yes
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: red; font-weight: bold">No. Please validate your email.</span>
                                    </c:otherwise>
                                </c:choose>
                            </label>
                        </div>
                        <c:if test="${!profileForm.accountValidated}">
                        <div class="row_field">
                            <label class="profile_label">Account</label>
                            <label class="profile_label" style="width: 274px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <c:choose>
                                    <c:when test="${profileForm.accountValidationExpired}">
                                        Disabled since <span class="timestamp"><fmt:formatDate value="${profileForm.accountValidationExpireDay}" type="both"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        Disables after
                                        <span style="color: red; font-weight: bold"><fmt:formatDate value="${profileForm.accountValidationExpireDay}" type="both" pattern="MMM dd, yyyy"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </label>
                        </div>
                        </c:if>
                        <div class="row_field">
                            <label class="profile_label">Last modified</label>
                            <label class="profile_label" style="width: 274px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <fmt:formatDate value="${profileForm.updated}" type="both"/>
                            </label>
                        </div>

                        <c:choose>
                            <c:when test="${empty pageContext.request.userPrincipal.principal.pid}">
                                <div class="full" style="display: <c:out value="${(isSameUser) ? '' : 'none'}"/>">
                                    <input type="submit" value="UPDATE" style="background: #2c97de;" class="read_btn" disabled="disabled"
                                            name="profile_update" id="profileUpdate_bt">
                                </div>
                            </c:when>
                            <c:otherwise>
                                <label class="profile_label" style="padding-top: 40px; width: 400px; !important; color: #606060; !important; font-weight: bold; !important;">
                                    <c:out value="${pageContext.request.userPrincipal.principal.pid}"/> Social signup account.
                                    Please update your social account to see changes here.
                                </label>
                            </c:otherwise>
                        </c:choose>
                    </form:form>
                </div>
                <c:if test="${!empty profileForm.profileImage}">
                <div class="down_form">
                    <div class="photo_section">
                        <div class="photo_part">
                            <div class="pic">
                                <img width="170" height="175" alt=" Image from social profile"
                                        style="font-size: 0.9em; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;"
                                        src="${profileForm.profileImage}" />
                            </div>
                        </div>
                        <!-- add photo option for future release -->
                        <%--<div class="photo_button">--%>
                            <%--<input type="button" value="TAKE NEW PHOTO" style="background:#0079FF" class="read_btn">--%>
                            <%--<input type="button" value="UPLOAD IMAGE" style="background:#0079FF;margin: 29px 96px 0px 0px;" class="read_btn">--%>
                        <%--</div>--%>
                    </div>
                </div>
                </c:if>
            </div>

            <div id="tabs-2" class="ajx-content report_my">
                <h1 class="h1">PREFERENCES</h1>
                <hr>

                <div class="r-success" style="width: 98%; margin: 20px 0 10px 0; display: none" id="expenseTagSuccess"></div>
                <div class="r-error" style="width: 98%; margin: 20px 0 10px 0; display: none" id="expenseTagError"></div>

                <h2 class="h2" style="padding-bottom:2%;">Tag Expenses</h2>
                <div class="">
                    <c:forEach var="expenseTag" items="${profileForm.expenseTags}" varStatus="status">
                    <input type="button"
                            value="&times;&nbsp;&nbsp; <spring:eval expression="expenseTag.tagName" /> &nbsp;<spring:eval expression="profileForm.expenseTagCount.get(expenseTag.tagName)" />"
                            style="color: <spring:eval expression="expenseTag.tagColor" />"
                            class="white_btn"
                            id="<spring:eval expression="expenseTag.id" />"
                            onclick="clickedExpenseTag(this);">
                    </c:forEach>
                </div>
                <h3 class="h3 padtop2per" style="padding-top:25px;color:#0079FF;">&#43; ADD TAG</h3>
                <form:form modelAttribute="expenseTagForm" method="post" action="i.htm">
                    <form:hidden path="tagColor"/>
                    <form:hidden path="tagId"/>

                    <spring:hasBindErrors name="expenseTagForm">
                    <div class="row_field">
                        <div id="tagErrors" class="r-validation">
                            <c:if test="${errors.hasFieldErrors('tagName')}">
                                <form:errors path="tagName"/><br/>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('tagColor')}">
                                <form:errors path="tagColor"/><br/>
                            </c:if>
                        </div>
                    </div>
                    </spring:hasBindErrors>

                    <div style="width: 250px">
                        <form:input path="tagName" placeholder="NEW TAG NAME" size="20" cssClass="name_txt tag_txt" />
                        <div class="color-box"></div>
                        <br/>
                        <span class="si-general-text remaining-characters">
                            <span id="textCount"></span> characters remaining
                        </span>
                    </div>

                    <div class="full" style="display: <c:out value="${(isSameUser) ? '' : 'none'}"/>">
                        <input type="submit" value="SAVE" class="read_btn" name="expense_tag_save_update" id="expenseTagSaveUpdate_bt"
                                style="background: #808080; margin: 77px 10px 0 0;" disabled="disabled">
                        <input type="submit" value="DELETE" class="read_btn" name="expense_tag_delete" id="expenseTagDelete_bt" hidden="hidden"
                                style="background:#FC462A; margin: 77px 10px 0 0;">
                    </div>
                </form:form>
            </div>

            <div id="tabs-3" class="ajx-content report_my">
                <h1 class="h1">BILLING &amp; USAGE</h1>
                <hr>
                <div class="down_form">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Billing</h2>

                    <div class="row_field">
                        <label class="profile_label" style="width: 150px;">Plan Subscribed</label>
                        <%--<form:form method="post" modelAttribute="billingForm">--%>
                            <%--<form:select path="billingAccountType" cssClass="styled-select slate" cssStyle="width: 175px;" onchange="">--%>
                                <%--<form:option value="0" label="Select Billing Type" />--%>
                                <%--<form:options itemLabel="description" />--%>
                            <%--</form:select>--%>
                        <%--</form:form>--%>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important;">
                            ${billingForm.billingPlan.description}
                        </label>
                    </div>
                    <div class="row_field">
                        <label class="profile_label" style="width: 150px;">Billed</label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; width: 150px">
                            1<sup style="font-size:small; vertical-align:super;">st</sup> of every month
                        </label>
                    </div>
                </div>

                <div class="down_form">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Disk Usage</h2>
                    <div class="row_field">
                        <label class="profile_label" style="width: 150px;">Used</label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important;">
                            <fmt:formatNumber value="${billingForm.totalSLN_MB}"/> MB
                        </label>
                    </div>
                    <div class="row_field">
                        <label class="profile_label" style="width: 150px;">Pending</label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important;">
                            <c:choose>
                                <c:when test="${billingForm.pendingDiskUsage_MB.unscaledValue() == 0}">-</c:when>
                                <c:otherwise><fmt:formatNumber value="${billingForm.pendingDiskUsage_MB}"/> MB *</c:otherwise>
                            </c:choose>
                        </label>
                    </div>
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <div class="row_field">
                            <label class="profile_label" style="width: 150px;">Usage saved by scaling</label>
                            <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important;">
                                <fmt:formatNumber value="${billingForm.diskSaved_MB}"/> MB
                            </label>
                        </div>
                    </sec:authorize>
                </div>

                <div class="down_form" style="width: 99%">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Billing History</h2>

                    <div class="row_field">
                        <label class="profile_label" style="width: 100px;">Month</label>
                        <label class="profile_label" style="width: 160px;">Plan</label>
                        <label class="profile_label" style="width: 150px;">Bill Status</label>
                        <label class="profile_label" style="width: 75px;">Id</label>
                        <label class="profile_label" style="width: 150px;">Tx Status</label>
                        <label class="profile_label" style="width: 240px;">Date</label>
                    </div>
                    <c:forEach var="billing" items="${billingForm.billings}"  varStatus="status">
                    <div class="row_field">
                        <label class="profile_label" style="width: 100px; font-weight: normal; !important;">
                            ${billing.billedForMonthYear}
                        </label>
                        <label class="profile_label" style="width: 160px; font-weight: normal; !important;">
                            ${billing.billingPlan.description}
                        </label>
                        <label class="profile_label" style="width: 150px; font-weight: normal; !important;">
                            ${billing.billedStatus.description}
                        </label>
                        <label class="profile_label" style="width: 75px; font-weight: normal; !important;">
                            ${billing.transactionId}
                        </label>
                        <label class="profile_label" style="width: 150px; font-weight: normal; !important;">
                            ${billing.transactionStatus.description}
                        </label>
                        <label class="profile_label" style="width: 240px; font-weight: normal; !important;">
                            <c:choose>
                            <c:when test="${billing.billedStatus eq 'NB'}">
                                <span style="color: red; font-weight: bold">Payment Due</span>
                            </c:when>
                            <c:when test="${billing.billedStatus eq 'P'}">
                                NA
                            </c:when>
                            <c:when test="${billing.billedStatus eq 'B' || billing.billedStatus eq 'R'}">
                                <fmt:formatDate value="${billing.updated}" pattern="MMM dd, yyyy hh:mm:ss a z"/>
                            </c:when>
                            <c:otherwise>
                                <span style="color: red; font-weight: bold">Contact Support</span>
                            </c:otherwise>
                            </c:choose>
                        </label>
                    </div>
                    </c:forEach>
                </div>
            </div>

            <sec:authorize access="hasRole('ROLE_ADMIN')">
            <div id="tabs-4" class="ajx-content report_my">
                <h1 class="h1">STATUS</h1>
                <hr>
                <div class="down_form">
                    <form:form method="post" modelAttribute="profileForm" action="update.htm">
                    <form:hidden path="rid"/>

                    <c:if test="${!empty profileForm.successMessage}">
                    <div class="r-success" style="width: 98%; margin: 0 0 0 0;">
                        <c:out value="${profileForm.successMessage}" />
                    </div>
                    </c:if>
                    <c:if test="${!empty profileForm.errorMessage}">
                    <div class="r-error" style="width: 98%; margin: 0 0 0 0;">
                        <c:out value="${profileForm.errorMessage}" />
                    </div>
                    </c:if>

                    <div class="row_field">
                        <label class="profile_label">Profile Id</label>
                        <label class="profile_label" style="width: 260px; !important; color: #606060; !important; font-weight: normal; !important;">
                            <spring:eval expression="profileForm.rid" />
                        </label>
                    </div>
                    <div class="row_field">
                        <form:label for="level" path="level" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Level</form:label>
                        <form:select path="level" cssClass="styled-select slate">
                            <form:option value="0" label="Select Account Type" />
                            <form:options itemLabel="description" />
                        </form:select>
                    </div>
                    <div class="row_field">
                        <form:label for="active" path="active" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Status</form:label>
                        <div class="profile_label">
                            <form:checkbox path="active" id="active" />
                            <label for="active">&nbsp; Active</label>
                        </div>
                    </div>
                    &nbsp;<br>
                    &nbsp;<br>
                    &nbsp;<br>
                    &nbsp;<br>
                    <input type="reset" value="RESET" name="Reset" class="read_btn" style="background:#2c97de; margin: 0; !important;" />
                    <input type="submit" value="UPDATE" name="Update" class="read_btn" style="background:#2c97de; margin: 0; !important;" />
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
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
        confirmBeforeAction();
        userProfilePreferences();

        $('.color-box').colpick({
            colorScheme:'dark',
            layout:'hex',
            color: '${expenseTagForm.tagColor.substring(1)}',
            onSubmit:function(hsb,hex,rgb,el) {
                $(el).css('background-color', '#'+hex);
                $(el).colpickHide();
                $('#tagColor').val('#' + hex);
            }
        }).css('background-color', '${expenseTagForm.tagColor}');
    });

    <c:if test="${empty pageContext.request.userPrincipal.principal.pid}">
    $("#userProfile_firstName").on('click', function () {
        $(this).prop("readonly", false).focus();
        $('#profileUpdate_bt').attr('disabled', false).css('background', '#2c97de');
    });
    $("#userProfile_lastName").on('click', function () {
        $(this).prop("readonly", false).focus();
        $('#profileUpdate_bt').attr('disabled', false).css('background', '#2c97de');
    });
    $("#userProfile_mail").on('click', function () {
        $(this).prop("readonly", false).focus();
        $('#profileUpdate_bt').attr('disabled', false).css('background', '#2c97de');
    });
    </c:if>
</script>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>