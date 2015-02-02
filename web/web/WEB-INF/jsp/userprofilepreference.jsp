<%@ include file="/WEB-INF/jsp/include.jsp"%>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin1.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <%--<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>--%>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/colpick.js" type="text/javascript"></script>

    <script>
        $(function () {
            $("#tabs").tabs({
                beforeLoad: function (event, ui) {
                    ui.jqXHR.error(function () {
                        ui.panel.html(
                                "Couldn't load this tab. We'll try to fix this as soon as possible. " +
                                "If this wouldn't be a demo.");
                    });
                }
            });
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
            </c:choose>
        });
        </c:if>

        $(document).ready(function () {
            $('.timestamp').cuteTime({ refresh: 10000 });
        });
    </script>

</head>
<body>
<div class="main_wrapper">
    <div class="header">
        <div class="header_wrapper">
            <div class="header_left_content">
                <div id="logo">
                    <h1><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" href="#">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">PROFILE</a>
                <a class="top-account-bar-text" href="#">REPORT</a>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
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
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                <li><a href="#tabs-3">STATUS</a></li>
                </sec:authorize>
            </ul>

            <spring:eval expression="${profileForm.rid eq pageContext.request.userPrincipal.principal.rid}" var="isSameUser" />

            <div id="tabs-1" class="report_my ajx-content" style="display: block;">
                <h1 class="h1">PROFILE</h1>
                <hr>
                <div class="photo_section">
                    <div class="photo_part">
                        <h2 class="h2">Photo</h2>
                        <div class="pic"></div>
                    </div>
                    <div class="photo_button">
                        <input type="button" value="TAKE NEW PHOTO" style="background:#0079FF" class="read_btn">
                        <input type="button" value="UPLOAD IMAGE" style="background:#0079FF;margin: 29px 96px 0px 0px;" class="read_btn">
                    </div>
                </div>
                <div class="down_form">
                    <form:form modelAttribute="profileForm" method="post" action="i.htm">
                        <form:hidden path="rid"/>
                        <form:hidden path="updated"/>
                        <div class="row_field">
                            <label class="profile_label">First name</label>
                            <form:input path="firstName" id="userProfile_firstName" size="20" cssClass="name_txt" readonly="true" />
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Last name</label>
                            <form:input path="lastName" id="userProfile_lastName" size="20" cssClass="name_txt" readonly="true" />
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Email address</label>
                            <form:input path="mail" id="userProfile_mail" size="20" cssClass="name_txt" readonly="true" />
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Email validated</label>
                            <label class="profile_label" style="width: 274px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <c:choose>
                                    <c:when test="${pageContext.request.userPrincipal.principal.accountValidated}">
                                        Yes
                                    </c:when>
                                    <c:otherwise>
                                        No. Please validate your email.
                                    </c:otherwise>
                                </c:choose>
                            </label>
                        </div>
                        <c:if test="${!pageContext.request.userPrincipal.principal.accountValidated}">
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

                        <c:if test="${!empty profileForm.successMessage || !empty profileForm.errorMessage}">
                        <div class="row_field">
                            <div class="first first-small ajx-content">
                                <c:if test="${!empty profileForm.successMessage}">
                                    <c:out value="${profileForm.successMessage}" />
                                </c:if>
                                <c:if test="${!empty profileForm.errorMessage}">
                                    <c:out value="${profileForm.errorMessage}" />
                                </c:if>
                            </div>
                        </div>
                        </c:if>

                        <spring:hasBindErrors name="profileForm">
                        <div class="row_field">
                            <div class="first first-small ajx-content">
                                <c:if test="${errors.hasFieldErrors('firstName')}">
                                    <form:errors path="firstName" />
                                    <br>
                                </c:if>
                                <c:if test="${errors.hasFieldErrors('lastName')}">
                                    <form:errors path="lastName"  />
                                    <br>
                                </c:if>
                                <c:if test="${errors.hasFieldErrors('mail')}">
                                    <form:errors path="mail"  />
                                    <br>
                                </c:if>
                            </div>
                        </div>
                        </spring:hasBindErrors>

                        <c:choose>
                            <c:when test="${empty pageContext.request.userPrincipal.principal.pid}">
                                <div class="full" style="display: <c:out value="${(isSameUser) ? '' : 'none'}"/>">
                                    <input type="submit" value="UPDATE" style="background:#0079FF" class="read_btn" hidden="true"
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
            </div>

            <div id="tabs-2" class="ajx-content report_my">
                <h1 class="h1">PREFERENCES</h1>
                <hr>
                <h2 class="h2" style="padding-bottom:2%;">Tags</h2>
                <div class="">
                    <c:forEach var="expenseTag" items="${profileForm.expenseTags}" varStatus="status">
                    <input type="button"
                            value="&times;&nbsp;&nbsp; <spring:eval expression="expenseTag.tagName" /> <spring:eval expression="profileForm.expenseTagCount.get(expenseTag.tagName)" />"
                            style="color: <spring:eval expression="expenseTag.tagColor" />"
                            class="white_btn"
                            id="<spring:eval expression="expenseTag.id" />"
                            onclick="clickedExpenseTag(this);">
                    </c:forEach>
                </div>
                <h3 class="h3 padtop2per" style="padding-top:25px;color:#0079FF">&#43; ADD TAG</h3>
                <form:form modelAttribute="expenseTypeForm" method="post" action="i.htm">
                    <form:hidden path="tagColor"/>
                    <form:hidden path="tagId"/>

                    <div style="width: 250px">
                        <form:input path="tagName" placeholder="NEW TAG NAME" size="20" cssClass="name_txt tag_txt" />
                        <div class="color-box"></div>
                        <br/>
                        <span class="si-general-text remaining-characters">
                            <span id="textCount"></span> characters remaining
                        </span>
                    </div>

                    <spring:hasBindErrors name="expenseTypeForm">
                    <div class="row_field">
                        <div id="tagErrors" class="first first-small ajx-content">
                            <c:if test="${errors.hasFieldErrors('tagName')}">
                                <form:errors path="tagName"/>
                            </c:if>
                            <c:if test="${errors.hasFieldErrors('tagColor')}">
                                <form:errors path="tagColor"/>
                            </c:if>
                        </div>
                    </div>
                    </spring:hasBindErrors>

                    <div class="full" style="display: <c:out value="${(isSameUser) ? '' : 'none'}"/>">
                        <input type="submit" value="SAVE" class="read_btn" name="expense_tag_save_update" id="expenseTagSaveUpdate_bt"
                                style="background:#0079FF; margin: 77px 10px 0px 0px; !important;">
                        <input type="submit" value="DELETE" class="read_btn" name="expense_tag_delete" id="expenseTagDelete_bt" hidden="true"
                                style="background:#0079FF; margin: 77px 10px 0px 0px; !important;">
                    </div>
                </form:form>
            </div>

            <sec:authorize access="hasRole('ROLE_ADMIN')">
            <div id="tabs-3" class="ajx-content report_my">
                <h1 class="h1">STATUS</h1>
                <hr>
                <div class="down_form">
                    <form:form method="post" modelAttribute="profileForm" action="update.htm">
                    <form:hidden path="rid"/>
                        <div class="row_field">
                            <label class="profile_label">Profile Id</label>
                            <label class="profile_label" style="width: 260px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <spring:eval expression="profileForm.rid" />
                            </label>
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Level</label>
                            <form:select path="level" cssClass="styled-select slate">
                                <form:option value="0" label="Select Account Type" />
                                <form:options itemLabel="description" />
                            </form:select>
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Status</label>
                            <div class="profile_label">
                                <form:checkbox path="active" id="active" />
                                <label for="active">Active</label>
                            </div>
                        </div>
                        <c:if test="${!empty profileForm.successMessage || !empty profileForm.errorMessage}">
                        <div class="row_field">
                            <div class="first first-small ajx-content">
                                <c:if test="${!empty profileForm.successMessage}">
                                    <c:out value="${profileForm.successMessage}" />
                                </c:if>
                                <c:if test="${!empty profileForm.errorMessage}">
                                    <c:out value="${profileForm.errorMessage}" />
                                </c:if>
                            </div>
                        </div>
                        </c:if>
                        &nbsp;<br>
                        &nbsp;<br>
                        &nbsp;<br>
                        &nbsp;<br>
                        <input type="reset" value="RESET" name="Reset" class="read_btn" style="background:#0079FF; margin: 0; !important;" />
                        <input type="submit" value="UPDATE" name="Update" class="read_btn" style="background:#0079FF; margin: 0; !important;" />
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
</body>
<script>
    $(document).ready(function () {
        $('#tagName').NobleCount('#textCount', {
            on_negative: 'error',
            on_positive: 'success',
            max_chars: 12
        });
    });

    $('.color-box').colpick({
        colorScheme:'dark',
        layout:'hex',
        color: '${expenseTypeForm.tagColor.substring(1)}',
        onSubmit:function(hsb,hex,rgb,el) {
            $(el).css('background-color', '#'+hex);
            $(el).colpickHide();
            $('#tagColor').val('#' + hex);
        }
    }).css('background-color', '${expenseTypeForm.tagColor}');

    function clickedExpenseTag(button) {
        var buttonValue = button.value.split(" ");
        var tagName = '', space = '';
        for(var i = 0; i < buttonValue.length - 1; i ++) {
            if(i != 0) {
                tagName = tagName + space;
                space = ' ';
                tagName = tagName + buttonValue[i];
            }
        }
        $('#tagColor').val($(button).attr('style').split(" ")[1]);
        $('#tagId').val($(button).attr('id'));

        $('#tagName').focus().val(tagName);
        $('.color-box').css('background-color', $(button).attr('style').split(" ")[1]);
        $('#textCount').text(12 - tagName.length);

        $('#expenseTagSaveUpdate_bt').val('UPDATE');
        $('#expenseTagDelete_bt').attr('hidden', false);

        $('#tagErrors').hide();
    }

    <c:if test="${empty pageContext.request.userPrincipal.principal.pid}">
    $("#userProfile_firstName").on('click', function () {
        $(this).prop("readonly", false).focus();
        $('#profileUpdate_bt').attr('hidden', false);
    });
    $("#userProfile_lastName").on('click', function () {
        $(this).prop("readonly", false).focus();
        $('#profileUpdate_bt').attr('hidden', false);
    });
    $("#userProfile_mail").on('click', function () {
        $(this).prop("readonly", false).focus();
        $('#profileUpdate_bt').attr('hidden', false);
    });
    </c:if>
</script>
</html>