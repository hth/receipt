<%@ include file="../include.jsp"%>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.admin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
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
    <sec:authorize access="hasRole('ROLE_BUSINESS')">
    <div>
        <div class="down_form" style="width: 90%;">
            <h1 class="h1"><c:out value="${businessLandingForm.bizName}" /></h1>
            <hr>
            Registered customers: <c:out value="${businessLandingForm.customerCount}" />
            <br/>
            Total customer purchases: <c:out value="${businessLandingForm.totalCustomerPurchases}" />
            <br/>
            Store count: <c:out value="${businessLandingForm.storeCount}" />
            Visit count: <c:out value="${businessLandingForm.visitCount}" />
            <br/>
            <a href="/business/campaign.htm">Start New Campaign</a>
        </div>

        <div class="rightside-list-holder full-list-holder"
                style="overflow-y: hidden; height: 700px; margin-left: 0; padding-left: 0;">
            <div class="down_form" style="width: 96%;">
                <c:choose>
                <c:when test="${!empty businessLandingForm.campaignListForm.campaigns}">
                <table width="100%" style="margin: 0 4px 0 4px">
                    <thead>
                    <tr>
                        <th></th>
                        <th width="300px;">Text</th>
                        <th>Campaign Live Date</th>
                        <th width="300px;">Distribution</th>
                    </tr>
                    </thead>
                    <c:forEach items="${businessLandingForm.campaignListForm.campaigns}" var="item" varStatus="status">
                    <tr>
                        <td style="padding: 10px; border: 1px solid #ccc" rowspan="3">${status.count}&nbsp;</td>
                        <td style="padding: 10px; border: 1px solid #ccc;">
                            <a href="/business/campaign.htm?campaignId=${item.id}">${item.freeText}</a>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ccc;">
                            <fmt:formatDate pattern="MMMM dd, yyyy" value="${item.live}"/>
                            <span style="color: #6E6E6E;font-weight: normal;">&nbsp;<fmt:formatDate value="${item.live}" type="time"/></span>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ccc;">
                            <c:if test="${!empty item.campaignStats.get('P')}">
                            Patrons: ${item.campaignStats.get("P").distributionPercent}&nbsp;%
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 10px; border: 1px solid #ccc" colspan="2">
                            <span style="float:left;">State: ${item.campaignStatus.description}</span>
                            <span style="float:right;">Duration: ${item.daysBetween} Days</span>
                        </td>
                        <td style="padding: 10px; border: 1px solid #ccc;">
                            <c:if test="${!empty item.campaignStats.get('NP')}">
                            Non Patrons: ${item.campaignStats.get("NP").distributionPercent}&nbsp;%
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 10px; border: 1px solid #ccc" colspan="3">
                            <c:if test="${item.campaignStatus eq 'D'}">
                                <span style="float:left;">Reason: ${item.reason}</span>
                            </c:if>
                            <span style="float:right;">Last Modified: <fmt:formatDate pattern="MMMM dd, yyyy" value="${item.updated}"/></span>
                        </td>
                    </tr>
                    </c:forEach>
                </table>
                </c:when>
                <c:otherwise>
                    You have no new campaign set to connect with customer.
                </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    </sec:authorize>
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