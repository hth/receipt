<%@ include file="/WEB-INF/jsp/include.jsp" %>
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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
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
                <a class="top-account-bar-text" href="/access/signoff.htm">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username"/>
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username"/>
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
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            Search User
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: 800px;">
        <div class="down_form" style="width: 95%;">
            <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Search users to change profile settings</h2>
            <form:form method="post" modelAttribute="userSearchForm" action="landing.htm">
                <div class="row_field">
                    <label class="profile_label">
                        Search Name
                    </label>
                    <form:input path="userName" id="userName" size="15" cssClass="name_txt" />
                </div>
                <div class="row_field">
                    Enter at least 3 characters to find a specific user or else its list all the user below.
                    Would change this later as the number of users increases.
                </div>
                <c:if test="${!empty userSearchForm.userProfiles}">
                    <div class="small_margin"></div>
                    <div class="rightside-list-holder" style="width: 920px; min-height: 50px; height: 50px; overflow-y: hidden; margin-bottom: 0px;">
                        <ul>
                            <li style="width: 900px;">
                                <span class="rightside-li-date-text" style="width: 20px;"></span>
                                <span class="rightside-li-date-text" style="width: 180px;">Level</span>
                                <a href="#" class="rightside-li-middle-text" style="width: 340px;">First, Last Name</a>
                                <span class="rightside-li-right-text" style="width: 340px;">Receiptofi Id</span>
                            </li>
                        </ul>
                    </div>
                    <div class="rightside-list-holder mouseScroll" style="width: 920px;">
                        <ul>
                            <c:forEach var="userProfile" items="${userSearchForm.userProfiles}"  varStatus="status">
                                <li style="width: 900px;">
                                    <span class="rightside-li-date-text" style="width: 20px;">${status.count}</span>
                                    <span class="rightside-li-date-text" style="width: 180px;"><spring:eval expression="userProfile.level.description" /></span>
                                    <a href="${pageContext.request.contextPath}/access/userprofilepreference/their.htm?id=${userProfile.receiptUserId}"
                                            class="rightside-li-middle-text" style="width: 340px;" target="_blank">
                                        <spring:eval expression="userProfile.name" />
                                    </a>
                                    <span class="rightside-li-right-text" style="width: 340px;">${userProfile.email}</span>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </form:form>
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED. (<fmt:message key="build.version" />)
    </div>
</div>
<script>
    $(document).ready(function() {
        $( "#userName" ).autocomplete({
            source: "${pageContext. request. contextPath}/admin/find_user.htm"
        });
    });
</script>
</body>
</html>