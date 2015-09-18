<%@ include file="include.jsp" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin-nn.css"> <!-- Resource style -->
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
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
        <div id="tabs" class="nav-list">
            <ul class="nav-block">
                <li><a href="#tabs-1">DASHBOARD</a></li>
                <li><a href="#tabs-2">FRIENDS</a></li>
            </ul>

            <div id="tabs-1" class="report_my ajx-content" style="display: block;">
                <h1 class="h1">DASHBOARD</h1>
                <hr>
            </div>

            <div id="tabs-2" class="ajx-content report_my">
                <h1 class="h1">FRIENDS</h1>
                <hr>

                <div class="down_form" style="width: 40%">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Connected</h2>

                    <div id="friends">
                    <c:choose>
                    <c:when test="${!empty splitForm.activeProfiles}">
                        <c:forEach var="profile" items="${splitForm.activeProfiles}" varStatus="status">
                        <div class="row_field">
                            <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 360px;">
                                <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.name}
                            </label>
                        </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="r-info" id="noReceiptId">
                        <c:choose>
                        <c:when test="${!empty splitForm.pendingProfiles}">
                            Friend has yet to approve your request.
                        </c:when>
                        <c:otherwise>
                            Invite friends to split expenses.
                        </c:otherwise>
                        </c:choose>
                        </div>
                    </c:otherwise>
                    </c:choose>
                    </div>
                </div>

                <div class="down_form" style="width: 55%">
                    <c:if test="${!empty splitForm.awaitingProfiles}">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Awaiting Acceptance</h2>

                    <div id="awaiting">
                    <c:forEach var="profile" items="${splitForm.awaitingProfiles}" varStatus="status">
                    <div class="row_field" id="${profile.id}">
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 300px;">
                        <c:choose>
                            <c:when test="${!empty profile.name}">
                                <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.name}
                            </c:when>
                            <c:otherwise>
                                <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.email}
                            </c:otherwise>
                        </c:choose>
                        </label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 135px;">
                            <fmt:formatDate value="${profile.created}" pattern="MMM dd, yyyy" />
                        </label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 135px;">
                            <div class="cd-form" style="margin: 0; width: 100%;">
                                <button style="font-size: 11px !important; padding: 9px 10px; float: left;" onclick="friendRequest('${profile.id}', '${profile.authKey}', true)" id="acceptFriend_bt">Accept</button>
                                <button style="font-size: 11px !important; padding: 9px 10px;" onclick="friendRequest('${profile.id}', '${profile.authKey}', false)" id="declineFriend_bt">Decline</button>
                            </div>
                        </label>
                    </div>
                    </c:forEach>
                    </div>
                    </c:if>

                    <c:if test="${!empty splitForm.pendingProfiles}">

                    <c:choose>
                    <c:when test="${!empty splitForm.awaitingProfiles}">
                    <h2 class="h2" style="padding-bottom:2%; padding-top: 10%; text-decoration: underline;">Pending Acceptance</h2>
                    </c:when>
                    <c:otherwise>
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Pending Acceptance</h2>
                    </c:otherwise>
                    </c:choose>

                    <div id="pending">
                    <c:forEach var="profile" items="${splitForm.pendingProfiles}" varStatus="status">
                    <div class="row_field">
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 300px;">
                            <c:choose>
                                <c:when test="${!empty profile.name}">
                                    <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.name}
                                </c:when>
                                <c:otherwise>
                                    <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.email}
                                </c:otherwise>
                            </c:choose>
                        </label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px;">
                            <fmt:formatDate value="${profile.created}" pattern="MMM dd, yyyy" />
                        </label>
                    </div>
                    </c:forEach>
                    </div>

                    </c:if>
                </div>
            </div>
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
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>