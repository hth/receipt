<%@ include file="include.jsp"%>
<!DOCTYPE html>
<html lang="en" ng-app="scroll" ng-controller="Main">
<head>
	<meta charset="utf-8"/>
	<meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/fineuploader/fine-uploader.css'/>
    <link rel='stylesheet' type='text/css' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.css'/>
    <link rel='stylesheet' type='text/css' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.print.css' media='print'/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/highcharts.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/randomcolor/0.3.0/randomColor.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>

    <script>
        function init() {
            window.addEventListener('scroll', function(e){
                var distanceY = window.pageYOffset || document.documentElement.scrollTop,
                        shrinkOn = 300,
                        header = document.querySelector("header");
                if (distanceY > shrinkOn) {
                    classie.add(header,"smaller");
                } else {
                    if (classie.has(header,"smaller")) {
                        classie.remove(header,"smaller");
                    }
                }
            });
        }
        window.onload = init();
    </script>
    <script>
        function Main($scope, $http) {
            $scope.items = [];

            var page = 5;
            $scope.loadMore = function() {
                if (page < '${notificationForm.count}') {
                    console.log("loading onwards: " + page + ", " + "total count: " + '${notificationForm.count}');
                    $scope.loading = true;
                    $http.get('${pageContext. request. contextPath}/access/notificationPaginated/' + page + '.htm')
                            .success(function(data, status) {
                                if(data.length <= 5) {
                                    console.log('Request status ' + status + ":" + data.length + ":" + data + ", Page " + page);
                                    for (var i = 0; i < 5 && i < data.length; i++) {
                                        var d = data[i].split(":");
                                        console.log("Adding: " + d[0] + ":" + d[1] + ":" + d[2]);
                                        $scope.items.push({href : d[0], message : d[1], created : d[2]});
                                    }
                                } else {
                                    $scope.failed = true;
                                }
                                $scope.loading = false;
                            }).error(function(data, status) {
                                console.log('Request error, data:' + data + ",status:");
                                $scope.loading = false;
                                $scope.failed = true;
                            });
                    page += 5;
                    console.log($scope.items);
                }
            };
            $scope.loadMore();
        }

        angular.module('scroll', []).directive('whenScrolled', function() {
            return function(scope, elm, attr) {
                var raw = elm[0];

                elm.bind('scroll', function() {
                    if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                        scope.$apply(attr.whenScrolled);
                    }
                });
            };
        });
    </script>
    <script type='text/javascript'>
        $(document).ready(function() {
            "use strict";

            $('#calendar').fullCalendar({
                header : {
                    left : 'prev,next today',
                    center : '',
                    right: ''
                },
                defaultView: 'month',
                contentHeight: 500, //Adds another 50 in surrounding area hence 500 height
                aspectRatio: 1,
                editable : false,
                eventLimit: true,
                events : [
                    <c:set var="receiptGroupedIterator" value="${landingForm.receiptGrouped}" />
                    <c:forEach var="receiptGrouped" items="${receiptGroupedIterator}">
                    {
                        title : '<fmt:formatNumber value="${receiptGrouped.stringTotal}" type="currency" />',
                        start : '${receiptGrouped.dateForFullCalendar}',
                        end   : '${receiptGrouped.dateForFullCalendar}',
                        url   : '${pageContext.request.contextPath}/access/day.htm?date=${receiptGrouped.date.time}',
                        allDay: true
                    },
                    </c:forEach>
                ]
            });

            calendarActions();
        });
    </script>
</head>
<body>
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
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
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
<div class="sidebar">
    <div class="sidebar-top-summary">
        <div class="sidebar-top-summary-upper clearfix">
            <h1 id="pendingCountInitial">
                <a href='${pageContext. request. contextPath}/access/document/pending.htm' class="big-view">
                    ${documentStatsForm.pendingCount}
                </a>
            </h1>
            <h1 id="pendingCountId"></h1>

            <div class="sts-upper-right">
                <span class="top-summary-textb">
                <c:choose>
                    <c:when test="${documentStatsForm.pendingCount le 1}">Receipt pending</c:when>
                    <c:otherwise>Receipts pending</c:otherwise>
                </c:choose>
                </span>
				<span class="general-text">
                    Last sync: <span class="timestamp" id="pendingCountSyncedId"></span>
                </span>
            </div>
        </div>
        <div class="sidebar-top-summary-lower clearfix">
            <h1 id="rejectedCountInitial">
                <a href='${pageContext. request. contextPath}/access/document/rejected.htm' class="big-view-lower">
                    ${documentStatsForm.rejectedCount}
                </a>
            </h1>
            <h1 id="rejectedCountId"></h1>

            <div class="sts-upper-right">
				<span class="top-summary-textb">
                    <c:choose>
                        <c:when test="${documentStatsForm.rejectedCount le 1}">Receipt rejected</c:when>
                        <c:otherwise>Receipts rejected</c:otherwise>
                    </c:choose>
                </span>
				<span class="general-text">
                    Last sync: <span class="timestamp" id="rejectedCountSyncedId"></span>
                </span>
            </div>
        </div>
    </div>
	<div class="sidebar-git-datum">
		<div class="gd-title">
			<h1 class="widget-title-text">Upload new receipt</h1>
		</div>
        <div id="fine-uploader-validation" class="upload-text"></div>
	</div>
	<div class="sidebar-indication">
		<div class="si-title">
			<h1 class="widget-title-text">Notifications (${notificationForm.count})</h1>
		</div>
		<div class="si-list-holder" when-scrolled="loadMore()">
            <c:choose>
            <c:when test="${!empty notificationForm.notifications}">
                <ul>
                    <c:forEach var="notification" items="${notificationForm.notifications}" varStatus="status">
                    <li class="si-list">
                        <img class="si-notification-icon" alt="Notification icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
                        <span class="si-general-text">${notification.notificationMessageForDisplay}</span>
                        <span class="si-date-text"><fmt:formatDate value="${notification.created}" pattern="MMM. dd" /></span>
                    </li>
                    </c:forEach>
                    <li class="si-list" ng-repeat="i in items">
                        <img class="si-notification-icon" alt="Notification icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
                        <span class="si-general-text"><a class='rightside-li-middle-text full-li-middle-text' href="{{i.href}}">{{i.message}}</a></span>
                        <span class="si-date-text">{{i.created}}</span>
                    </li>
                </ul>
                <p class="si-list-footer si-list-footer-success" ng-show="loading">
                    <%--<img src="${pageContext.request.contextPath}/static/img/notification-loading.gif"/>--%>
                    <em>Loading ...</em>
                </p>
                <p class="si-list-footer si-list-footer-error" ng-show="failed">
                    <em>Failed to retrieve data</em>
                </p>
            </c:when>
            <c:otherwise>
                <p class="si-general-text">There are no Notifications &nbsp;</p>
            </c:otherwise>
            </c:choose>
		</div>
		<div class="si-footer">
            <c:if test="${!empty notificationForm.notifications}">
                <p class="view-more-text">
                    <a class="view-more-text" ng-href="${pageContext.request.contextPath}/access/notification.htm">View All Notifications</a>
                </p>
            </c:if>
		</div>
	</div>
	<div class="sidebar-invite">
		<div class="gd-title">
			<h1 class="widget-title-text">Friend Invite</h1>
		</div>
        <div id="inviteTextMessage"></div>
		<form>
            <input type="text" placeholder="Email address of friend here ..." size="20"
                    onfocus="changeInviteText(this, 'focus')"
                    onblur="changeInviteText(this, 'blur')"
                    id="inviteEmailId"/>
		</form>
		<div class="gd-button-holder">
			<button class="gd-button" style="background: #808080;" onclick="submitInvitationForm()" id="sendInvite_bt" disabled="disabled">SEND INVITE</button>
		</div>
        <div id="inviteText" class="si-general-text invite-general-text">Invitation is sent with your name and email address</div>
	</div>
</div>

<spring:eval expression="pageContext.request.userPrincipal.principal.userLevel ge T(com.receiptofi.domain.types.UserLevelEnum).USER_COMMUNITY" var="isValidForMap" />
<div id="off_screen">
    <div id="map-canvas"></div>
</div>

<div class="rightside-content">
	<div id="tabs" class="nav-list">
		<ul class="nav-block">
			<li><a href="#tab1">OVERVIEW</a></li>
            <c:if test="${isValidForMap}">
			<li><a href="#tab2">MAP</a></li>
            </c:if>
		</ul>
		<div id="tab1" class="ajx-content">
			<div class="rightside-title">
				<h1 class="rightside-title-text left" id="monthShownId"><fmt:formatDate value="${landingForm.receiptForMonth.monthYearDateTime}" pattern="MMMM, yyyy" /></h1>
                <span class="right right_view" style="width: 24%;">
					<input type="button" value="List" class="overview_view toggle_button_left toggle_selected" id="btnList" onclick="toggleListCalendarView(this)">
					<span style="width:1px;background:white;float:left;">&nbsp;</span>
					<input type="button" value="Calendar" class="overview_view toggle_button_right" id="btnCalendar" onclick="toggleListCalendarView(this)">
				</span>
			</div>

            <div id="onLoadReceiptForMonthId">
                <c:choose>
                <c:when test="${!empty landingForm.receiptForMonth.receipts}">
                <div class="rightside-list-holder mouseScroll" id="receiptListId">
                    <ul>
                        <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
                        <li>
                            <c:choose>
                                <c:when test="${!empty receipt.expenseReportInFS or receipt.splitCount gt 1}">
                                    <span class="rightside-li-date-text rightside-li-date-text-short"><fmt:formatDate value="${receipt.date}" pattern="MMM. dd"/></span>
                                    <c:choose>
                                        <c:when test="${!empty receipt.expenseReportInFS and receipt.splitCount gt 1}">
                                            <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                                <a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receipt.id}.htm' style="margin-top: -2px;">
                                                    <img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png'
                                                            width='15' height='16' title='Download Expense Report' class='downloadIcon'>
                                                </a>
                                                <c:choose>
                                                <c:when test="${receipt.ownReceipt}">
                                                    <span class="member" style="background-color: #00529B; width: 25px; height: 20px;">
                                                        <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="member" style="background-color: #484848; width: 25px; height: 20px;">
                                                        <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                                    </span>
                                                </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </c:when>
                                        <c:when test="${!empty receipt.expenseReportInFS}">
                                            <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                                <a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receipt.id}.htm' style="margin-top: -2px;">
                                                    <img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png'
                                                            width='15' height='16' title='Download Expense Report' class='downloadIcon'>
                                                </a>
                                            </p>
                                        </c:when>
                                        <c:when test="${!empty receipt.splitCount}">
                                            <p class="rightside-li-date-text rightside-li-date-text-show-attr" align="center">
                                                <c:choose>
                                                <c:when test="${receipt.ownReceipt}">
                                                    <span class="member" style="background-color: #00529B; width: 25px; height: 20px;">
                                                        <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="member" style="background-color: #606060; width: 25px; height: 20px;">
                                                        <span class="member-initials" style="line-height: 20px;">+${receipt.splitCount - 1}</span>
                                                    </span>
                                                </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </c:when>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <span class="rightside-li-date-text"><fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/></span>
                                </c:otherwise>
                            </c:choose>
                            <span style="background-color: ${receipt.expenseColor};">&nbsp;&nbsp;</span>
                            <c:choose>
                            <c:when test="${receipt.billedStatus eq 'NB'}">
                                <a href="/access/userprofilepreference/i.htm#tabs-3"
                                        class="rightside-li-middle-text">
                                    <spring:eval expression="receipt.name"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm"
                                        class="rightside-li-middle-text" target="_blank">
                                    <spring:eval expression="receipt.name"/>
                                </a>
                            </c:otherwise>
                            </c:choose>
                            <span class="rightside-li-right-text"><spring:eval expression='receipt.splitTotal'/></span>
                        </li>
                        </c:forEach>
                    </ul>
                </div>
                </c:when>
                <c:otherwise>
                <div class="r-info" id="noReceiptId">
                    No receipt data available for this month.
                </div>
                </c:otherwise>
                </c:choose>
            </div>

            <div id="refreshReceiptForMonthId"></div>

            <div class="calendar" id="calendarId">
                <div id="calendar"></div>
            </div>
            <div class="pie-chart">
                <div id="expenseByBusiness"></div>
			</div>
		</div>

        <c:if test="${isValidForMap}">
        <div id="tab2" class="ajx-content">
            <div class="rightside-title temp_offset" id="title_MapDataId">
                <h1 class="rightside-title-text left">
                    Expenses by business location
                </h1>
            </div>

            <c:choose>
            <c:when test="${!empty landingForm.receiptGroupedByBizLocations}">
            <div class="rightside-list-holder">
                <div id="map-placeholder"></div>
            </div>
            </c:when>
            <c:otherwise>
                <div class="r-info temp_offset" id="noMapDataId">
                    No receipt available to map with location.
                </div>
            </c:otherwise>
            </c:choose>
		</div>
        </c:if>
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
<c:if test="${!empty landingForm.bizByExpenseTypes}">
<!-- Biz by expense -->
<script>
$(document).ready(function() {
    drawExpenseByBusiness();
});
</script>
</c:if>

<script>
var observeDOM = (function () {
    var MutationObserver = window.MutationObserver || window.WebKitMutationObserver,
            eventListenerSupported = window.addEventListener;

    return function (obj, callback) {
        if (MutationObserver) {
            // define a new observer
            var obs = new MutationObserver(function (mutations, observer) {
                if (mutations[0].addedNodes.length || mutations[0].removedNodes.length)
                    callback();
            });
            // have the observer observe foo for changes in children
            obs.observe(obj, {childList: true, subtree: true});
        }
        else if (eventListenerSupported) {
            obj.addEventListener('DOMNodeInserted', callback, false);
            obj.addEventListener('DOMNodeRemoved', callback, false);
        }
    }
});

// Observe a specific DOM element:
observeDOM(document.getElementById('refreshReceiptForMonthId'), function () {
    drawExpenseByBusiness();
});

function drawExpenseByBusiness() {
    $(function () {
        "use strict";

        var colors = randomColor({hue: 'blue', luminosity: 'bright', count: ${landingForm.bizByExpenseTypes.size()}});
        var categories = [${landingForm.bizNames}];
        var data = [
            <c:forEach var="item" items="${landingForm.bizByExpenseTypes}" varStatus="status">
            {
                y: ${item.total},
                color: colors[${status.count-1}],
                url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}/${landingForm.receiptForMonth.monthYear}.htm',
                id: '${item.bizNameForId}',
                drilldown: {
                    name: '${item.bizName}',
                    categories: [${item.expenseTags}],
                    data: [${item.expenseValues}],
                    color: colors[${status.count-1}],
                    url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}/${landingForm.receiptForMonth.monthYear}.htm',
                    id: '${item.bizNameForId}'
                }
            },
            </c:forEach>
        ];

        // Build the data arrays
        var bizNames = [];
        var expenseTags = [];
        populateExpenseByBusiness(data, bizNames, categories, expenseTags);
        loadMonthlyExpensesByBusiness('${landingForm.receiptForMonth.monthYear}', bizNames, expenseTags);
    });
}
</script>

<c:if test="${!empty landingForm.receiptGroupedByBizLocations && isValidForMap}">
<!-- Google Map -->
<script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=<spring:eval expression="@environmentProperty.getProperty('google-browser-api-key')" />">
</script>
<script type="text/javascript">
    $(document).ready(function () {
        "use strict";

        /**
         * Data for the markers consisting of a businessName, a LatLng and a zIndex for
         * the order in which these markers should display on top of each
         * other.
         */
        var locations = [
            <c:forEach var="loc" items="${landingForm.receiptGroupedByBizLocations}" varStatus="status">
            <c:if test="${loc.bizStore.validatedUsingExternalAPI && !empty loc.bizStore.coordinate}">
            [
                '<div class="mapContainer">' +
                '<div><h3>${loc.bizName.safeJSBusinessName} : <b>${loc.totalStr}</b></h3></div>' +
                '<div>' +
                '<div>${loc.bizStore.safeJSAddress}</div>' +
                '</div>' +
                '</div>',
                ${loc.bizStore.lat}, ${loc.bizStore.lng}, ${status.count}
            ],
            </c:if>
            </c:forEach>
        ];

        getGoogleMap(locations);
    });
</script>
</c:if>

<script type="text/javascript">
    $(document).ready(function () {
        "use strict";

        $("#calendarId").hide();
        <c:if test="${empty landingForm.receiptForMonth.receipts}">
        $("#btnList").addClass("toggle_selected");
        $("#btnCalendar").removeClass("toggle_selected");
        </c:if>

        $("#noReceiptId").removeClass("temp_offset");
        $("#noMapDataId").removeClass("temp_offset");
        $("#title_MapDataId").removeClass("temp_offset");
    });
</script>

<script type="text/template" id="qq-template">
    <div class="qq-uploader-selector qq-uploader" qq-drop-area-text="Drop files here">
        <div class="qq-total-progress-bar-container-selector qq-total-progress-bar-container">
            <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-total-progress-bar-selector qq-progress-bar qq-total-progress-bar"></div>
        </div>
        <div class="qq-upload-drop-area-selector qq-upload-drop-area" qq-hide-dropzone>
            <span class="qq-upload-drop-area-text-selector"></span>
        </div>
        <div class="qq-upload-button-selector qq-upload-button">
            <div>&uarr; &nbsp; UPLOAD IMAGE(S)</div>
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
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<script src="${pageContext.request.contextPath}/static/js/fineupload.js"></script>
</body>
</html>
