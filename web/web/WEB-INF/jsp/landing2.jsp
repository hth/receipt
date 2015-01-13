<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html lang="en" ng-app="scroll" ng-controller="Main">
<head>
<meta charset="utf-8" />
<meta name="description" content="" />
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<title><fmt:message key="title" /></title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css" />

<script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
<script async src="${pageContext.request.contextPath}/static/js/receiptofi.js"></script>

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
            <li>
				<a class="top-account-bar-text" href="#">
					<form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
						<input type="submit" value="LOG OUT" class="button"/>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					</form>
				</a>
			</li>
            <li><a class="top-account-bar-text" href="#">PROFILE</a></li>            
            <li>
				<a class="top-account-bar-text user-email" href="#">
					<sec:authentication property="principal.username" />
				</a>
			</li>
        </ul>
    </div>
    <div class="nav-hold">
    <h1>Receiptofi Inc</h1>
        <nav class="nav-list">
            <ul>
                <li class="selected"><a href="${pageContext.request.contextPath}/access/landing.htm">OVERVIEW</a></li>
                <li><a href="first.html">MILEAGE</a></li>
                <li><a href="second.html">EXPENSE ANALYSIS</a></li>
                <li><a href="third.html">REPORTS</a></li>
                <li><a href="fourth.html">MAP</a></li>
            </ul>
        </nav>
    </div>
</header>
<div class="main clearfix">
    <div class="sidebar">
    	<div class="sidebar-top-summary">
    		<div class="sidebar-top-summary-upper clearfix">
    			<h1 class="big-view">
					${pendingCount}
    			</h1>
    			<div class="sts-upper-right">
    				<span class="top-summary-textb">
						<c:choose>
							<c:when test="${pendingCount} eq 1">
								Receipt pending
							</c:when>
							<c:otherwise>
								Receipts pending
							</c:otherwise>
						</c:choose>
					</span>
    				<span class="general-text">
						Last updated: August 28th
					</span>
    			</div>
    		</div>
    		<div class="sidebar-top-summary-lower clearfix">
    			<h1 class="big-view-lower">
					${rejectedCount}
    			</h1>
    			<div class="sts-upper-right">
    				<span class="top-summary-textb">
						<c:choose>
							<c:when test="${rejectedCount} eq 1">
								Receipt rejected
							</c:when>
							<c:otherwise>
								Receipts rejected
							</c:otherwise>
						</c:choose>
					</span>
    				<span class="general-text">Last rejected: August 28th</span>
    			</div>
    		</div>
    	</div>
    	<div class="sidebar-git-datum">
    		<div class="gd-title">
    			<h1 class="widget-title-text">Upload new receipt</h1>
    		</div>
    		<div class="gd-button-holder">
    			<button class="gd-button">UPLOAD RECEIPT</button>
    		</div>
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
						<img alt="indication icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
						<span class="si-general-text">${notification.notificationMessageForDisplay}</span>
						<span class="si-date-text"><fmt:formatDate value="${notification.created}" pattern="MMM. dd" /></span>
					</li>
				</c:forEach>
					<li class="si-list" ng-repeat="i in items">
						<img alt="indication icon" src="${pageContext.request.contextPath}/static/img/notification-icon.png">
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
    			<input type="text" value="Enter emails" size="20"/>
    		</form>
    		<div class="gd-button-holder">
    			<button class="gd-button">SEND INVITE</button>
    		</div>
    	</div>
    </div>
    <div class="rightside-content">
    	<div class="rightside-title">
			<h1 class="rightside-title-text">
				<fmt:formatDate value="${landingForm.receiptForMonth.monthYearDateTime}" pattern="MMMM, yyyy" />
			</h1>
		</div>
		<div class="rightside-list-holder">
			<ul>
				<c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
				<li class="rightside-list">
					<span class="rightside-li-date-text">
						<fmt:formatDate value="${receipt.date}" pattern="MMMM dd, yyyy" />
					</span>
					<a href="${pageContext.request.contextPath}/access/receipt/${receipt.id}.htm" class="rightside-li-middle-text">
						<spring:eval expression="receipt.name" />
					</a>
					<span class="rightside-li-right-text">
						<spring:eval expression='receipt.total' />
					</span>
				</li>
				</c:forEach>
			</ul>
			<p class="view-more-text">View All</p>
		</div>
		<div class="pie-chart">
			<img src="${pageContext.request.contextPath}/static/img/pie-chart.png" style="float: right;">
		</div>
		<div class="pie-chart">
			<img src="${pageContext.request.contextPath}/static/img/cal.png" style="float: right;">
		</div>
    </div>
</div>
<div class="footer-tooth clearfix">
	<div class="footer-tooth-left"></div>
	<div class="footer-tooth-middle"></div>
	<div class="footer-tooth-right"></div>
</div>
</body>
</html>
