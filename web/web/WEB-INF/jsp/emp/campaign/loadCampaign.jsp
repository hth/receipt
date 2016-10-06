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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
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
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/emp/campaign/landing.htm">Receiptofi</a></h1>
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
        <div class="business_reg">
            <div class="down_form" style="width: 90%">
                <form:form commandName="couponCampaign">
                    <h1 class="h1">${couponCampaign.businessName}</h1>
                    <hr>

                    <spring:hasBindErrors name="couponCampaign">
                    <div class="r-validation" style="width: 98%; margin: 0 0 0 0;">
                        <c:if test="${errors.hasFieldErrors('reason')}">
                            <form:errors path="reason" /><br>
                        </c:if>
                    </div>
                    </spring:hasBindErrors>

                    <div class="row_field">
                        <form:label path="freeText" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error">Campaign Text</form:label>
                        <form:input path="freeText" size="200" cssClass="name_txt" cssStyle="width: 285px;" readonly="true"/>
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

                    <div class="row_field">
                        <span class="profile_label">Image</span>
                        <div id="container"></div>
                    </div>

                    <div class="row_field">
                        <form:label path="distributionPercentPatrons" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error" cssStyle="width: 280px;">Patrons Receiving Campaign</form:label>
                        <form:input path="distributionPercentPatrons" size="20" cssClass="name_txt" cssStyle="border: 0;" readonly="true"/>
                    </div>
                    <div class="row_field">
                        <form:label path="distributionPercentNonPatrons" cssClass="profile_label"
                                cssErrorClass="profile_label lb_error" cssStyle="width: 280px;">Non Patrons Receiving Campaign</form:label>
                        <form:input path="distributionPercentNonPatrons" size="20" cssClass="name_txt" cssStyle="border: 0;" readonly="true"/>
                    </div>
                    <div class="full">
                        <c:if test="${couponCampaign.campaignStatus ne 'N'}">
                        <div class="row_field">
                            <form:label path="campaignStatus" cssClass="profile_label"
                                    cssErrorClass="profile_label lb_error">State</form:label>
                            <span class="name_txt" style="width: 200px; border: 0;">${couponCampaign.campaignStatus.description}</span>
                        </div>
                        <div class="row_field">
                            <form:label path="reason" cssClass="profile_label"
                                    cssErrorClass="profile_label lb_error">Decline Reason</form:label>
                            <form:input path="reason" size="200" cssClass="name_txt" cssStyle="width: 285px;"/>
                        </div>
                        </c:if>
                        <c:choose>
                            <c:when test="${couponCampaign.campaignStatus eq 'P'}">
                                <input type="submit" value="APPROVE" class="read_btn" name="campaign-approve"
                                        style="background: #2c97de; margin: 77px 10px 0 0;">
                                <input type="submit" value="DECLINE" class="read_btn" name="campaign-decline"
                                        style="background: #FC462A; margin: 77px 10px 0 0;">
                            </c:when>
                            <c:otherwise>
                                <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel"
                                        style="background: #2c97de; margin: 77px 10px 0 0;">
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form:form>
            </div>
        </div>
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
                <c:when test="${couponCampaign.campaignStatus eq 'A'}">
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
        el.style.border = "1px dotted";
        rotate(el, info[i].rotate);
        df.appendChild(el);
    }
    document.getElementById("container").appendChild(df);
</script>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</html>