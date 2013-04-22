<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="java.math.BigDecimal, java.util.Date" %>
<%@ page import="java.util.Map" %>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>

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

 	<div class="divTable">
		<div class="divRow">
			<div class="divOfCell200"><h3><a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">${sessionScope['userSession'].emailId}</a></h3></div>
		    <div class="divOfCell200"><h3>Total Expense: <a href="${pageContext.request.contextPath}/landing.htm#tabs-2"><fmt:formatNumber value="${total}" type="currency"/></a></h3></div>
		</div>
   	</div>

	<table>
		<tr>
			<td valign="top">
				<form:form modelAttribute="uploadReceiptImage" method="post" enctype="multipart/form-data">
					<fieldset style="width:310px;">
					    <legend>Upload Receipt</legend>

                        <c:choose>
                        <c:when test="${userSession.pendingCount gt 0}">
						<div class="ui-widget">
							<div class="ui-state-highlight ui-corner-all" style="margin-top: 5px; padding: 0 .7em;">
								<p>
                                    <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;" title="Shows number of pending receipt(s) to be processed"></span>
                                    <span style="display:block; width:310px;">
                                        Pending receipt(s) to be processed: <a href="${pageContext.request.contextPath}/receiptpending.htm"><strong>${userSession.pendingCount}</strong></a>
                                    </span>
								</p>
							</div>
						</div>
                        </c:when>
                        <c:otherwise>
                        <div class="ui-widget">
                            <div class="ui-state-highlight ui-corner-all" style="margin-top: 5px; padding: 0 .7em;">
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

					    <p>
					        <form:label for="description" path="description">
					        &nbsp;&nbsp;&nbsp;&nbsp;Description:&nbsp;
					        </form:label>
					        <form:input path="description" size="32"/>
					    </p>
					    <p>
					    	<form:errors path="description" cssClass="error" />
					    </p>

					    <p>
					        <form:label for="fileData" path="fileData">
					        Receipt Image:&nbsp;
					        </form:label>
					        <form:input path="fileData" type="file"/>
					    </p>

					    <p>
					    	<form:errors path="fileData" cssClass="error" />
					    </p>

					    <p align="center">
					        <input type="submit" value="Upload My Receipt"/>
					    </p>

					</fieldset>
			    </form:form>
			</td>
			<td>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
								<% @SuppressWarnings("unchecked") Map<Date, BigDecimal> receiptGrouped = (Map<Date, BigDecimal>) request.getAttribute("receiptGrouped"); %>
								<% if(receiptGrouped != null && receiptGrouped.size() > 0) { %>
								<% for(Date date : receiptGrouped.keySet()) { %>
								{
									title : '<%= receiptGrouped.get(date) %>',
									start : '<%= date %>',
									end   : '<%= date %>',
									url   : '${pageContext.request.contextPath}/dayreceipt?date=<%= date.toString() %>',
								} ,
								<% } %>
								<% } %>
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
	<h2 class="demoHeaders">Dashboard</h2>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Receipts</a></li>
			<li><a href="#tabs-2">Expense Analysis</a></li>
			<li><a href="#tabs-3">More</a></li>
		</ul>
		<div id="tabs-1">
			<c:if test="${receipts.size() > 0}">
			<table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
				<tbody>
					<tr style="background-color:orange;color:white;">
						<th style="padding:3px;"></th>
						<th style="padding:3px;">Title</th>
						<th style="padding:3px;">Receipt Date</th>
						<th style="padding:3px;">Tax</th>
						<th style="padding:3px;">Total</th>
					</tr>
				</tbody>
				<c:forEach var="receipt" items="${receipts}"  varStatus="status">
				<tr>
					<td style="padding:3px;" align="right">
						${status.count}
					</td>
					<td style="padding:3px;" title="${receipt.description}">
						<spring:eval expression="receipt.title" />
					</td>
					<td style="padding:3px;">
						<spring:eval expression="receipt.receiptDate" />
					</td>
					<td style="padding:3px;" align="right">
						<spring:eval expression="receipt.tax" />
					</td>
					<td style="padding:3px;" align="right">
						<a href="${pageContext.request.contextPath}/receipt.htm?id=${receipt.id}">
							<spring:eval expression="receipt.total" />
						</a>
					</td>
				</tr>
				</c:forEach>
			</table>
			</c:if>
		</div>
		<div id="tabs-2">
		    <fieldset style="width:315px;">
			    <legend>Total Expense</legend>
				<div class="divTable">
					<div class="headRow">
					   <div class="divCell">Total</div>
					   <div class="divCell">Tax</div>
					   <div class="divCell">Total without Tax</div>
					</div>
					<div class="divRow">
						<div class="divCell">${total}</div>
					    <div class="divCell">${tax}</div>
					    <div class="divCell">${totalWithoutTax}</div>
					</div>
		    	</div>
	    	</fieldset>
		</div>
		<div id="tabs-3">

		</div>
	</div>

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

    <div class="googleMapContainer" id="map-canvas"></div>
</body>
</html>