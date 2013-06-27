<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css' />
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css' />
    <link rel='stylesheet' type='text/css' href="jquery/fineuploader/fineuploader-3.6.3.css" />

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
    <script type='text/javascript' src="jquery/js/highcharts.js"></script>
    <script type="text/javascript" src="jquery/fineuploader/jquery.fineuploader-3.6.3.min.js"></script>

    <script>
        function runCounter(max) {
            var runTill = max;
            incCounter();

            function incCounter() {
                var currCount = parseInt($('#pendingCountValue').html());
                $('#pendingCountValue').text(currCount+1);
                if (currCount+1 != runTill) {
                    setTimeout(incCounter,50);
                }
            }
        }
    </script>

    <script>
        $(document).ready(function() {
            var errorHandler = function (event, id, fileName, reason) {
                qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
            };

            <%-- TODO http://blog.fineuploader.com/2013/01/resume-failed-uploads-from-previous.html --%>
            var restricteduploader = new qq.FineUploader({
                element: $('#restricted-fine-uploader')[0],
                callbacks: {
                    onError: errorHandler,
                    onComplete: function(id, fileName, responseJSON) {
                        if (responseJSON.success == true) {
                            $(this.getItemByFileId(id)).hide('slow');

                            $.ajax({
                                type: 'POST',
                                url:  '${pageContext. request. contextPath}/fetcher/pending.htm',
                                success: function(response) {
                                    if(response > 0) {
                                        var html = '';
                                        html = html +   "<div class='ui-widget'>" +
                                                            "<div class='ui-state-highlight ui-corner-all alert-success' style='margin-top: 0px; padding: 0 .7em;'>" +
                                                                "<p>" +
                                                                    "<span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;' title='Shows number of pending receipt(s) to be processed'></span>" +
                                                                    "<span style='display:block; width:310px;'>";
                                        if(response == 1) {
                                            html = html +               "Pending receipt to be processed: ";
                                        } else {
                                            html = html +               "Pending receipts to be processed: ";
                                        }
                                        html = html +                   "<a href='${pageContext.request.contextPath}/pending.htm'><strong style='color: green;' class='timer' id='pendingCountValue'>" + 0 + "</strong></a>";
                                        html = html +               "</span>" +
                                                                "</p>" +
                                                            "</div>" +
                                                        "</div>";
                                        $('#pendingCountInitial').hide();
                                        $('#pendingCountId').html(html);
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
                showMessage: function(message) {
                    $('#restricted-fine-uploader').append('<div class="alert-error">' + message + '</div>');
                }
            });
        });
    </script>

	<!-- For tabs -->
	<script>
		$(function() {

			$( "#accordion" ).accordion();

			var availableTags = [
				"ActionScript",
				"AppleScript",
				"Asp",
				"BASIC",
				"C",
				"C++",
				"Clojure",
				"COBOL",
				"ColdFusion",
				"Erlang",
				"Fortran",
				"Groovy",
				"Haskell",
				"Java",
				"JavaScript",
				"Lisp",
				"Perl",
				"PHP",
				"Python",
				"Ruby",
				"Scala",
				"Scheme"
			];
			$( "#autocomplete" ).autocomplete({
				source: availableTags
			});

			$( "#button" ).button();
			$( "#radioset" ).buttonset();

			$( "#tabs" ).tabs();

			$( "#dialog" ).dialog({
				autoOpen: false,
				width: 400,
				buttons: [
					{
						text: "Ok",
						click: function() {
							$( this ).dialog( "close" );
						}
					},
					{
						text: "Cancel",
						click: function() {
							$( this ).dialog( "close" );
						}
					}
				]
			});

			// Link to open the dialog
			$( "#dialog-link" ).click(function( event ) {
				$( "#dialog" ).dialog( "open" );
				event.preventDefault();
			});

			$( "#datepicker" ).datepicker({
				inline: true
			});

			$( "#slider" ).slider({
				range: true,
				values: [ 17, 67 ]
			});

			$( "#progressbar" ).progressbar({
				value: 20
			});

			// Hover states on the static widgets
			$( "#dialog-link, #icons li" ).hover(
				function() {
					$( this ).addClass( "ui-state-hover" );
				},
				function() {
					$( this ).removeClass( "ui-state-hover" );
				}
			);
		});
	</script>
</head>
<body>
<div class="wrapper">
 	<div class="divTable">
		<div class="divRow">
            <div class="divOfCell300"><img src="images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"></div>
			<div class="divOfCell200"><h3><a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">${sessionScope['userSession'].emailId}</a></h3></div>
		    <div class="divOfCell200" id="active-tab-2"><h3>Total Expense: <a href="#"><fmt:formatNumber value="${total}" type="currency"/></a></h3></div>
		</div>
   	</div>

	<table>
		<tr>
			<td valign="top">
                <div id="pendingCountInitial">
                <c:choose>
                    <c:when test="${pendingCount gt 0}">
                        <div class="ui-widget">
                            <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                                <p>
                                    <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;" title="Shows number of pending receipt(s) to be processed"></span>
                                    <span style="display:block; width:310px;">
                                        <c:choose>
                                            <c:when test="${pendingCount} eq 1">
                                                Pending receipt to be processed: <a href="${pageContext.request.contextPath}/pending.htm"><strong>${pendingCount}</strong></a>
                                            </c:when>
                                            <c:otherwise>
                                                Pending receipts to be processed: <a href="${pageContext.request.contextPath}/pending.htm"><strong>${pendingCount}</strong></a>
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
                                    <span style="display:block; width:310px;">
                                        No pending receipt
                                    </span>
                                </p>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                </div>
                <div id="pendingCountId"></div>
                &nbsp;&nbsp;&nbsp;
                <fieldset style="width: 310px; margin-bottom: 10px;">
                    <legend>Upload Receipt</legend>
                    <div id="restricted-fine-uploader"></div>
                    <%--<div style="margin-top: 10px; margin-bottom:1px; font-size: 12px">&#8277; Upload 3 files at a time; &#8277; Max upload size - 10 MB</div>--%>
                </fieldset>
                <div>
                    Friend's
                    <input id="inviteEmailId" type="text"
                           onfocus="this.value=''; setInviteBackGroundColor('white'); $('#info').html('&#8277; Invitation sent with your name and email address');                                                        "
                           onblur="setInviteBackGroundColor('#fefefe')"
                           value=" Email address here ..."
                           size="33"/>
                    <input type="button" onclick="submitInvitationForm()" name="Invite" value="Invite" size="5">
                </div>
                <div id="info" style="color: black; margin-top: 5px">&#8277; Invitation sent with your name and email address</div>
			</td>
			<td>
				&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
			<td valign="top">
				<div>
					<script type='text/javascript'>
						$(document).ready(function() {

							var date = new Date();
							var d = date.getDate();
							var m = date.getMonth();
							var y = date.getFullYear();

							$('#calendar').fullCalendar({
								header : {
									left : 'prev,next today',
									center : '',
									right : 'title'
								},
								editable : false,
								events : [
                                <c:set var="receiptGroupedIterator" value="${requestScope.receiptGrouped}" />
                                <c:forEach var="receiptGrouped" items="${receiptGroupedIterator}">
								{
									title : '${receiptGrouped.stringTotal}',
									start : '${receiptGrouped.date}',
									end   : '${receiptGrouped.date}',
									url   : '${pageContext.request.contextPath}/day.htm?date=${receiptGrouped.date.time}'
								} ,
                                </c:forEach>
								]
							});

						});
					</script>
					<div id='calendar'></div>
			    </div>
			</td>
		</tr>
	</table>

	<!-- Tabs -->
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Receipts</a></li>
			<li><a href="#tabs-2">Expense Analysis</a></li>
			<li><a href="#tabs-3">More</a></li>
		</ul>
		<div id="tabs-1">
            <c:choose>
            <c:when test="${!empty receipts}">
            <table>
                <tr>
                    <td style="vertical-align: top">
                        <table style="width: 470px" class="etable">
                            <tr>
                                <th style="padding: 3px;"></th>
                                <th style="padding: 3px;">Business</th>
                                <th style="padding: 3px;">Receipt Date</th>
                                <th style="padding: 3px;">Tax</th>
                                <th style="padding: 3px;">Total</th>
                            </tr>
                            <c:forEach var="receipt" items="${receipts}"  varStatus="status">
                            <tr>
                                <td style="padding: 3px;" align="right">
                                    ${status.count}
                                </td>
                                <td style="padding: 3px;">
                                    <spring:eval expression="receipt.bizName.name" />
                                </td>
                                <td style="padding: 3px;">
                                    <fmt:formatDate value="${receipt.receiptDate}" type="date"/>
                                </td>
                                <td style="padding: 3px;" align="right">
                                    <spring:eval expression="receipt.tax" />
                                </td>
                                <td style="padding: 3px;" align="right">
                                    <a href="${pageContext.request.contextPath}/receipt.htm?id=${receipt.id}">
                                        <spring:eval expression="receipt.total" />
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </table>
                    </td>
                    <td style="vertical-align: top">
                        <div id="container" style="min-width: 525px; height: 275px; margin: 0 auto"></div>
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
                            No receipt(s) submitted or transformed for this month
                        </span>
                    </p>
                </div>
            </div>
            </c:otherwise>
            </c:choose>
		</div>
		<div id="tabs-2">
            <c:if test="${!empty receipts}">
            <table>
                <tr>
                    <td style="vertical-align: top">
                        <div id="monthly" style="min-width: 525px; height: 375px; margin: 0 auto"></div>

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
                        <div id="allExpenseTypes" style="min-width: 525px; height: 375px; margin: 0 auto"></div>
                    </td>
                </tr>
            </table>
            </c:if>
		</div>
		<div id="tabs-3">
            <c:if test="${!empty receipts}">
            <div class="googleMapContainer" id="map-canvas"></div>
            </c:if>
		</div>
	</div>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

<c:if test="${!empty bizByExpenseTypes}">
<script>
    $(function () {

        var colors = Highcharts.getOptions().colors,
                categories = [${bizNames}],
                name = 'Receipt Expenses',
                data = [
                    <c:forEach var="item" items="${bizByExpenseTypes}"  varStatus="status">
                    {
                        y: ${item.total},
                        color: colors[${status.count-1}],
                        url: 'http://bing.com/search?q=foo',
                        drilldown: {
                            name: '${item.bizName}',
                            categories: [${item.expenseTypes}],
                            data: [${item.expenseValues}],
                            color: colors[${status.count-1}],
                            url: 'http://bing.com/search?q=foo'
                        }
                    },
                    </c:forEach>
                ];


        // Build the data arrays
        var bizNames = [];
        var expenseTypes = [];
        for (var i = 0; i < data.length; i++) {

            // add browser data
            bizNames.push({
                name: categories[i],
                y: data[i].y,
                color: data[i].color
            });

            // add version data
            for (var j = 0; j < data[i].drilldown.data.length; j++) {
                var brightness = 0.2 - (j / data[i].drilldown.data.length) / 5;
                expenseTypes.push({
                    name: data[i].drilldown.categories[j],
                    y: data[i].drilldown.data[j],
                    color: Highcharts.Color(data[i].color).brighten(brightness).get()
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
                text: 'Business By Expense, ?Month?, ?2013?'
            },
            yAxis: {
                title: {
                    text: 'Total expense'
                }
            },
            plotOptions: {
                pie: {
                    shadow: false,
                    center: ['50%', '50%']
                }
            },
            tooltip: {
                valueSuffix: '$',
                formatter: function() {
                    return this.point.name + ": " + this.point.y + "$";
                }
            },
            series: [
                {
                    name: 'Total',
                    data: bizNames,
                    size: '60%',
                    dataLabels: {
                        formatter: function () {
                            return this.y > 5 ? this.point.name : null;
                        },
                        color: 'white',
                        distance: -30
                    },
                    point: {
                        events: {
                            click: function(e) {
                                location.href = e.point.series.options.url; //proper path 2)
                                e.preventDefault();
                            }
                        }
                    },
                    allowPointSelect: true,
                    cursor: 'pointer'
                },
                {
                    name: 'Total',
                    data: expenseTypes,
                    size: '80%',
                    innerSize: '60%',
                    dataLabels: {
                        formatter: function () {
                            // display only if larger than 1
                            return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + this.y + '$' : null;
                        }
                    },
                    point: {
                        events: {
                            click: function(e) {
                                location.href = e.point.series.options.url; //proper path 2)
                                e.preventDefault();
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
<script>
    $(function () {
        $('#monthly').highcharts({
            chart: {
                type: 'column',
                margin: [ 50, 50, 100, 40]
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
                            'Expense in ${months.get(0).year}: '+ Highcharts.numberFormat(this.y, 1) +
                            '$';
                }
            },
            series: [{
                name: 'Population',
                data: [
                    <c:forEach var="month" items="${months}"  varStatus="status">
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

<script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCsVM5IGJXRnMEZvva3F3TW0tcbnbyW-Pw&sensor=false">
</script>
<script type="text/javascript">
    $(document).ready(function () {
        var us_center = new google.maps.LatLng(37.090240,-95.7128910);

        var map;
        var infowindow;

        getGoogleMap('41.033245', '29.110191',
                '<div class="mapContainer">' +
                '<div class="mapContentLeft"><h1>Hotel Name <i>Information/Suggestion</i></h1>' +
                '<div>Hotel Image</div>' +
                '</div>' +
                '<div class="mapContentRight">' +
                    '<div class="mapHotelStars">*****</div>' +
                    '<div class="mapHotelAdress">Hotel Adress</div>' +
                    '<div class="mapHotelPrice"> Currency + Integer </div>' +
                    '</div>' +
                '</div>');

        function getGoogleMap(Altitude, Latitude, Address) {
            var myOptions = {
                center: us_center,
                zoom: 4,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }

            map = new google.maps.Map(document.getElementById("map-canvas"), myOptions);
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
                ['San Francisco', 37.77493,	-122.41942, 4],
                ['Sunnyvale',   37.36886,	-122.03656, 5],
                ['Los Angles',  34.05223,	-118.24368, 3],
                ['Seattle',     47.60621,	-122.33207, 2],
                ['New York',    40.71435,	-74.00597, 1]
            ];

            for (var i = 0; i < locations.length; i++) {
                var location    = locations[i];
                var title       = location[0];
                var latitude    = location[1];
                var longitude   = location[2];
                var xindex      = location[3];
                displayMarker(title, latitude, longitude, xindex);
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
        }
        //http://stackoverflow.com/questions/5058258/google-map-v3-marker-click-function-problem-and-trigger-external-link?rq=1
    });
</script>
<style type="text/css">
    .googleMapContainer {width:700px; height:500px;}
    .mapContainer {border:1px solid red;}
</style>

<script>
    $("#active-tab-2").click(function() {
        $( "#tabs" ).tabs({ active: 1 });
    });
</script>

<c:if test="${!empty itemExpenses}">
<script>
    $(function () {
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
                                        url: '${pageContext.request.contextPath}/expenses.htm?type=${item.key}'
                                    },
                                    <c:set var="first" value="true"/>
                                </c:when>
                                <c:otherwise>
                                    {
                                        name: '${item.key}',
                                        y: ${item.value},
                                        sliced: false,
                                        selected: false,
                                        url: '${pageContext.request.contextPath}/expenses.htm?type=${item.key}'
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
                                    url: '${pageContext.request.contextPath}/expenses.htm?type=${item.key}'
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
        document.getElementById("inviteEmailId").style.background=color
    }

    function submitInvitationForm() {
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
                    '403' : "Forbidden resouce can't be accessed",
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
                    message="Unknow Error \n.";
                }
                $(this).css("display","inline");
                $(this).html(message);
            });
    }
</script>

</body>
</html>