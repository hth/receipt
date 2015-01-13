<%@ include file="/WEB-INF/jsp/include.jsp"%>
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
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/fineuploader/fineuploader-3.6.3.css'/>
    <link rel='stylesheet' type='text/css' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.2.5/fullcalendar.min.css'/>
    <link rel='stylesheet' type='text/css' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.2.5/fullcalendar.print.css' media='print'/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script async src="${pageContext.request.contextPath}/static/js/receiptofi.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/fineuploader/jquery.fineuploader-3.6.3.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.0.4/highcharts.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.2.5/fullcalendar.min.js"></script>

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
                console.log("page:" + page + "," + "notificationCount:" + '${landingForm.notificationForm.count}');
                if(page < '${landingForm.notificationForm.count}' - 1) {
                    $scope.loading = true;
                    $http.get('${pageContext. request. contextPath}/access/notificationPaginated/' + page + '.htm')
                            .success(function(data, status) {
                                if(data.length <= 5) {
                                    console.log('Request status ' + status + ":" + data.length + ":" + data);
                                    for (var i = 0; i < 5 && page + i < '${landingForm.notificationForm.count}'; i++) {
                                        var d = data[i].split(":");
                                        console.log(d[0] + ":" + d[1] + ":" + d[2]);
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
</head>
<body>
<header>
	<div class="top-account-bar">
		<ul>
			<li><a class="top-account-bar-text" href="#">LOG OUT</a></li>
			<li><a class="top-account-bar-text" href="#">PROFILE</a></li>
            <li>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
            </li>
		</ul>
	</div>
	<div class="nav-hold">
		<h1>Receiptofi</h1>
	</div>
</header>
<div class="main clearfix">
<div class="sidebar">
	<div class="sidebar-top-summary">
		<div class="sidebar-top-summary-upper clearfix">
			<h1 class="big-view" id="pendingCountInitial">
                <a href='${pageContext. request. contextPath}/access/pendingdocument.htm'>
                    ${documentStatsForm.pendingCount}
                </a>
            </h1>
			<h1 class="big-view" id="pendingCountId"></h1>

			<div class="sts-upper-right">
                <span class="top-summary-textb">
                <c:choose>
                    <c:when test="${documentStatsForm.pendingCount le 1}">Receipt pending</c:when>
                    <c:otherwise>Receipts pending</c:otherwise>
                </c:choose>
                </span>
				<span class="general-text">
                    Last sync:
                    <span class="timestamp" id="pendingCountSyncedId">
                        <fmt:formatDate value="${documentStatsForm.pendingCountSynced}" type="both"/>
                    </span>
                </span>
			</div>
		</div>
		<div class="sidebar-top-summary-lower clearfix">
			<h1 class="big-view-lower">${documentStatsForm.rejectedCount}</h1>

			<div class="sts-upper-right">
				<span class="top-summary-textb">
                    <c:choose>
                        <c:when test="${documentStatsForm.rejectedCount le 1}">Receipt rejected</c:when>
                        <c:otherwise>Receipts rejected</c:otherwise>
                    </c:choose>
                </span>
				<span class="general-text">
                    Last sync:
                    <span class="timestamp">
                        <fmt:formatDate value="${documentStatsForm.rejectedCountSynced}" type="both"/>
                    </span>
                </span>
			</div>
		</div>
	</div>
	<div class="sidebar-git-datum">
		<div class="gd-title">
			<h1 class="widget-title-text">Upload new receipt</h1>
		</div>
        <div id="restricted-fine-uploader"></div>
	</div>
	<div class="sidebar-indication">
		<div class="si-title">
			<h1 class="widget-title-text">Notifications (${landingForm.notificationForm.count})</h1>
		</div>
		<div class="si-list-holder" when-scrolled="loadMore()">
            <c:choose>
            <c:when test="${!empty landingForm.notificationForm.notifications}">
                <ul>
                    <c:forEach var="notification" items="${landingForm.notificationForm.notifications}" varStatus="status">
                        <li class="si-list">
                            <img class="si-notification-icon" alt="Notification icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
                            <span class="si-general-text">${notification.notificationMessageForDisplay}</span>
                            <span class="si-date-text"><fmt:formatDate value="${notification.created}" pattern="MMM. dd" /></span>
                        </li>
                    </c:forEach>
                    <li class="si-list" ng-repeat="i in items">
                        <img class="si-notification-icon" alt="Notification icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
                        <span class="si-general-text"><a class='notification' href="{{i.href}}">{{i.message}}</a></span>
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
            <c:if test="${!empty landingForm.notificationForm.notifications}">
                <p class="view-more-text">
                    <a class="view-more-text" ng-href="${pageContext.request.contextPath}/access/notification.htm">View All Notifications</a>
                </p>
            </c:if>
		</div>
	</div>
	<div class="sidebar-date">
		<div class="gd-title">
			<h1 class="widget-title-text">Friend Invite</h1>
		</div>
		<form>
            <input type="text" value="Email address of friend here ..." size="20"
                    onfocus="changeInviteText(this, 'focus')"
                    onblur="changeInviteText(this, 'blur')"
                    id="inviteEmailId"/>
		</form>
		<div class="gd-button-holder">
			<button class="gd-button" onclick="submitInvitationForm()">SEND INVITE</button>
		</div>
        <div id="inviteText" class="si-general-text invite-general-text">Invitation sent with your name and email address</div>
	</div>
</div>
<div class="rightside-content">
	<div id="tabs" class="nav-list">
		<ul class="nav-block">
			<li><a href="#tab1">OVERVIEW</a></li>

			<li><a href="#tab2">FIRST</a></li>
			<li><a href="#tab3">SECOND</a></li>
			<li><a href="#tab4">THIRD</a></li>
			<li><a href="#tab5">FOURTH</a></li>
		</ul>
		<div id="tab1" class="ajx-content">
			<div class="rightside-title">
				<h1 class="rightside-title-text left">
                    <fmt:formatDate value="${landingForm.receiptForMonth.monthYearDateTime}" pattern="MMMM, yyyy" />
                </h1>
                <span class="right" style="width: 23%;">
					<input type="button" style="margin: 0px;border-bottom-left-radius: 5px;border-top-left-radius: 5px;" value="List" id="btnlist" class="overview_view" onclick="showhide()">
					<span style="width:1px;background:white;float:left;">&nbsp;</span>
					<input type="button" style="margin:0px;border-bottom-right-radius: 5px;border-top-right-radius: 5px;" value="Calendar" class="overview_view" id="btndetail" onclick="showhide()">
				</span>
			</div>
			<div class="rightside-list-holder">
				<ul>
                    <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
                    <li class="rightside-list">
                        <span class="rightside-li-date-text">
                            <fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy"/>
                        </span>
                        <a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm" class="rightside-li-middle-text">
                            <spring:eval expression="receipt.name"/>
                        </a>
                        <span class="rightside-li-right-text">
                            <spring:eval expression='receipt.total'/>
                        </span>
                    </li>
                    </c:forEach>
				</ul>
				<p class="view-more-text">View All</p>
			</div>
            <div class="pie-chart">
                <div id="expenseByBusiness" style="min-width: 530px; height: 425px; margin: 0 auto"></div>
			</div>
			<div class="calendar">
                <script type='text/javascript'>
                    $(document).ready(function() {
                        "use strict";

                        $('#calendar').fullCalendar({
                            header : {
                                left : 'prev,next today',
                                center : '',
                                right: 'month,agendaWeek,agendaDay'
                            },
                            defaultView: 'month',
                            contentHeight: 550,
                            aspectRatio: 1,
                            editable : false,
                            eventLimit: true,
                            events : [
                                <c:set var="receiptGroupedIterator" value="${landingForm.receiptGrouped}" />
                                <c:forEach var="receiptGrouped" items="${receiptGroupedIterator}">
                                {
                                    title : '<fmt:formatNumber value="${receiptGrouped.stringTotal}" type="currency" />',
                                    start : '${receiptGrouped.date}',
                                    end   : '${receiptGrouped.date}',
                                    url   : '${pageContext.request.contextPath}/access/day.htm?date=${receiptGrouped.date.time}'
                                },
                                </c:forEach>
                            ]
                        });

                        $('.fc-button-prev').click(function(){
                            var start = $("#calendar").fullCalendar('getView').start;
                            var eventTime = $.fullCalendar.formatDate(start, "MMM, yyyy");
                            $(loadMonthlyExpenses(eventTime, 'prev'));
                        });

                        $('.fc-button-next').click(function(){
                            var end = $("#calendar").fullCalendar('getView').end;
                            var eventTime = $.fullCalendar.formatDate(end, "MMM, yyyy");
                            $(loadMonthlyExpenses(eventTime, 'next'));
                        });

                    });
                </script>
                <div id='calendar'></div>
			</div>
		</div>
		<div id="tab2" class="first ajx-content">
			<img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>

			<p><strong>No data here submitted for August 2014</strong></p>
		</div>

		<div id="tab3" class="ajx-content">
			<img width="95%" src="${pageContext.request.contextPath}/static/img/sec-bar.jpg"/>
		</div>

		<div id="tab4" class="report1 ajx-content">
			<h1 class="h1">REPORTS</h1>
			<hr>
			<div class="contain3">
				<!-- left content strats-->
				<div class="left-li">
					<ul>
						<li>
							<a class="ll-t" href="#">2014 AUGUST</a>
						</li>
						<li>
							<a class="ll-t" href="#">JULY 2014 $243.83</a>
						</li>
						<li>
							<a class="ll-t" href="#">JUNE 2014</a>
						</li>
						<li>
							<a class="ll-t" href="#">APRIL 2014</a>
						</li>
						<li>
							<a class="ll-t" href="#">MARCH 2014</a>
						</li>
						<li>
							<a class="ll-t" href="#">FEBRUARY 2014</a>
						</li>
						<li>
							<a class="ll-t" href="#">JANUARY 2014</a>
						</li>
						<li>
							<a class="ll-t" href="#">2013</a>
						</li>

					</ul>
				</div>
				<!-- left content ends-->

				<!-- right content starts-->

				<div class="rightside-list-holder">
					<div class="rightside-title">
						<h1 class="rightside-title-text">JULY 2014 <span style="color: #007aff;">$243.83</span></h1>
					</div>
					<ul>
						<li>
							<span class="rightside-li-date-text">JULY 20, 2014</span>
							<a class="rightside-li-middle-text  cd-popup-trigger" href="#">Some& Some</a>
							<span class="rightside-li-right-text">$121.00</span>
						</li>
						<li>
							<span class="rightside-li-date-text">JULY 15, 2014</span>
							<a class="rightside-li-middle-text cd-popup-trigger" href="#">Express</a>
							<span class="rightside-li-right-text">$22.90</span>
						</li>
						<li>
							<span class="rightside-li-date-text">JULY 10, 2014</span>
							<a class="rightside-li-middle-text cd-popup-trigger" href="#">OLA</a>
							<span class="rightside-li-right-text">$57.96</span>
						</li>
						<li>
							<span class="rightside-li-date-text">JULY 03, 2014</span>
							<a class="rightside-li-middle-text cd-popup-trigger" href="#">Dee</a>
							<span class="rightside-li-right-text">$23.75</span>
						</li>
						<li>
							<span class="rightside-li-date-text">JULY 02, 2014</span>
							<a class="rightside-li-middle-text cd-popup-trigger" href="#">Some Data</a>
							<span class="rightside-li-right-text">$14.02</span>
						</li>
						<li>
							<span class="rightside-li-date-text">JULY 01, 2014</span>
							<a class="rightside-li-middle-text cd-popup-trigger" href="#">Collection Collective</a>
							<span class="rightside-li-right-text">$4.20</span>
						</li>
					</ul>
				</div>

				<!-- right content ends-->
			</div>
		</div>
		<div id="tab5" class="ajx-content">
		</div>
	</div>
</div>
<div class="footer-tooth clearfix">
	<div class="footer-tooth-middle"></div>
	<div class="footer-tooth-right"></div>
</div>
<div class="cd-popup" role="alert">
	<div class="cd-popup-container">

		<div id="tabde" class="report ajx-content">
			<div style="float:left;width:55%;margin-right: 3%;">
				<h1 class="h1">AUGUST 26, 2014
					<span style="color: #919191;font-size: 0.8em;font-weight: normal;">12:36PM</span>

				</h1>
				<hr style="width: 100%;">
				<div class="mar10px">
					<h1 class="font3em">Dds Art</h1>

					<p class="padtop2per">Near 123</p>

					<p>Some Where 345</p>
				</div>
				<div class="detailHead">
					<h1 class="font2em" style="margin-left: 5px;">Map-93 <span class="colorblue right">$1.25</span></h1>
				</div>
				<div class="rightside-list-holder border">
					<ul>
						<li>
							<span class="rightside-li-date-text">1. KJHG Med</span>
							<select>
								<option value="volvo">Home</option>
								<option value="saab">Home</option>
							</select>
							<span class="rightside-li-right-text">$1.99</span>
						</li>
						<li>
							<span class="rightside-li-date-text">2. LKJ - Ether</span>
							<select>
								<option value="volvo">Home</option>
								<option value="saab">Home</option>
							</select>
							<span class="rightside-li-right-text">$15.99</span>
						</li>
						<li>
							<span class="rightside-li-date-text">3. This thing</span>
							<select>
								<option value="volvo">Home</option>
								<option value="saab">Home</option>
							</select>
							<span class="rightside-li-right-text">$22.99</span>
						</li>
						<li>
							<span class="rightside-li-date-text">4. Pink stuff</span>
							<select>
								<option value="volvo">Home</option>
								<option value="saab">Home</option>
							</select>
							<span class="rightside-li-right-text">$14.49</span>
						</li>
						<li style="border-bottom: 1px dotted #919191;">
							<span class="rightside-li-date-text">5. Somethings</span>
							<select>
								<option value="volvo">Home</option>
								<option value="saab">Home</option>
							</select>
							<span class="rightside-li-right-text">$13.19</span>
						</li>
					</ul>


					<!-- second list starts-->
					<ul>
						<li>
							<span class="rightside-li-date-text">ABC</span>

							<span class="rightside-li-right-text">$81.65</span>
						</li>
						<li>
							<span class="rightside-li-date-text">BB</span>
							<span class="rightside-li-right-text">$7.60</span>
						</li>
						<li style="border-bottom: 1px solid #919191;">
							<span class="rightside-li-date-text">ZZZ</span>
							<span class="rightside-li-right-text">$89.25</span>
						</li>
					</ul>


					<!-- second list ends -->
					<h1 class="h1 padtop2per" style="padding-bottom:2%;">My notes</h1>
					<textarea style="width: 561px;height: 145px; padding:1%;" placeholder="Write notes here..."></textarea>
					<input type="button" value="DELETE" style="background:#FC462A"></input>
					<input type="button" value="SAVE" style="background:#0079FF"></input>


				</div>


			</div>

			<div style="width:38%;float: left;padding-top: 4%;">
				<img style="width: 390px;height: 590px;padding-left: 8%;" src="${pageContext.request.contextPath}/static/img/details.JPG"/>
			</div>
		</div>
		<a href="#0" class="cd-popup-close img-replace"></a>
	</div>
	<!-- cd-popup-container -->
</div>
</div>
<!-- cd-popup -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css">
<!-- Resource style -->
<script src="${pageContext.request.contextPath}/static/js/modernizr.js"></script>
<!-- Modernizr -->
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<!-- Resource jQuery -->

<c:if test="${!empty landingForm.bizByExpenseTypes}">
<!-- Biz by expense -->
<script>
    $(function () {
        "use strict";

        var colors = Highcharts.getOptions().colors,
                categories = [${landingForm.bizNames}],
                data = [
                    <c:forEach var="item" items="${landingForm.bizByExpenseTypes}"  varStatus="status">
                    {
                        y: ${item.total},
                        color: colors[${status.count-1}],
                        url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}.htm',
                        id: '${item.bizNameForId}',
                        drilldown: {
                            name: '${item.bizName}',
                            categories: [${item.expenseTags}],
                            data: [${item.expenseValues}],
                            color: colors[${status.count-1}],
                            url: '${pageContext.request.contextPath}/access/receipt/biz/${item.bizName}.htm',
                            id: '${item.bizNameForId}'
                        }
                    },
                    </c:forEach>
                ];


        // Build the data arrays
        var bizNames = [];
        var expenseTags = [];
        for (var i = 0; i < data.length; i++) {

            // add browser data
            bizNames.push({
                name: categories[i],
                y: data[i].y,
                color: data[i].color,
                url: data[i].url,
                id: data[i].id
            });

            // add version data
            for (var j = 0; j < data[i].drilldown.data.length; j++) {
                var brightness = 0.2 - (j / data[i].drilldown.data.length) / 5;
                expenseTags.push({
                    name: data[i].drilldown.categories[j],
                    y: data[i].drilldown.data[j],
                    color: Highcharts.Color(data[i].color).brighten(brightness).get(),
                    url: data[i].drilldown.url,
                    id: data[i].drilldown.id
                });
            }
        }

        loadMonthlyExpenses('${landingForm.receiptForMonth.monthYear}', bizNames, expenseTags);
    });
</script>
</c:if>
</body>
</html>
