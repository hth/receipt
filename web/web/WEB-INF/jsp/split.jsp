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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin-nn.css">
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.0.0/jquery-migrate.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/highcharts.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/colpick.js" ></script>

    <script>
        $(function () {
            $("#tabs").tabs({
                activate: function () {
                    $(window).resize();
                }
            });
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

        $(document).ready(function () {
            "use strict";

            $("#friend_title_id").removeClass("temp_offset");
            $("#friend_id").removeClass("temp_offset");
            $("#friend_awaiting_id").removeClass("temp_offset");
        });
    </script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/access/landing.htm">Receiptofi</a></h1>
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
                <li><a href="#tabs-1">OWE's YOU</a></li>
                <li><a href="#tabs-2">YOU OWE</a></li>
                <li><a href="#tabs-3">FRIENDS</a></li>
            </ul>

            <div id="tabs-1" class="report_my ajx-content" style="display: block;">
                <c:choose>
                    <c:when test="${!empty splitForm.jsonOweMe}">
                        <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Friends owe you</h2>
                        <p class="analysis-text">
                            Shows amount owed by each friend you have split your receipts with.
                        </p>
                        <p class="analysis-text">
                            You can only <b>'Settle'</b> when you owe your friend. Once you hit settle, transaction
                            cannot be reverted. Once transaction is settled, you will not be able to delete or request
                            recheck on a receipt. Nor would you be able to remove or add friends from splitting expenses.
                        </p>
                        <p class="analysis-text">
                            <b>'Let go'</b> when you have already settled with your friend and would not like to change
                            net amount owed. Your friend would still be able to see shared receipt.
                        </p>

                        <div id="containerOwesMe" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>

                        <div class="rightside-list-holder full-list-holder">
                            <c:forEach items="${splitForm.yourSplitExpenses}" var="taskStats" varStatus="status">
                            <div class="receipt-detail-holder border" style="padding-bottom: 20px;" id="${status.count}">
                                <p class="analysis" style="padding-bottom: 10px; color: black">${taskStats.key}</p>

                                <ul>
                                    <c:forEach items="${taskStats.value}" var="splitExpense" varStatus="status">
                                    <li style="width: 850px;" id="${splitExpense.id}">
                                        <span class="rightside-li-date-text counter-li-text"><fmt:formatNumber value="${status.count}" pattern="00"/></span>
                                        <span class="rightside-li-date-text"><fmt:formatDate value="${splitExpense.receiptDate}" pattern="MMM dd, yyyy"/></span>
                                        <span class="rightside-li-date-text full-li-date-text" style="width: 350px !important;">
                                            <a href="${pageContext.request.contextPath}/access/receipt/${splitExpense.receiptDocumentId}.htm"
                                                    class="rightside-li-middle-text" target="_blank">
                                                <spring:eval expression="splitExpense.bizName.businessName"/>
                                            </a>
                                        </span>
                                        <span class="rightside-li-date-text" style="color: black">${splitExpense.splitTotalString}</span>
                                        <span class="rightside-li-date-text">
                                            <c:if test="${splitForm.canBeSettledWithFriend(splitExpense.friendUserId)}">
                                            <div class="gd-button-holder" style="width: 50px;">
                                                <button class="gd-button" onclick="settleSplit('${splitExpense.id}');"
                                                        style="width: 100px; height: 30px; padding: 0;">
                                                    SETTLE
                                                </button>
                                            </div>
                                            </c:if>
                                        </span>
                                    </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="r-info" id="noReceiptId">
                            No one owes you.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div id="tabs-2" class="report_my ajx-content" style="display: block;">
                <c:choose>
                    <c:when test="${!empty splitForm.jsonOweOthers}">
                        <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">You owe friends</h2>
                        <p class="analysis-text">
                            Shows amount you owe to each friend who has split their receipt with you.
                        </p>

                        <div id="containerOwesOthers" style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto"></div>

                        <div class="rightside-list-holder full-list-holder">
                            <c:forEach items="${splitForm.friendsSplitExpenses}" var="taskStats" varStatus="status">
                            <div class="receipt-detail-holder border" style="padding-bottom: 20px;">
                                <p class="analysis" style="padding-bottom: 10px; color: black">${taskStats.key}</p>

                                <ul>
                                    <c:forEach items="${taskStats.value}" var="splitExpense" varStatus="status">
                                    <li style="width: 850px;">
                                        <span class="rightside-li-date-text counter-li-text"><fmt:formatNumber value="${status.count}" pattern="00"/></span>
                                        <span class="rightside-li-date-text"><fmt:formatDate value="${splitExpense.receiptDate}" pattern="MMM dd, yyyy"/></span>
                                        <span class="rightside-li-date-text full-li-date-text" style="width: 350px !important;">
                                            <a href="${pageContext.request.contextPath}/access/receipt/${splitExpense.receiptDocumentId}.htm"
                                                    class="rightside-li-middle-text" target="_blank">
                                                <spring:eval expression="splitExpense.bizName.businessName"/>
                                            </a>
                                        </span>
                                        <span class="rightside-li-date-text" style="color: black">${splitExpense.splitTotalString}</span>
                                    </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="r-info" id="noReceiptId">
                            You owe nothing to any of your friends.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div id="tabs-3" class="ajx-content report_my">
                <h1 class="h1 temp_offset" id="friend_title_id">FRIENDS</h1>
                <hr class="temp_offset">

                <div class="down_form temp_offset" id="friend_id" style="width: 45%">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Connected</h2>

                    <div id="friends">
                    <c:choose>
                    <c:when test="${!empty splitForm.activeProfiles}">
                        <c:forEach var="profile" items="${splitForm.activeProfiles}" varStatus="status">
                        <div class="row_field" id="${profile.id}">
                            <label class="profile_label" style="!important; color: #606060 !important; font-weight: normal !important; line-height: 30px; width: 360px;">
                                <div class="member" style="background-color: #00529B">
                                    <span class="member-initials">${profile.initials}</span>
                                </div>
                                &nbsp;
                                <img src="${pageContext.request.contextPath}/static/images/connectedx32.png"
                                        title="Connection" width="25" style="vertical-align: middle"
                                        onclick="unfriendRequest('${profile.email}', '${profile.name}', '${profile.id}', event);">
                                &nbsp;
                                ${profile.name}
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

                <div class="down_form temp_offset" id="friend_awaiting_id" style="width: 50%">
                    <c:if test="${!empty splitForm.awaitingProfiles}">
                    <h2 class="h2" style="padding-bottom:2%; text-decoration: underline;">Awaiting Acceptance</h2>

                    <div id="awaiting">
                    <c:forEach var="profile" items="${splitForm.awaitingProfiles}" varStatus="status">
                    <div class="row_field" id="${profile.id}">
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 290px;">
                        <c:choose>
                            <c:when test="${!empty profile.name}">
                                <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.name}
                            </c:when>
                            <c:otherwise>
                                <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.email}
                            </c:otherwise>
                        </c:choose>
                        </label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 150px;">
                            <fmt:formatDate value="${profile.createdDate}" pattern="MMM dd, yyyy" />
                        </label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 150px;">
                            <div class="cd-form" style="margin: 0; width: 100%;">
                                <button style="font-size: 11px !important; padding: 9px 10px; float: left;"
                                        onclick="friendRequest('${profile.id}', '${profile.authKey}', 'A')"
                                        id="acceptFriend_bt">Accept</button>
                                <button style="font-size: 11px !important; padding: 9px 10px;"
                                        onclick="friendRequest('${profile.id}', '${profile.authKey}', 'D')"
                                        id="declineFriend_bt">Decline</button>
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
                    <div class="row_field" id="${profile.id}">
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 290px;">
                            <c:choose>
                                <c:when test="${!empty profile.name}">
                                    <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.name}
                                </c:when>
                                <c:otherwise>
                                    <div class="member" style="background-color: #00529B"><span class="member-initials">${profile.initials}</span></div>&nbsp;&nbsp;${profile.email}
                                </c:otherwise>
                            </c:choose>
                        </label>
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 150px;">
                            <c:choose>
                                <c:when test="${profile.profileActive}">
                                    <fmt:formatDate value="${profile.createdDate}" pattern="MMM dd, yyyy" />
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${profile.provider.equals('FACEBOOK')}">
                                            <span class="fa fa-facebook social-awesome-icon" style="vertical-align: middle; background: #3B5998; color: #ffffff; padding: 3px;"></span> sign up pending
                                        </c:when>
                                        <c:when test="${profile.provider.equals('GOOGLE')}">
                                            <span class="fa fa-google-plus social-awesome-icon" style="vertical-align: middle; background: #dd4b39; color: #ffffff; padding: 3px;"></span> sign up pending
                                        </c:when>
                                        <c:otherwise>
                                            Sign up pending
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </label
                        <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 150px;">
                            <div class="cd-form" style="margin: 0; width: 100%;">
                                <button style="font-size: 11px !important; padding: 9px 10px; float: left;"
                                        onclick="friendRequest('${profile.id}', '${profile.authKey}', 'C')"
                                        id="cancelFriend_bt">Cancel Invite</button>
                            </div>
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
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script>
    $(function () {
        $('#containerOwesMe').highcharts({
            credits: {
                enabled: false
            },
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: 0,
                plotShadow: false
            },
            title: {
                text: 'Owes you',
                align: 'center',
                verticalAlign: 'middle',
                y: 40
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.y:.2f}</b>'
            },
            plotOptions: {
                pie: {
                    dataLabels: {
                        enabled: true,
                        distance: -50,
                        style: {
                            fontWeight: 'bold',
                            color: 'white',
                            textShadow: '0px 1px 2px black'
                        }
                    },
                    startAngle: -90,
                    endAngle: 90,
                    center: ['50%', '75%']
                }
            },
            series: [{
                type: 'pie',
                name: 'Owes you',
                innerSize: '50%',
                data: [
                    <c:forEach var="oweExpense" items="${splitForm.jsonOweMe}" varStatus="status">
                    ['${oweExpense.name}', ${oweExpense.splitTotal}],
                    </c:forEach>
                ]
            }]
        });
    });

    $(function () {
        $('#containerOwesOthers').highcharts({
            credits: {
                enabled: false
            },
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: 0,
                plotShadow: false
            },
            title: {
                text: 'You owe',
                align: 'center',
                verticalAlign: 'middle',
                y: 40
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.y:.2f}</b>'
            },
            plotOptions: {
                pie: {
                    dataLabels: {
                        enabled: true,
                        distance: -50,
                        style: {
                            fontWeight: 'bold',
                            color: 'white',
                            textShadow: '0px 1px 2px black'
                        }
                    },
                    startAngle: -90,
                    endAngle: 90,
                    center: ['50%', '75%']
                }
            },
            series: [{
                type: 'pie',
                name: 'You owe',
                innerSize: '50%',
                data: [
                    <c:forEach var="oweExpense" items="${splitForm.jsonOweOthers}" varStatus="status">
                    ['${oweExpense.name}', ${oweExpense.splitTotal}],
                    </c:forEach>
                ]
            }]
        });
    });
</script>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>