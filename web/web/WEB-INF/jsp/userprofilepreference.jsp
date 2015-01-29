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
                    <%--<form>--%>
                        <div class="row_field">
                            <label class="profile_label">First name</label>
                            <input type="text" name="" required="true" size="20" class="name_txt"
                                    value="<spring:eval expression="userProfilePreferenceForm.userProfile.firstName"/>">
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Last name</label>
                            <input type="text" required="true" size="20" name="" class="name_txt"
                                    value="<spring:eval expression="userProfilePreferenceForm.userProfile.lastName"/>">
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Email address</label>
                            <input type="text" name="" size="20" class="name_txt"
                                    value="<spring:eval expression="userProfilePreferenceForm.userProfile.email"/>">
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Last modified</label>
                            <label class="profile_label" style="width: 260px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <fmt:formatDate value="${userProfilePreferenceForm.userProfile.updated}" type="both"/>
                            </label>
                        </div>
                        <input type="button" value="SAVE" style="background:#0079FF" class="read_btn">
                    <%--</form>--%>
                </div>
            </div>

            <div id="tabs-2" class="ajx-content report_my">
                <spring:eval expression="${userProfilePreferenceForm.userProfile.receiptUserId eq pageContext.request.userPrincipal.principal.rid}" var="isSameUser" />

                <h1 class="h1">PREFERENCES</h1>
                <hr>
                <h2 class="h2" style="padding-bottom:2%;">Tags</h2>
                <div class="">
                    <c:forEach var="expenseTag" items="${userProfilePreferenceForm.expenseTags}" varStatus="status">
                    <input type="button"
                            value="&times;&nbsp;&nbsp; <spring:eval expression="expenseTag.tagName" /> <spring:eval expression="userProfilePreferenceForm.expenseTagCount.get(expenseTag.tagName)" />"
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
                        <form:input path="tagName" placeholder="NEW TAG" size="20" cssClass="name_txt tag_txt" />
                        <div class="color-box"></div>
                        <br/>
                        <span class="si-general-text remaining-characters">
                            <span id="textCount"></span> characters remaining
                        </span>
                        <br/><br/>
                    </div>
                    <form:errors path="tagName" cssClass="first first-small ajx-content" />
                    <form:errors path="tagColor" cssClass="first first-small ajx-content" />

                    <div class="full">
                        <input type="submit" value="SAVE" class="read_btn" name="expense_tag"
                                style="background:#0079FF; margin-top:46px; <c:out value="${(isSameUser) ? '' : 'disabled'}"/>">
                    </div>
                </form:form>
            </div>

            <sec:authorize access="hasRole('ROLE_ADMIN')">
            <div id="tabs-3" class="ajx-content report_my">
                <h1 class="h1">STATUS</h1>
                <hr>
                <div class="down_form">
                    <form:form method="post" modelAttribute="userProfilePreferenceForm" action="update.htm">
                    <form:hidden path="userProfile.receiptUserId"/>
                        <div class="row_field">
                            <label class="profile_label">Profile Id</label>
                            <label class="profile_label" style="width: 260px; !important; color: #606060; !important; font-weight: normal; !important;">
                                <spring:eval expression="userProfilePreferenceForm.userProfile.receiptUserId" />
                            </label>
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Level</label>
                            <form:select path="userProfile.level" cssClass="styled-select slate">
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
        var tagName = '', space = '';
        for(var i = 0; i < button.value.split(" ").length - 1; i ++) {
            if(i != 0) {
                tagName = tagName + space;
                space = ' ';
                tagName = tagName + button.value.split(" ")[i];
            }
        }
        $('#tagName').focus().val(tagName);
        $('#tagColor').val($(button).attr('style').split(" ")[1]);
        $('.color-box').css('background-color', $(button).attr('style').split(" ")[1])
        $('#textCount').text(12 - tagName.length);
        $('#tagId').val($(button).attr('id'))
    }
</script>
</html>