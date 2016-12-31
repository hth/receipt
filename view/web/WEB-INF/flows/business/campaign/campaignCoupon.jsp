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
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel='stylesheet' href='${pageContext.request.contextPath}/static/external/css/fineuploader/fine-uploader.css'/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/colpick.css"/>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
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
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
        <sec:authorize access="hasRole('ROLE_BUSINESS')">
        <div class="business_reg">
            <div class="down_form" style="width: 90%">
                <form:form modelAttribute="couponCampaign" enctype="multipart/form-data">
                <h1 class="h1">Continue Creating New Coupon Campaign</h1>
                <hr>
                <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
                <input type="hidden" id="campaignId" value="${couponCampaign.campaignId}"/>
                <input type="hidden" id="bizId" value="${couponCampaign.bizId}"/>

                <spring:hasBindErrors name="couponCampaign">
                <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                    <ul>
                        <c:if test="${errors.hasFieldErrors('distributionPercentPatrons')}">
                            <li><form:errors path="distributionPercentPatrons" /></li>
                        </c:if>
                        <c:if test="${errors.hasFieldErrors('distributionPercentNonPatrons')}">
                            <li><form:errors path="distributionPercentNonPatrons" /></li>
                        </c:if>
                    </ul>
                </div>
                </spring:hasBindErrors>
                <div class="row_field" style="padding-bottom: 15px;">
                    <h1 class="widget-title-text">Upload Coupon</h1>
                </div>
                <div id="fine-uploader-validation" class="upload-text"></div>
                <div class="row_field">
                    <span id="couponImageSize" class="si-general-text remaining-characters"><sup>*</sup> Image max width 600px</span>
                </div>

                <div class="row_field">
                    <form:label path="distributionPercentPatrons" cssClass="profile_label" cssErrorClass="profile_label lb_error" cssStyle="width: 310px;">
                        % Of Patrons Receiving Coupons
                    </form:label>
                    <form:input path="distributionPercentPatrons" size="20" cssClass="name_txt" cssStyle="border: 0; width: 90px;" readonly="true"/> <span id="campaignDeliveryPatronId"></span>
                </div>
                <div class="row_field">
                    <div id="slider-vertical-patrons" style="height:15px; width: 325px;"></div>
                </div>
                <div class="row_field">
                    <form:label path="distributionPercentNonPatrons" cssClass="profile_label" cssErrorClass="profile_label lb_error" cssStyle="width: 310px;">
                        % Of Non Patrons Receiving Coupons
                    </form:label>
                    <form:input path="distributionPercentNonPatrons" size="20" cssClass="name_txt" cssStyle="border: 0;  width: 90px;" readonly="true"/> <span id="campaignDeliveryNonPatronId"></span>
                </div>
                <div class="row_field">
                    <div id="slider-vertical-non-patrons" style="height:15px; width: 325px;"></div>
                </div>

                <div class="full">
                    <input type="submit" value="NEXT" class="read_btn" name="_eventId_submit" style="background: #2c97de; margin: 77px 10px 0 0;">
                    <input type="submit" value="CANCEL" class="read_btn" name="_eventId_cancel" style="background: #FC462A; margin: 77px 10px 0 0;">
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
        <p class="footer_copy">&#169; 2017 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script type="text/template" id="qq-template">
    <div class="qq-uploader-selector qq-uploader" qq-drop-area-text="Drop files here">
        <div class="qq-total-progress-bar-container-selector qq-total-progress-bar-container">
            <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-total-progress-bar-selector qq-progress-bar qq-total-progress-bar"></div>
        </div>
        <div class="qq-upload-drop-area-selector qq-upload-drop-area" qq-hide-dropzone>
            <span class="qq-upload-drop-area-text-selector"></span>
        </div>
        <div class="qq-upload-button-selector qq-upload-button">
            <div>&uarr; &nbsp; UPLOAD COUPON</div>
        </div>
            <span class="qq-drop-processing-selector qq-drop-processing">
                <span>Processing dropped files...</span>
                <span class="qq-drop-processing-spinner-selector qq-drop-processing-spinner"></span>
            </span>
        <ul class="qq-upload-list-selector qq-upload-list" aria-live="polite" aria-relevant="additions removals">
            <li>
                <div class="qq-progress-bar-container-selector">
                    <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-progress-bar-selector qq-progress-bar"></div>
                </div>
                <span class="qq-upload-spinner-selector qq-upload-spinner"></span>
                <img class="qq-thumbnail-selector" qq-max-size="100" qq-server-scale>
                <span class="qq-upload-file-selector qq-upload-file"></span>
                <span class="qq-upload-size-selector qq-upload-size"></span>
                <button class="qq-btn qq-upload-cancel-selector qq-upload-cancel">Cancel</button>
                <button class="qq-btn qq-upload-retry-selector qq-upload-retry">Retry</button>
                <button class="qq-btn qq-upload-delete-selector qq-upload-delete">Delete</button>
                <span role="status" class="qq-upload-status-text-selector qq-upload-status-text"></span>
            </li>
        </ul>

        <dialog class="qq-alert-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <div class="qq-dialog-buttons">
                <button class="qq-cancel-button-selector">Close</button>
            </div>
        </dialog>

        <dialog class="qq-confirm-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <div class="qq-dialog-buttons">
                <button class="qq-cancel-button-selector">No</button>
                <button class="qq-ok-button-selector">Yes</button>
            </div>
        </dialog>

        <dialog class="qq-prompt-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <input type="text">
            <div class="qq-dialog-buttons">
                <button class="qq-cancel-button-selector">Cancel</button>
                <button class="qq-ok-button-selector">Ok</button>
            </div>
        </dialog>
    </div>
</script>
<script>
    $(function () {
        $('#slider-vertical-patrons').slider({
            orientation: "horizontal",
            range: "min",
            min: 0,
            max: 100,
            value: ${couponCampaign.distributionPercentPatronsAsInt},
            slide: function (event, ui) {
                $("#distributionPercentPatrons").val(ui.value + "%");
                computeDeliveryCount(
                        '#campaignDeliveryPatronId',
                        "/business/api/pc/${couponCampaign.bizId}/" + ui.value + ".htm");
            }
        });
        $('#distributionPercentPatrons').val($('#slider-vertical-patrons').slider("value") + "%");

        $('#slider-vertical-non-patrons').slider({
            orientation: "horizontal",
            range: "min",
            min: 0,
            max: 100,
            value: ${couponCampaign.distributionPercentNonPatronsAsInt},
            slide: function (event, ui) {
                $("#distributionPercentNonPatrons").val(ui.value + "%");
                computeDeliveryCount(
                        '#campaignDeliveryNonPatronId',
                        "/business/api/npc/${couponCampaign.bizId}/" + ui.value + ".htm");
            }
        });
        $('#distributionPercentNonPatrons').val($('#slider-vertical-non-patrons').slider("value") + "%");
    });
</script>
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<script src="${pageContext.request.contextPath}/static/js/couponUpload.js"></script>
</html>