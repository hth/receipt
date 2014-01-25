<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html ng-app="App">
<head>
    <meta charset="utf-8">
	<title><fmt:message key="title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />

    <!-- load dojo and provide config via data attribute -->
    <script src="//ajax.googleapis.com/ajax/libs/dojo/1.9.2/dojo/dojo.js"
            data-dojo-config="isDebug: false, parseOnLoad: true">
    </script>
    <script>
        var loadingOverlay = (function(){
            // the overlay object with its methods are the return value
            // of this anonymous function

            var overlayNode;
            return {
                init: function(){
                    // create the container element for the overlay
                    // We store the reference in the overlayNode closure variable
                    overlayNode = dojo.create('div', {
                        id: 'loadingOverlay',
                        'class': 'loadingOverlay pageOverlay',
                        innerHTML: '<div class="loadingMessage">Loading...</div>'
                    }, dojo.body());

                    return this;
                },
                show: function(){
                    // show the overlay
                    dojo.style( overlayNode, {
                        display: 'block'
                    });
                },
                hide: function(){
                    // hide the overlay
                    dojo.fadeOut({
                        node: overlayNode,
                        onEnd: function(){
                            dojo.style(overlayNode, "display", "none");
                        }
                    }).play();
                }
            };
        })();
    </script>

    <link rel='stylesheet' type='text/css' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/1.6.4/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='//cdnjs.cloudflare.com/ajax/libs/fullcalendar/1.6.4/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css' />
    <link rel='stylesheet' type='text/css' href="jquery/fineuploader/fineuploader-3.6.3.css" />
    <link rel='stylesheet' type='text/css' href="jquery/css/_angular/animate-custom.css" />
    <link rel='stylesheet' type='text/css' href='jquery/css/receipt.css' />

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/1.6.4/fullcalendar.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/3.0.7/highcharts.js"></script>
    <script type="text/javascript" src="jquery/fineuploader/jquery.fineuploader-3.6.3.min.js"></script>

    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.7/angular.min.js"></script>
    <script type="text/javascript" src="jquery/js/_angular/angular-animate.min.js"></script>
    <script type="text/javascript" src="jquery/js/_angular/angular-animate.min.js.map"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore-min.js"></script>

    <!-- For drop down menu -->
    <script>
        $(document).ready(function () {
            "use strict";

            $(".account").click(function () {
                var X = $(this).attr('id');
                if (X == 1) {
                    $(".submenu").hide();
                    $(this).attr('id', '0');
                }
                else {
                    $(".submenu").show();
                    $(this).attr('id', '1');
                }

            });

            //Mouse click on sub menu
            $(".submenu").mouseup(function () {
                return false
            });

            //Mouse click on my account link
            $(".account").mouseup(function () {
                return false
            });

            //Document Click
            $(document).mouseup(function () {
                $(".submenu").hide();
                $(".account").attr('id', '');
            });
        });
    </script>

    <script>
        function runCounter(max) {
            "use strict";
            incCounter();

            function incCounter() {
                var currCount = parseInt($('#pendingCountValue').html());
                if (currCount < max) {
                    $('#pendingCountValue').text(currCount + 1);
                    setTimeout(incCounter, 1);
                }
            }
        }
    </script>

    <script>
        $(document).ready(function () {
            "use strict";

            var errorHandler = function (event, id, fileName, reason) {
                qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
            };

            <%-- TODO http://blog.fineuploader.com/2013/01/resume-failed-uploads-from-previous.html --%>
            var restricteduploader = new qq.FineUploader({
                element: $('#restricted-fine-uploader')[0],
                callbacks: {
                    onError: errorHandler,
                    onComplete: function (id, fileName, responseJSON) {
                        if (responseJSON.success == true) {
                            $(this.getItemByFileId(id)).hide('slow');

                            $.ajax({
                                type: 'POST',
                                url:  '${pageContext. request. contextPath}/rws/pending.htm',
                                success: function(response) {
                                    if(response > 0) {
                                        var html = '';
                                        html = html +
                                                "<div class='ui-widget'>" +
                                                "<div class='ui-state-highlight ui-corner-all alert-success' style='margin-top: 0px; padding: 0 .7em;'>" +
                                                "<p>" +
                                                "<span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;' title='Shows number of pending receipt(s) to be processed'></span>" +
                                                "<span style='width:280px;'>";
                                        if (response == 1) {
                                            html = html + "Pending receipt to be processed: ";
                                        } else {
                                            html = html + "Pending receipts to be processed: ";
                                        }
                                        html = html +
                                                "<a href='${pageContext.request.contextPath}/pending.htm' style='text-decoration: none;'>" +
                                                "<strong class='pendingCounter' id='pendingCountValue'>" +
                                                0 +
                                                "</strong>" +
                                                "</a>";
                                        html = html +
                                                "</span>" +
                                                "</p>" +
                                                "</div>" +
                                                "</div>";
                                        $('#pendingCountInitial').hide();
                                        $('#pendingCountId').html(html).show();
                                        $(runCounter(response));
                                    }
                                }
                            });
                        }
                    }
                },
                request: {
                    endpoint: '${pageContext. request. contextPath}/landing/upload.htm',
                    customHeaders: { Accept: 'multipart/form-data' }
                },
                multiple: true,
                validation: {
                    allowedExtensions: ['jpeg', 'jpg', 'gif', 'png'],
                    sizeLimit: 10485760 // 10 MB in bytes
                },
                text: {
                    uploadButton: '&uarr; &nbsp; Click or Drop to upload Receipt(s)'
                },
                showMessage: function (message) {
                    $('#restricted-fine-uploader').append('<div class="alert-error">' + message + '</div>');
                }
            });
        });
    </script>

    <!-- For dashboard tabs -->
    <script>
        $(function () {
            "use strict";

            $('#tabs').css('width','1025px');
            $("#tabs").tabs();
        });
    </script>
    <script>
        var App = angular.module('App', ['ngAnimate']);
        App.constant('SERVICE', {
            'F': '${pageContext.request.contextPath}/mws/f.json',
            'M': '${pageContext.request.contextPath}/mws/m.json',
            'S': '${pageContext.request.contextPath}/mws/s.json',
            'TIMEOUT': 0
        });

        App.controller('mileageCtrl', function($scope, Server, SERVICE, $timeout) {
            $scope.merging = $scope.splitting = false;
            $scope.draggables = [];

            $scope.mergeText = function() {
                return $scope.merging ? 'Merging...' : 'Merge to compute miles driven';
            };

            $scope.splitText = function() {
                return $scope.splitting ? 'Splitting...' : 'Split to odometer reading';
            };

            Server.load().success(function(data) {
                $scope.records = data.ms;
            }).error(function(data) {
                // @todo handle error
            });

            $scope.grab = function(grabbed, index) {
                var record = $scope.records[index],
                        alreadyGrabbed = _.where($scope.draggables, {i: record.i}).length !== 0,
                        isSplitIn = $scope.draggables[0] && $scope.draggables[0].c, // assuming split alone will be in the house
                        limitReached = $scope.draggables.length === 2;

                // If 2 records are already selected and the current one is `merge`
                // then undo the first and insert the current one
                if (limitReached && !record.c) {
                    $scope.records[_.indexOf($scope.records, $scope.draggables[0])].grabbed = false;
                    $scope.draggables.shift();
                }

                // If already selected, undo it
                if (alreadyGrabbed) {
                    $scope.draggables = _.reject($scope.draggables, function(draggable) { return draggable.i === record.i; });
                } else if (grabbed) {
                    // If the current one is `split` or `split` in the house already
                    // then undo/uncheck all and insert the current one
                    if (record.c || isSplitIn) {
                        $scope.draggables.forEach(function(draggable) {
                            $scope.records[_.indexOf($scope.records, draggable)].grabbed = false;
                        });
                        $scope.draggables.length = 0;
                    }
                    $scope.draggables.push(record);
                }

                $scope.errorMessage = '';
            };

            $scope.merge = function() {
                var merger = 0, newRecord = {}, ids = [];

                $scope.saveSnapshot();

                $scope.draggables.forEach(function(draggable, i) {
                    // remove the original grabbed records
                    $scope.records = _.reject($scope.records, function(record) {
                        return record.i === draggable.i;
                    });
                    // and merge them into a single draggable
                    merger = Math.abs(merger) - draggable.t;

                    ids.push(draggable.i);
                });

                // finally update both records and draggables
                newRecord = {i: new Date().getTime(), t: Math.abs(merger), c: true, grabbed: true};
                $scope.draggables = [newRecord];
                $scope.records.push(newRecord);

                // initiate a server call for updates
                $scope.merging = true;
                Server.merge(ids).success(function(data) {
                    $timeout(function() {
                        if (data.success === false) {
                            $scope.loadSnapshot();
                            $scope.errorMessage = data.message;
                        } else {
                            // this also updates id in $scope.draggables, magical??
                            // Not at All. Because of ng-change below
                            angular.extend($scope.records[_.indexOf($scope.records, newRecord)], data.ms[0]);
                            $scope.merging = false;
                            if(data.mm > 0) {
                                $("#mmText").text("Monthly miles driven: " + data.mm + " Miles");
                            } else {
                                $("#mmText").text("No mileage has been computed for this month");
                            }
                        }
                    }, SERVICE.TIMEOUT);
                }).error(function(data) {
                    $timeout(function() {
                        $scope.loadSnapshot();
                    }, SERVICE.TIMEOUT);
                });

                $scope.errorMessage = '';
            };

            $scope.split = function() {
                var newRecords = [], i = $scope.draggables[0].i;

                $scope.saveSnapshot();

                // remove the original grabbed record
                $scope.records = _.reject($scope.records, function(record) {
                    return record.i === i;
                });
                // and split the record in two separate records
                newRecords.push({i: $scope.draggables[0].i, s: $scope.draggables[0].s, e: 0, t: $scope.draggables[0].s, c: false, sd: $scope.draggables[0].sd, n: $scope.draggables[0].n, na: $scope.draggables[0].na});
                newRecords.push({i: $scope.draggables[0].i, s: $scope.draggables[0].e, e: 0, t: $scope.draggables[0].e, c: false, sd: $scope.draggables[0].ed, n: $scope.draggables[0].n, na: $scope.draggables[0].na});

                // finally update both records and draggables
                $scope.records = $scope.records.concat(newRecords);
                $scope.draggables = newRecords;

                // initiate a server call for updates
                $scope.splitting = true;
                Server.split(i).success(function(data) {
                    $timeout(function() {
                        if (data.success === false) {
                            $scope.loadSnapshot();
                            $scope.errorMessage = data.message;
                        } else {
                            // this also updates id in $scope.draggables, magical??
                            // Not at All. Because of ng-change below
                            $scope.records[_.indexOf($scope.records, newRecords[0])].i = data.ms[0].i;
                            $scope.records[_.indexOf($scope.records, newRecords[1])].i = data.ms[1].i;

                            $scope.records[_.indexOf($scope.records, newRecords[0])].n = data.ms[0].n;
                            $scope.records[_.indexOf($scope.records, newRecords[1])].n = data.ms[1].n;

                            $scope.records[_.indexOf($scope.records, newRecords[0])].na = data.ms[0].na;
                            $scope.records[_.indexOf($scope.records, newRecords[1])].na = data.ms[1].na;

                            $scope.splitting = false;
                            if(data.mm > 0) {
                                $("#mmText").text("Monthly miles driven: " + data.mm + " Miles");
                            } else {
                                $("#mmText").text("No mileage has been computed for this month");
                            }
                        }
                    }, SERVICE.TIMEOUT);
                }).error(function(data) {
                    $timeout(function() {
                        $scope.loadSnapshot();
                    }, SERVICE.TIMEOUT);
                });
            };

            $scope.saveSnapshot = function() {
                $scope.recordsSnapshot = $scope.records;
                $scope.draggablesSnapshot = $scope.draggables;
            };

            $scope.loadSnapshot = function() {
                $scope.records = $scope.recordsSnapshot;
                $scope.draggables = $scope.draggablesSnapshot;
                $scope.merging = $scope.splitting = false;
            };
        });

        App.service('Server', function($http, SERVICE) {
            return {
                load: function() {
                    return $http.post(SERVICE.F);
                },
                merge: function(ids) {
                    return $http.post(SERVICE.M, {id1: ids[0], id2: ids[1]});
                },
                split: function(id) {
                    return $http.post(SERVICE.S, {id: id});
                }
            }
        });
    </script>
</head>
<body>
<script>
    // put up the loading overlay while the page initializes
    loadingOverlay.init().show();

    dojo.ready(function () {
        // take down the loading overlay when the page is ready
        loadingOverlay.hide();
    })
</script>

<div class="wrapper">
 	<div class="divTable" style="width: 810px">
		<div class="divRow">
            <div class="divOfCell250" style="height: 46px"><img src="images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" style="height: 40px"/></div>
			<div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="images/gear.png" width="18px" height="15px" style="float: right;"/>
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">Profile And Preferences</a></li>
                                <li><a href="${pageContext.request.contextPath}/signoff.htm">Sign off</a></li>
                                <li><a href="${pageContext.request.contextPath}/eval/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
		    <div class="divOfCell300" id="active-tab-3" style="height: 46px"><h3>Total Expense: <a href="#" style="color: #065c14"><fmt:formatNumber value="${total}" type="currency"/></a></h3></div>
		</div>
   	</div>

	<table style="width: 1025px">
		<tr>
			<td style="vertical-align: top; width: 280px">
                <div id="pendingCountInitial" style="width: 280px;">
                <c:choose>
                <c:when test="${pendingCount gt 0}">
                <div class="ui-widget">
                    <div class="ui-state-highlight ui-corner-all default-state" style="margin-top: 0px; padding: 0 .7em;">
                        <p>
                            <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;" title="Shows number of pending receipt(s) to be processed"></span>
                            <span style="width:280px;">
                            <c:choose>
                                <c:when test="${pendingCount} eq 1">
                                    Pending receipt to be processed:
                                    <a href="${pageContext.request.contextPath}/pending.htm" style="text-decoration: none;">
                                        <strong class="pendingCounter">
                                        ${pendingCount}
                                        </strong>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    Pending receipts to be processed:
                                    <a href="${pageContext.request.contextPath}/pending.htm" style="text-decoration: none;">
                                        <strong class="pendingCounter">
                                        ${pendingCount}
                                        </strong>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                            </span>
                        </p>
                    </div>
                </div>
                </c:when>
                <c:otherwise>
                <div class="ui-widget">
                    <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                        <p>
                            <span class="ui-icon ui-icon-circle-check" style="float: left; margin-right: .3em;" title="No pending receipt to be processed"></span>
                            <span style="display:block; width:280px;">
                                No pending receipt
                            </span>
                        </p>
                    </div>
                </div>
                </c:otherwise>
                </c:choose>
                </div>
                <div id="pendingCountId" style="width: 280px"></div>
                &nbsp;&nbsp;&nbsp;
                <fieldset style="width: 260px; margin-bottom: 10px;">
                    <legend style="color: #065c14; font-weight: bold; font-size: 1.05em">&nbsp;Upload Receipt&nbsp;</legend>
                    <div id="restricted-fine-uploader" style="margin-left: 10px; font-size: 1.05em"></div>
                    <%--<div style="margin-top: 10px; margin-bottom:1px; font-size: 12px">&#8277; Upload 3 files at a time; &#8277; Max upload size - 10 MB</div>--%>
                </fieldset>
                <div style="width: 280px; display: inline">
                    Friend's
                    <input id="inviteEmailId" type="text"
                           onfocus="this.value=''; setInviteBackGroundColor('white'); $('#info').html('&#8277; Invitation sent with your name and email address');                                                         "
                           onblur="setInviteBackGroundColor('#fefefe')"
                           value=" Email address here ..."
                           class="inputForInvitationEmail"
                           />
                    <input type="button" onclick="submitInvitationForm()" name="Invite" value="Invite" />
                </div>
                <div id="info" style="color: black; margin-top: 5px">&#8277; Invitation sent with your name and email address</div>
			</td>
			<td style="vertical-align: top;">
				<div>
					<script type='text/javascript'>
						$(document).ready(function() {
                            "use strict";

							$('#calendar').fullCalendar({
								header : {
									left : 'prev,next today',
									center : '',
									right : 'title'
								},
                                contentHeight: 225,
                                aspectRatio: 1,
								editable : false,
                                weekMode : 'liquid',
								events : [
                                <c:set var="receiptGroupedIterator" value="${landingForm.receiptGrouped}" />
                                <c:forEach var="receiptGrouped" items="${receiptGroupedIterator}">
								{
									title : '<fmt:formatNumber value="${receiptGrouped.stringTotal}" type="currency" />',
									start : '${receiptGrouped.date}',
									end   : '${receiptGrouped.date}',
									url   : '${pageContext.request.contextPath}/day.htm?date=${receiptGrouped.date.time}'
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
			</td>
            <td style="vertical-align: top;" style="width: 250px">
                <c:if test="${!empty landingForm.notifications}">
                <div>
                    <section class="chunk">
                        <fieldset>
                            <legend class="hd">
                                <span class="text"><fmt:message key="notification.title" /></span>
                            </legend>
                            <c:forEach var="notification" items="${landingForm.notifications}" varStatus="status">
                            <div class="bd">
                                <div class="text"><fmt:formatDate value="${notification.created}" pattern="MM/dd" /> - ${notification.notificationMessage4Display}</div>
                            </div>
                            </c:forEach>
                            <div class="bd">
                                <div class="text"><a href="${pageContext.request.contextPath}/notification.htm">more...</a></div>
                            </div>
                        </fieldset>
                    </section>
                </div>
                </c:if>
            </td>
		</tr>
	</table>

    <spring:eval expression="userSession.level ge T(com.receiptofi.domain.types.UserLevelEnum).USER_COMMUNITY" var="isValidForMap" />
    <div id="off_screen">
        <div id="map-canvas"></div>
    </div>

	<!-- Tabs -->
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Receipts</a></li>
			<li><a href="#tabs-2">Mileage</a></li>
			<li><a href="#tabs-3">Expense Analysis</a></li>
            <li><a href="#tabs-4">Reports</a></li>
            <c:if test="${isValidForMap}">
            <li><a href="#tabs-5">Geographical</a></li>
            </c:if>
		</ul>
		<div id="tabs-1" style="height: 500px">
            <div id="onLoadReceiptForMonthId">
            <c:choose>
            <c:when test="${!empty landingForm.receiptForMonth.receipts}">
            <table>
                <tr>
                    <td style="vertical-align: top">
                        <table style="width: 470px" class="etable" id="tableReceiptForMonth">
                            <tr>
                                <th style="padding: 3px;"></th>
                                <th style="padding: 3px;"></th>
                                <th style="padding: 3px;">Business</th>
                                <th style="padding: 3px;">Date</th>
                                <th style="padding: 3px;">Tax</th>
                                <th style="padding: 3px;">Total</th>
                            </tr>
                            <c:forEach var="receipt" items="${landingForm.receiptForMonth.receipts}" varStatus="status">
                            <tr id="${receipt.bizNameForId}">
                                <td style="padding: 3px; text-align: right">
                                    <fmt:formatNumber value="${status.count}" pattern="00"/>.
                                </td>
                                <td style="padding: 3px; text-align: center">
                                    <c:if test="${!empty receipt.expenseReportInFS}">
                                        <a href="${pageContext.request.contextPath}/filedownload/expensofi/${receipt.id}.htm">
                                            <img src="images/download_icon_lg.png" class="downloadIcon" width="14" height="14" title="Download expensed receipt">
                                        </a>
                                    </c:if>
                                </td>
                                <td style="padding: 3px;">
                                    <spring:eval expression="receipt.name" />
                                </td>
                                <td style="padding: 3px;">
                                    <fmt:formatDate value="${receipt.date}" pattern="dd, MMM" />
                                </td>
                                <td style="padding: 3px; text-align: right">
                                    <spring:eval expression="receipt.tax" />
                                </td>
                                <td style="padding: 3px; text-align: right">
                                    <a href="${pageContext.request.contextPath}/receipt/${receipt.id}.htm">
                                        <spring:eval expression='receipt.total' />
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </table>
                    </td>
                    <td style="vertical-align: top">
                        <div id="container" style="min-width: 530px; height: 425px; margin: 0 auto"></div>
                    </td>
                </tr>
            </table>
            </c:when>
            <c:otherwise>
            <div class="ui-widget">
                <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                    <p>
                        <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                        <span style="display:block; width:410px;">
                            No receipt(s) submitted or transformed for <b>${landingForm.receiptForMonth.monthYear}</b>
                        </span>
                    </p>
                </div>
            </div>
            </c:otherwise>
            </c:choose>
            </div>

            <div id="refreshReceiptForMonthId"></div>
		</div>
        <div id="tabs-2" style="height: 500px;">
            <div ng-controller="mileageCtrl">
                <c:choose>
                    <c:when test="${!empty landingForm.mileageEntities}">
                    <div style="display:block; width:410px; padding: 5px; margin-bottom: 10px" id="mmText">
                        <fmt:formatNumber value="${landingForm.mileageMonthlyTotal}" type="number" /> Miles driven in <b>${landingForm.receiptForMonth.monthYear}</b>
                    </div>
                    <div class='alert alert-danger' ng-bind="errorMessage" ng-show="errorMessage"></div>
                    <div class="col-xs-6">
                        <table style="width: 465px" class="etable" id="tableMileageForMonth">
                            <tr>
                                <th style="padding: 3px;"></th>
                                <th style="padding: 3px;"></th>
                                <th style="padding: 3px;">Day</th>
                                <th style="padding: 3px;">Odometer reading / Miles driven</th>
                                <th style="padding: 3px">Notes</th>
                            </tr>
                            <tbody ng-repeat="record in records">
                            <tr ng-switch on="record.c">
                                <td style="padding: 3px; text-align: center" ng-switch-when="true">
                                    <input type="checkbox" ng-model="record.grabbed" ng-change="grab(record.grabbed, $index)" ng-disabled="merging || splitting">
                                </td>
                                <td style="padding: 3px; text-align: center" ng-switch-when="false">
                                    <input type="checkbox" ng-model="record.grabbed" ng-change="grab(record.grabbed, $index)" ng-disabled="merging || splitting">
                                </td>
                                <td style="padding: 3px; text-align: center" ng-switch-when="true">
                                    <img src="images/cars.png" style="height: 18px; width: 25px"/>
                                </td>
                                <td style="padding: 3px; text-align: center" ng-switch-when="false">
                                    <img src="images/odometers.png" />
                                </td>
                                <td style="padding: 3px; text-align: left" ng-switch-when="false">
                                    {{record.sd | date:'dd MMM'}}
                                </td>
                                <td style="padding: 3px; text-align: left" ng-switch-when="true">
                                    {{record.sd | date:'dd MMM'}} - {{record.ed | date:'dd MMM'}}
                                </td>
                                <td style="padding: 3px; text-align: left" ng-switch-when="true">
                                    <a href="modv/{{record.i}}.htm" style="color: #065c14;">{{record.t | number:2}} Miles driven</a>
                                </td>
                                <td style="padding: 3px; text-align: left" ng-switch-when="false">
                                    <a href="modv/{{record.i}}.htm" style="color: darkred">{{record.t | number:2}} Odometer reading</a>
                                </td>
                                <td style="padding: 3px; text-align: left" ng-switch-when="false" title="{{record.n}}">
                                    {{record.na}}
                                </td>
                                <td style="padding: 3px; text-align: left" ng-switch-when="true" title="{{record.n}}">
                                    {{record.na}}
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-xs-6">
                        <button class="btn btn-success" ng-show="(draggables.length == 2 && !splitting) || merging" ng-click="merge()" ng-disabled="merging" ng-bind="mergeText()"></button>
                        <button class="btn btn-danger" ng-show="(draggables[0].c && !merging) || splitting" ng-click="split()" ng-disabled="splitting" ng-bind="splitText()"></button>
                        <br><br>
                        <div class="btn btn-default btn-lg btn-block draggable-animation" ng-repeat="draggable in draggables">
                        <span ng-switch on="draggable.c">
                            <div ng-switch-when="true">
                                <img src="images/cars.png" style="height: 18px; width: 25px"/> {{draggable.t | number:2}} Miles driven
                            </div>
                            <div ng-switch-when="false">
                                <img src="images/odometers.png" /> {{draggable.t | number:2}} Odometer reading
                            </div>
                        </span>
                        </div>
                    </div>
                    </c:when>
                    <c:otherwise>
                    <div class="ui-widget">
                        <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                            <p>
                                <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                                <span style="display:block; width:410px;">
                                    No odometer reading submitted or transformed for <b>${landingForm.receiptForMonth.monthYear}</b>
                                </span>
                            </p>
                        </div>
                    </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
		<div id="tabs-3" style="height: 500px">
            <c:choose>
            <c:when test="${!empty months}">
            <table>
                <tr>
                    <td style="vertical-align: top">
                        <div id="monthly" style="min-width: 475px; height: 425px; margin: 0 auto"></div>

                        <fieldset style="width:295px;">
                            <legend>Total Expense</legend>
                            <div class="divTable">
                                <div class="headRow">
                                    <div class="divCell">Sub Total</div>
                                    <div class="divOfCell75">
                                        &nbsp;&nbsp;&nbsp;Tax
                                    </div>
                                    <div class="divOfCell110">
                                        &nbsp;&nbsp;&nbsp;Total
                                    </div>
                                </div>
                                <div class="divRow">
                                    <div class="divCell">${total}</div>
                                    <div class="divOfCell75">+ ${tax}</div>
                                    <div class="divOfCell110">= ${totalWithoutTax}</div>
                                </div>
                            </div>
                        </fieldset>
                    </td>
                    <td style="vertical-align: top">
                        <div id="allExpenseTypes" style="min-width: 525px; height: 420px; margin: 0 auto"></div>
                    </td>
                </tr>
            </table>
            </c:when>
            <c:otherwise>
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width:410px;">
                        No expense analysis available as no receipt submitted or transformed
                    </span>
                </p>
            </div>
            </c:otherwise>
            </c:choose>
		</div>
        <div id="tabs-4" style="height: 500px">
            <c:choose>
                <c:when test="${!empty landingForm.receiptGroupedByMonths}">
                    <p>
                    <span style="display:block; width:410px;">
                        <b>Archived monthly report(s) for active month(s)</b>
                    </span>
                    </p>

                    <table style="width: 100px" class="etable">
                        <c:forEach var="item" items="${landingForm.receiptGroupedByMonths}"  varStatus="status">
                            <tr>
                                <td style="padding: 3px;">
                                    <a href="${pageContext.request.contextPath}/landing/report/<spring:eval expression='item.dateTime.toString("MMM, yyyy")' />.htm" target="_blank">
                                        <spring:eval expression='item.dateTime.toString("MMM, yyyy")' />
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="ui-widget">
                        <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                            <p>
                            <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                            <span style="display:block; width:410px;">
                                No receipt(s) submitted or transformed
                            </span>
                            </p>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <c:if test="${isValidForMap}">
		<div id="tabs-5" style="height: 500px">
            <c:choose>
            <c:when test="${!empty months}">
            <div id="map-placeholder"></div>
            </c:when>
            <c:otherwise>
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width:410px;">
                        No data available as no receipt submitted or transformed
                    </span>
                </p>
            </div>
            </c:otherwise>
            </c:choose>
		</div>
        </c:if>
	</div>

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved. (<fmt:message key="build.version" />)</p>
</div>

<script>
    $("#active-tab-3").click(function() {
        $("#tabs").tabs({ active: 2 });
    });
    <c:if test="${!empty param.showTab}">
        $("#tabs").tabs({ active: ${param.showTab} });
    </c:if>
</script>

<script>
    function loadMonthlyExpenses(date, clicked) {
        $.ajax({
            type: "POST",
            url: '${pageContext. request. contextPath}/landing/monthly_expenses.htm',
            data: {
                monthView: date,
                buttonClick: clicked
            },
            beforeSend: function() {
                $('#onLoadReceiptForMonthId').hide();
                $('#refreshReceiptForMonthId').html(
                        "<div class='spinner large' id='spinner'></div>"
                ).show();
            },
            success: function (response) {
                $('#refreshReceiptForMonthId').html(response).show();
            },
            complete: function() {
                //do nothing as load removes spinner
            }
        });
    }
</script>

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
                    url: 'receipt/biz/${item.bizName}.htm',
                    id: '${item.bizNameForId}',
                    drilldown: {
                        name: '${item.bizName}',
                        categories: [${item.expenseTags}],
                        data: [${item.expenseValues}],
                        color: colors[${status.count-1}],
                        url: 'receipt/biz/${item.bizName}.htm',
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

        // Create the chart
        $('#container').highcharts({
            chart: {
                type: 'pie'
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Business By Expense, ${landingForm.receiptForMonth.monthYear}'
            },
            yAxis: {
                title: {
                    text: 'Total expense'
                }
            },
            plotOptions: {
                pie: {
                    shadow: false,
                    center: ['50%', '50%'],
                    slicedOffset: 0
                }
            },
            tooltip: {
                valueSuffix: '$',
                formatter: function() {
                    return this.point.name + ": " + '$' + Highcharts.numberFormat(this.y, 2);
                }
            },
            series: [
                {
                    name: 'Total',
                    data: bizNames,
                    size: '60%',
                    dataLabels: {
                        enabled: false,
                        formatter: function () {
                            return this.y > 1 ? this.point.name : null;
                        },
                        color: 'white',
                        distance: -30
                    },
                    point: {
                        events: {
                            click: function(e) {
                                console.log(this.options.url);
                                location.href = this.options.url;
                            },
                            mouseOver: function(e) {
                                console.log('#' + this.options.id);
                                $('#tableReceiptForMonth tr#' + this.options.id).toggleClass('highlight');
                            },
                            mouseOut: function(e) {
                                console.log('#' + this.options.id);
                                $('#tableReceiptForMonth tr#' + this.options.id).removeClass('highlight');
                            }
                        }
                    },
                    allowPointSelect: true,
                    cursor: 'pointer'
                },
                {
                    name: 'Total',
                    data: expenseTags,
                    size: '80%',
                    innerSize: '60%',
                    dataLabels: {
                        enabled: false,
                        formatter: function () {
                            // display only if larger than 1
                            return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + '$' + Highcharts.numberFormat(this.y, 2) : null;
                        }
                    },
                    point: {
                        events: {
                            click: function(e) {
                                console.log(this.options.url);
                                location.href = this.options.url;
                            },
                            mouseOver: function(e) {
                                console.log('#' + this.options.id);
                                $('#tableReceiptForMonth tr#' + this.options.id).toggleClass('highlight');
                            },
                            mouseOut: function(e) {
                                console.log('#' + this.options.id);
                                $('#tableReceiptForMonth tr#' + this.options.id).removeClass('highlight');
                            }
                        }
                    },
                    allowPointSelect: true,
                    cursor: 'pointer'
                }
            ]
        });
    });
</script>
</c:if>

<c:if test="${!empty months}">
<!-- Monthly expense graph -->
<script>
    $(function () {
        "use strict";

        $('#monthly').highcharts({
            chart: {
                type: 'column',
                margin: [ 50, 50, 100, 50]
            },
            title: {
                text: 'Monthly Expenses ${months.get(0).year - 1} - ${months.get(0).year}'
            },
            credits: {
                enabled: false
            },
            xAxis: {
                categories: [
                    <c:forEach var="month" items="${months}"  varStatus="status">
                    '${month.monthName}',
                    </c:forEach>
                ],
                labels: {
                    rotation: -45,
                    align: 'right',
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Expenses in Dollar($)'
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b><br/>'+
                            'Expense in ${months.get(0).year}: '+ Highcharts.numberFormat(this.y, 2) +
                            '$';
                }
            },
            series: [{
                name: 'Monthly Expense',
                data: [
                    <c:forEach var="month" items="${months}" varStatus="status">
                    {y: ${month.stringTotal}, color: 'darkgreen'},
                    </c:forEach>
                ],
                dataLabels: {
                    enabled: false,
                    rotation: -90,
                    color: '#FFFFFF',
                    align: 'right',
                    x: 4,
                    y: 10,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    },
                    formatter:function(){
                        if(this.y > 0)
                            return this.y;
                    }
                }
            }]
        });
    });
</script>
</c:if>

<c:if test="${!empty months && isValidForMap}">
<!-- Google Map -->
<script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAG0ce7n_9QZBXMRtBZoVmIGbgim-Z7YbA&sensor=false">
</script>
<script type="text/javascript">
    $(document).ready(function () {
        "use strict";

        var bounds = new google.maps.LatLngBounds ();
        var map, infowindow;

        getGoogleMap();

        function getGoogleMap() {
            var myOptions = {
                zoom: 4,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }

            var $mapCanvas = $("#map-canvas");
            map = new google.maps.Map($mapCanvas.get(0), myOptions);
            map.fitBounds(bounds);  //Fit these bounds to the map
            var listenerHandle = google.maps.event.addListener(map, 'idle', function() {
                $mapCanvas.appendTo($("#map-placeholder"));
                google.maps.event.removeListener(listenerHandle);
            });

            infowindow = new google.maps.InfoWindow();
            google.maps.event.addListener(map, 'click', function() {
                infowindow.close();
            });

            /**
             * Data for the markers consisting of a name, a LatLng and a zIndex for
             * the order in which these markers should display on top of each
             * other.
             */
            var locations = [
                <c:forEach var="loc" items="${landingForm.receiptGroupedByBizLocations}" varStatus="status">
                    [
                        '<div class="mapContainer">' +
                            '<div><h1>${loc.bizName.safeJSName} : <b>${loc.totalStr}</b></h1></div>' +
                            '<div>' +
                                '<div>${loc.bizStore.address}</div>' +
                            '</div>' +
                        '</div>',
                        ${loc.bizStore.lat}, ${loc.bizStore.lng}, ${status.count}
                    ],
                </c:forEach>
            ];

            for (var i = 0; i < locations.length; i++) {
                var location    = locations[i];
                var title       = location[0];
                var latitude    = location[1];
                var longitude   = location[2];
                var xindex      = location[3];
                displayMarker(title, latitude, longitude, xindex);

                // And increase the bounds to take this point
                bounds.extend(new google.maps.LatLng (latitude, longitude));
            }
        }

        function displayMarker(title, latitude, longitude, xindex) {
            // Add markers to the map

            // Marker sizes are expressed as a Size of X,Y
            // where the origin of the image (0,0) is located
            // in the top left of the image.

            // Origins, anchor positions and coordinates of the marker
            // increase in the X direction to the right and in
            // the Y direction down.
            var image = {
                url: 'images/beachflag.png',
                // This marker is 20 pixels wide by 32 pixels tall.
                size: new google.maps.Size(20, 32),
                // The origin for this image is 0,0.
                origin: new google.maps.Point(0,0),
                // The anchor for this image is the base of the flagpole at 0,32.
                anchor: new google.maps.Point(0, 32)
            };
            var shadow = {
                url: 'images/beachflag_shadow.png',
                // The shadow image is larger in the horizontal dimension
                // while the position and offset are the same as for the main image.
                size: new google.maps.Size(37, 32),
                origin: new google.maps.Point(0,0),
                anchor: new google.maps.Point(0, 32)
            };
            // Shapes define the clickable region of the icon.
            // The type defines an HTML &lt;area&gt; element 'poly' which
            // traces out a polygon as a series of X,Y points. The final
            // coordinate closes the poly by connecting to the first
            // coordinate.
            var shape = {
                coord: [1, 1, 1, 20, 18, 20, 18 , 1],
                type: 'poly'
            };


            var myLatLng = new google.maps.LatLng(latitude, longitude);

            //Why re-center the US Map
            //map.setCenter(myLatLng);

            var marker = new google.maps.Marker({
                position: myLatLng,
                map: map,
                shadow: shadow,
                icon: image,
                shape: shape,
                title: title,
                zIndex: xindex
            });

            google.maps.event.addListener(marker, 'click', function() {
                infowindow.setContent(title);
                infowindow.open(map, marker);
            });

            google.maps.event.addListener(marker, 'mouseover', function() {
                infowindow.setContent(title);
                infowindow.open(map, marker);
            });
        }
    });
</script>
</c:if>

<c:if test="${!empty itemExpenses}">
<!-- Expense by Item types -->
<script>
    $(function () {
        "use strict";

        $('#allExpenseTypes').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Expense Share'
            },
            subtitle: {
                text: 'For 2013'
            },
            tooltip: {
                formatter: function () {
                    return this.point.name + ': <b>' + Highcharts.numberFormat(this.percentage, 2) + '%</b>';
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        formatter: function() {
                            return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 2) +' %';
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Expense share',
                point: {
                    events: {
                        click: function(e) {
                            location.href = e.point.url;
                            e.preventDefault();
                        }
                    }
                },
                data: [

                    <c:choose>
                        <c:when test="${!empty itemExpenses}">
                            <c:set var="first" value="false"/>
                            <c:forEach var="item" items="${itemExpenses}"  varStatus="status">
                            <c:choose>
                                <c:when test="${first eq false}">
                                    {
                                        name: '${item.key}',
                                        y: ${item.value},
                                        sliced: true,
                                        selected: true,
                                        url: '${pageContext.request.contextPath}/expenses/${item.key}.htm'
                                    },
                                    <c:set var="first" value="true"/>
                                </c:when>
                                <c:otherwise>
                                    {
                                        name: '${item.key}',
                                        y: ${item.value},
                                        sliced: false,
                                        selected: false,
                                        url: '${pageContext.request.contextPath}/expenses/${item.key}.htm'
                                    },
                                </c:otherwise>
                            </c:choose>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="item" items="${itemExpenses}"  varStatus="status">
                                {
                                    name: '${item.key}',
                                    y: ${item.value},
                                    sliced: false,
                                    selected: false,
                                    url: '${pageContext.request.contextPath}/expenses/${item.key}.htm'
                                },
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                ]
            }]
        });
    });
</script>
</c:if>

<script>
    function setInviteBackGroundColor(color) {
        "use strict";

        document.getElementById("inviteEmailId").style.background=color
    }

    function submitInvitationForm() {
        "use strict";

        var inviteEmailId = jQuery("#inviteEmailId").val();
        var object = {emailId: inviteEmailId};

        $.ajax({
            type: "POST",
            url: "${pageContext. request. contextPath}/landing/invite.htm",
            data: object,
            success: function(response) {
                $('#info').html(response);
                $('#inviteEmailId').val(' Email address here ...');
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
            }
        });

//TODO
//      http://stackoverflow.com/questions/377644/jquery-ajax-error-handling-show-custom-exception-messages
        $("div#errorcontainer")
            $.ajaxError(
            function(e, x, settings, exception) {
                var message;
                var statusErrorMap = {
                    '400' : "Server understood the request but request content was invalid.",
                    '401' : "Unauthorised access.",
                    '403' : "Forbidden resource can't be accessed",
                    '500' : "Internal Server Error.",
                    '503' : "Service Unavailable"
                };
                if (x.status) {
                    message =statusErrorMap[x.status];
                    if(!message){
                        message="Unknow Error \n.";
                    }
                }else if(exception=='parsererror'){
                    message="Error.\nParsing JSON Request failed.";
                }else if(exception=='timeout'){
                    message="Request Time out.";
                }else if(exception=='abort'){
                    message="Request was aborted by the server";
                }else {
                    message="Unknown Error \n.";
                }
                $(this).css("display","inline");
                $(this).html(message);
            });
    }
</script>

<script>
    $(function () {
        $('.tooltip').each(function () {
            var $this, id, t;

            $this = $(this);
            id = this.id;
            t = $('<span />', {
                title: $this.attr('title')
            }).appendTo($this.parent()).tooltip({
                position: {
                    of: '#' + id,
                    my: "left+190 center",
                    at: "left center",
                    collision: "fit"
                }
            });
            // remove the title from the real element.
            $this.attr('title', '');
            $('#' + id).focusin(function () {
                t.tooltip('open');
            }).focusout(function () {
                t.tooltip('close');
            });
        });
    });

    $(function () {
        $(document).tooltip();
    });
</script>

</body>
</html>