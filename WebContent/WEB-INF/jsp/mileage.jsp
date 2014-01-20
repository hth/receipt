<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><fmt:message key="receipt.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="../jquery/js/cute-time/jquery.cuteTime.min.js"></script>

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
        $(function() {
            $( "#datePickerStart").datepicker({
                onSelect: function(request) {
                    $.ajax({
                        type: "POST",
                        url: '${pageContext. request. contextPath}/mws/msd.htm',
                        data: JSON.stringify({
                            id: '${mileageForm.mileage.id}',
                            msd: $("#datePickerStart").val()
                        }),
                        contentType: 'application/json;charset=utf-8',
                        mimeType: 'application/json',
                        dataType:'json',
                        success: function (data) {
                            console.log(data);
                            if(data.s === true) {
                                $("#days").text(data.d);
                            }
                        }
                    }).fail(function(xhr, status, error){
                        console.log('error:' + status + ':' + error + ':' + xhr.responseText);
                    });
                }
                });
            $( "#datePickerEnd" ).datepicker({
                onSelect: function(request) {
                    $.ajax({
                        type: "POST",
                        url: '${pageContext. request. contextPath}/mws/med.htm',
                        data: JSON.stringify({
                            id: '${mileageForm.mileage.id}',
                            med: $("#datePickerEnd").val()
                        }),
                        contentType: 'application/json;charset=utf-8',
                        mimeType: 'application/json',
                        dataType:'json',
                        success: function (data) {
                            if(data.s === true) {
                                console.log(data);
                                $("#days").text(data.d);
                            }
                        }
                    }).fail(function(xhr, status, error){
                        console.log('error:' + status + ':' + error + ':' + xhr.responseText);
                    });
                }
                });
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="../images/gear.png" width="18px" height="15px" style="float: right;"/>
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
        </div>
    </div>

    <p>&nbsp;</p>

    <c:choose>
        <c:when test="${!empty mileageForm.mileage}">
            <c:if test="${!empty mileageForm.errorMessage}">
                <div class="ui-widget">
                    <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                        <p>
                            <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                            <span style="display:block; width: auto">
                                ${mileageForm.errorMessage}
                            </span>
                        </p>
                    </div>
                </div>
            </c:if>
            <table>
                <tr>
                    <td style="vertical-align: top;">
                        <form:form method="post" action="../modv.htm" modelAttribute="mileageForm">
                            <form:hidden path="mileage.id" id="mileageId"/>

                            <table style="width: 700px" class="etable">
                                <tr>
                                    <th>Trip Starting Odometer</th>
                                    <th>Trip Ending Odometer</th>
                                    <th>Total Trip</th>
                                </tr>
                                <tr>
                                    <td style="font-size: 16px">
                                        <b><spring:eval expression='mileageForm.mileage.start' /></b>
                                        &nbsp;&nbsp; Miles
                                        <img src="../images/odometers.png" style="height: 20px; width: 20px; vertical-align: top" />
                                    </td>
                                    <td style="font-size: 16px">
                                        <b><spring:eval expression='mileageForm.mileage.end' /></b>
                                        &nbsp;&nbsp;Miles
                                        <img src="../images/odometers.png" style="height: 20px; width: 20px; vertical-align: top" />
                                    </td>
                                    <td style="font-size: 16px">
                                        <b><spring:eval expression='mileageForm.mileage.total' /></b>
                                        &nbsp;&nbsp; Miles Driven
                                        <img src="../images/car-front.png" style="height: 20px; width: 20px; vertical-align: top"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <b>Trip Start Date:</b>
                                        <input type="text" id="datePickerStart" value="<fmt:formatDate value='${mileageForm.mileage.startDate}' type="both" pattern="MM/dd/yyyy"/>" style="width: 90px">
                                    </td>
                                    <td>
                                        <b>Trip End Date:</b>
                                        <input type="text" id="datePickerEnd" value="<fmt:formatDate value='${mileageForm.mileage.endDate}' type="both" pattern="MM/dd/yyyy"/>" style="width: 90px">
                                    </td>
                                    <td><span id="days"><spring:eval expression='mileageForm.mileage.tripDays()'/></span></td>
                                </tr>
                            </table>
                        </form:form>
                    </td>
                    <td style="width: 6px;">&nbsp;</td>
                    <td style="vertical-align: top; text-align: center">
                        <div id="container" style="height: 850px"></div>
                    </td>
                </tr>
            </table>
        </c:when>
        <c:otherwise>
            <div class="ui-widget">
                <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
                    <p>
                        <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                        <span style="display:block; width:700px;">
                        No mileage found!! Please hit back button and submit a valid request
                        </span>
                    </p>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved.</p>
</div>

<script>
    $(function() {
        "use strict";

        $("#itemId").focus();
    });
</script>

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
            return topHeight + 1;
        }
        return topHeight + imageHeight + 8;
    }

    // JSON data
    var topHeight = 0,
        info = [
            <c:forEach items="${mileageForm.mileage.fileSystemEntities}" var="arr" varStatus="status">
            {
                src: "${pageContext.request.contextPath}/filedownload/receiptimage/${arr.blobId}.htm",
                pos: {
                    top: topHeight = calculateTop(300),
                    left: 0
                },
                rotate: ${arr.imageOrientation},
                zIndex: 0
            },
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
        el.style.height = "300px";
        el.style.width = "300px";
        el.style.zIndex = info[i].zIndex;
        rotate(el, info[i].rotate);
        df.appendChild(el);
    }
    document.getElementById("container").appendChild(df);
</script>

</body>
</html>