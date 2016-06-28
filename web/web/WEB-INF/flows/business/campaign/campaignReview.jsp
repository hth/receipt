<%@ include file="../../../jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.0.0/jquery-migrate.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
</head>
<body>
<spring:eval expression="pageContext.request.userPrincipal.principal.userLevel eq T(com.receiptofi.domain.types.UserLevelEnum).BUSINESS_SMALL" var="hasAccess" />
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
                <c:if test="${hasAccess}">
                    <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                    <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
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
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
    <sec:authorize access="hasRole('ROLE_BUSINESS')">
        <div class="business_reg">
            <div class="down_form" style="width: 90%">
                <form:form commandName="couponCampaign">
                    <h1 class="h1">Submit New Coupon Campaign</h1>
                    <hr>
                    <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                    <input type="hidden" id="campaignId" value="${couponCampaign.campaignId}"/>
                    <input type="hidden" id="bizId" value="${couponCampaign.bizId}"/>

                    <div class="row_field">
                        <form:label path="freeText" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Coupon Text</form:label>
                        <form:input path="freeText" size="200" cssClass="name_txt" cssStyle="width: 250px;" readonly="true"/>
                    </div>
                    <div class="row_field">
                        <form:label path="additionalInfo" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Additional Info</form:label>
                        <form:textarea path="additionalInfo" cols="50" rows="5" cssClass="name_txt" cssStyle="width: 450px; height: 150px;" readonly="true"/>
                    </div>
                    <div class="row_field">
                        <form:label path="start" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Valid From</form:label>
                        <form:input path="start" size="20" cssClass="name_txt" cssStyle="width: 100px;" readonly="true" />
                        &nbsp;
                        <span style="padding: 10px 8px 8px 0; font-weight: bold">To</span>
                        &nbsp;
                        <form:input path="end" size="20" cssClass="name_txt" cssStyle="width: 100px;" readonly="true" />
                        <c:if test="${couponCampaign.daysBetween gt -1}">
                            <span style="padding: 10px 8px 8px 0; font-weight: bold">&nbsp; Duration <c:out value="${couponCampaign.daysBetween}" /> Days</span>
                        </c:if>
                    </div>
                    <div class="row_field">
                        <form:label path="live" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">First Available</form:label>
                        <form:input path="live" size="20" cssClass="name_txt" cssStyle="width: 100px;" readonly="true" />
                    </div>
                    <div id="fine-uploader-validation" class="upload-text"></div>
                    <div class="row_field">
                        <form:label path="distributionPercent" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error" cssStyle="width: 300px;">% Of Customers Receiving Coupons:</form:label>
                        <form:input path="distributionPercent" size="20" cssClass="name_txt" cssStyle="border: 0;" readonly="true"/>
                    </div>
                    <div id="container"></div>
                    <div class="full">
                    <c:if test="${couponCampaign.businessCampaignStatus ne 'N'}">
                    <div class="row_field">
                        <form:label path="businessCampaignStatus" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Current State</form:label>
                        <form:input path="businessCampaignStatus.description" size="20" cssClass="name_txt" cssStyle="width: 200px; border: 0;" readonly="true" />
                    </div>
                    </c:if>
                    <c:choose>
                    <c:when test="${couponCampaign.businessCampaignStatus ne 'L'}">
                        <input type="submit" value="CONFIRM" class="read_btn" name="_eventId_confirm"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        <input type="submit" value="REVISE" class="read_btn" name="_eventId_revise"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                        <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel"
                                style="background: #FC462A; margin: 77px 10px 0 0;">
                    </c:when>
                    <c:otherwise>
                        <input type="submit" value="HOME" class="read_btn" name="_eventId_cancel"
                                style="background: #2c97de; margin: 77px 10px 0 0;">
                    </c:otherwise>
                    </c:choose>
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
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<!-- Loads image -->
<script>
    function measurement(position) {
        if (position instanceof String) {
            if (position.indexOf("%") != -1) {
                return position;
            }
        }
        return position + "px";
    }
    function rotate(el, d) {
        var s = "rotate(" + d + "deg)";
        if (el.style) { // regular DOM Object
            el.style.MozTransform = s;
            el.style.WebkitTransform = s;
            el.style.OTransform = s;
            el.style.transform = s;
        } else if (el.css) { // JQuery Object
            el.css("-moz-transform", s);
            el.css("-webkit-transform", s);
            el.css("-o-transform", s);
            el.css("transform", s);
        }
        el.setAttribute("rotation", d);
    }
    function calculateTop(imageHeight) {
        if (topHeight == 0 ) {
            return topHeight + 5;
        }
        return topHeight + imageHeight + 5;
    }

    // JSON data
    var topHeight = 0,
            info = [
                <c:forEach items="${couponCampaign.fileSystemEntities}" var="arr" varStatus="status">
                    <c:choose>
                    <c:when test="${couponCampaign.businessCampaignStatus eq 'L'}">
                    {
                        src: "https://s3-us-west-2.amazonaws.com/<spring:eval expression="@environmentProperty.getProperty('aws.s3.bucketName')" />/<spring:eval expression="@environmentProperty.getProperty('aws.s3.couponBucketName')" />/${arr.key}",
                        pos: {
                            top: topHeight = calculateTop(${arr.height}),
                            left: 0
                        },
                        rotate: ${arr.imageOrientation},
                        zIndex: 0
                    },
                    </c:when>
                    <c:otherwise>
                    {
                        src: '${pageContext.request.contextPath}/access/filedownload/receiptimage/${arr.blobId}.htm',
                        pos: {
                            top: topHeight = calculateTop(${arr.height}),
                            left: 0
                        },
                        rotate: ${arr.imageOrientation},
                        zIndex: 0
                    },
                    </c:otherwise>
                    </c:choose>
                </c:forEach>
            ]
            ;

    var df = document.createDocumentFragment();
    for (var i = 0, j = info.length; i < j; i++) {
        var el = document.createElement("img");
        el.src = info[i].src;
        el.className = "img";
        el.style.left = measurement(info[i].pos.left);
        el.style.top = measurement(info[i].pos.top);
        el.style.zIndex = info[i].zIndex;
        rotate(el, info[i].rotate);
        df.appendChild(el);
    }
    document.getElementById("container").appendChild(df);
</script>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>