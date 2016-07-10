<%@ include file="../../include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="receipt.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.admin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.0.0/jquery-migrate.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<spring:eval expression="pageContext.request.userPrincipal.principal.userLevel ge T(com.receiptofi.domain.types.UserLevelEnum).SUPERVISOR" var="hasAccess" />
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/access/landing.htm"><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/>Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <c:if test="${hasAccess}">
                    <a class="top-account-bar-text" href="/emp/receipt/quality.htm">RECEIPT QUALITY</a>
                    <a class="top-account-bar-text" href="/emp/campaign/landing.htm">CAMPAIGN</a>
                </c:if>
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
    <div class="down_form" style="width: 90%">
        Total awaiting approvals: ${campaignListForm.campaignCount}
    </div>
    <div class="rightside-list-holder full-list-holder"
            style="overflow-y: hidden; height: 800px; margin-left: 0; padding-left: 0">
        <div class="down_form" style="width: 96%;">
            <c:choose>
            <c:when test="${!empty campaignListForm.businessCampaigns}">
            <table width="100%" style="margin: 0 4px 0 4px">
                <tr>
                    <th style="text-align: left;"></th>
                    <th style="text-align: left;">Text</th>
                    <th style="text-align: left;">Duration</th>
                    <th style="text-align: left;">Campaign Live Date</th>
                    <th style="text-align: left;">Distribution</th>
                    <th style="text-align: left;">State</th>
                    <th style="text-align: left;">Last Modified</th>
                </tr>
                <c:forEach items="${campaignListForm.businessCampaigns}" var="item" varStatus="status">
                <tr>
                    <td style="padding: 10px; border: 1px solid #ccc">${status.count}&nbsp;</td>
                    <td style="padding: 10px; border: 1px solid #ccc"><a href="/emp/campaign/${item.id}.htm">${item.freeText}</a></td>
                    <td style="padding: 10px; border: 1px solid #ccc">${item.daysBetween} Days</td>
                    <td style="padding: 10px; border: 1px solid #ccc">
                        <span style="color: #6E6E6E;font-weight: normal;"><fmt:formatDate pattern="MMMM dd, yyyy" value="${item.live}"/></span>
                    </td>
                    <td style="padding: 10px; border: 1px solid #ccc">${item.distributionPercent}&nbsp;%</td>
                    <td style="padding: 10px; border: 1px solid #ccc">${item.campaignStatus.description}</td>
                    <td style="padding: 10px; border: 1px solid #ccc">
                        <span style="color: #6E6E6E;font-weight: normal;"><fmt:formatDate pattern="MMMM dd, yyyy" value="${item.updated}"/></span>
                    </td>
                </tr>
                </c:forEach>
            </table>
            </c:when>
            <c:otherwise>
            There are no new campaigns to approve.
            </c:otherwise>
            </c:choose>
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
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</body>
</html>