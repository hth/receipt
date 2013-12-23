<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.update" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../../images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='../../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../../jquery/css/receipt.css'>
    <link rel='stylesheet' type='text/css' href="../../jquery/fineuploader/fineuploader-3.6.3.css" />

	<script type="text/javascript" src="../../jquery/js/jquery-1.10.1.min.js"></script>
	<script type="text/javascript" src="../../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type="text/javascript" src="../../jquery/js/raphael/raphael-min.js"></script>
    <script type="text/javascript" src="../../jquery/js/dynamic_list_helper2.js"></script>
    <script type="text/javascript" src="../../jquery/fineuploader/jquery.fineuploader-3.6.3.min.js"></script>
    <script type="text/javascript" src="../../jquery/js/beatak-imageloader/jquery.imageloader.js"></script>

	<%--<script>--%>
		<%--/* add background color to holder in tr tag */--%>
        <%--window.onload = function () {--%>
            <%--var angle = '${receiptOCRForm.receiptOCR.imageOrientation}';--%>
            <%--document.getElementById("holder").innerHTML = "";--%>
            <%--var R = Raphael("holder", 930, 800);--%>
            <%--/* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */--%>
            <%--var img = R.image('${pageContext.request.contextPath}/filedownload/receiptimage/${receiptOCRForm.receiptOCR.receiptBlobId}.htm', 80, 20, 750, 750);--%>
            <%--var butt1 = R.set(),--%>
                <%--butt2 = R.set();--%>
            <%--butt1.push(R.circle(24.833, 26.917, 26.667).attr({stroke: "#ccc", fill: "#fff", "fill-opacity": .4, "stroke-width": 2}),--%>
                       <%--R.path("M12.582,9.551C3.251,16.237,0.921,29.021,7.08,38.564l-2.36,1.689l4.893,2.262l4.893,2.262l-0.568-5.36l-0.567-5.359l-2.365,1.694c-4.657-7.375-2.83-17.185,4.352-22.33c7.451-5.338,17.817-3.625,23.156,3.824c5.337,7.449,3.625,17.813-3.821,23.152l2.857,3.988c9.617-6.893,11.827-20.277,4.935-29.896C35.591,4.87,22.204,2.658,12.582,9.551z").attr({stroke: "none", fill: "#000"}),--%>
                       <%--R.circle(24.833, 26.917, 26.667).attr({fill: "#fff", opacity: 0}));--%>
            <%--butt2.push(R.circle(24.833, 26.917, 26.667).attr({stroke: "#ccc", fill: "#fff", "fill-opacity": .4, "stroke-width": 2}),--%>
                       <%--R.path("M37.566,9.551c9.331,6.686,11.661,19.471,5.502,29.014l2.36,1.689l-4.893,2.262l-4.893,2.262l0.568-5.36l0.567-5.359l2.365,1.694c4.657-7.375,2.83-17.185-4.352-22.33c-7.451-5.338-17.817-3.625-23.156,3.824C6.3,24.695,8.012,35.06,15.458,40.398l-2.857,3.988C2.983,37.494,0.773,24.109,7.666,14.49C14.558,4.87,27.944,2.658,37.566,9.551z").attr({stroke: "none", fill: "#000"}),--%>
                       <%--R.circle(24.833, 26.917, 26.667).attr({fill: "#fff", opacity: 0}));--%>
            <%--butt1.translate(10, 181);--%>
            <%--butt2.translate(10, 245);--%>
            <%--butt1[2].click(function () {--%>
                <%--angle -= 90;--%>
                <%--img.stop().animate({transform: "r" + angle}, 1000, "<>");--%>
                <%--orientation(-90);--%>
            <%--}).mouseover(function () {--%>
                <%--butt1[1].animate({fill: "#fc0"}, 300);--%>
            <%--}).mouseout(function () {--%>
                <%--butt1[1].stop().attr({fill: "#000"});--%>
            <%--});--%>
            <%--butt2[2].click(function () {--%>
                <%--angle += 90;--%>
                <%--img.animate({transform: "r" + angle}, 1000, "<>");--%>
                <%--orientation(90);--%>
            <%--}).mouseover(function () {--%>
                <%--butt2[1].animate({fill: "#fc0"}, 300);--%>
            <%--}).mouseout(function () {--%>
                <%--butt2[1].stop().attr({fill: "#000"});--%>
            <%--});--%>
            <%--// setTimeout(function () {R.safari();});--%>

            <%--img.rotate(angle);--%>
        <%--};--%>

        <%--function orientation(angle) {--%>
            <%--$.ajax({--%>
                <%--url: '${pageContext. request. contextPath}/fetcher/change_ocr_image_orientation.htm',--%>
                <%--data: {--%>
                    <%--documentId: '${receiptOCRForm.receiptOCR.id}',--%>
                    <%--orientation: angle,--%>
                    <%--userProfileId: '${receiptOCRForm.receiptOCR.userProfileId}'--%>
                <%--},--%>
                <%--type: "POST",--%>
                <%--success: function (data) {--%>
                    <%--if(data == true) {--%>
                        <%--console.log("Success: Receipt_ Image Orientation Updated");--%>
                    <%--} else {--%>
                        <%--console.log("Failed: Receipt_ Image Orientation Updated");--%>
                    <%--}--%>
                <%--}--%>
            <%--});--%>
        <%--}--%>
	<%--</script>--%>

    <script type="text/javascript">
        $(document).ready(function() {
            $( "#bizName" ).autocomplete({
                source: "${pageContext. request. contextPath}/fetcher/find_company.htm"
            });

        });

        $(document).ready(function() {
            $( "#address" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '${pageContext. request. contextPath}/fetcher/find_address.htm',
                        data: {
                            term: request.term,
                            nameParam: $("#bizName").val()
                        },
                        contentType: "*/*",
                        dataTypes: "application/json",
                        success: function (data) {
                            console.log('response=', data);
                            response(data);
                        }
                    });
                }
            });

        });

        $(document).ready(function() {
            $( "#phone" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '${pageContext. request. contextPath}/fetcher/find_phone.htm',
                        data: {
                            term: request.term,
                            nameParam: $("#bizName").val(),
                            addressParam: $("#address").val()
                        },
                        contentType: "*/*",
                        dataTypes: "application/json",
                        success: function (data) {
                            console.log('response=', data);
                            response(data);
                        }
                    });
                }
            });

        });

        $(document).ready(function() {
            $( ".items" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '${pageContext. request. contextPath}/fetcher/find_item.htm',
                        data: {
                            term: request.term,
                            nameParam: $("#bizName").val()
                        },
                        contentType: "*/*",
                        dataTypes: "application/json",
                        success: function (data) {
                            console.log('response=', data);
                            response(data);
                        }
                    });
                }
            });

        });

        $(document).ready(function() {
            $( "#total" ).autocomplete({
                source: function (request, response) {
                    $('#existingErrorMessage').hide();
                    $.ajax({
                        url: '${pageContext. request. contextPath}/fetcher/check_for_duplicate.htm',
                        data: {
                            date:  $("#date").val(),
                            total: $("#total").val(),
                            userProfileId: '${receiptOCRForm.receiptOCR.userProfileId}'
                        },
                        contentType: "*/*",
                        dataTypes: "application/json",
                        success: function (data) {
                            console.log('response=', data);
                            if(data) {
                                var html = '';
                                html = html +
                                        "<div class='ui-state-highlight ui-corner-all alert-error' style='margin-top: 0px; padding: 0 .7em;'>" +
                                            "<p>" +
                                                "<span class='ui-icon ui-icon-alert' style='float: left; margin-right: .3em;'></span>" +
                                                "<span style='display:block; width: auto'>" +
                                                    "Found pre-existing receipt with similar information for the " +
                                                    "selected date. Suggestion: Confirm the receipt data or else mark " +
                                                    "as duplicate by rejecting this receipt." +
                                                "</span>" +
                                            "</p>" +
                                        "</div>";

                                var errorMessage = document.getElementById('errorMessage');
                                errorMessage.innerHTML = html;
                            } else {
                                var errorMessage = document.getElementById('errorMessage');
                                errorMessage.innerHTML = "";
                            }
                        }
                    });
                }
            });

        });
    </script>

    <!-- For drop down menu -->
    <script>
        $(document).ready(function () {

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

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="../../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <spring:eval var="isTech" expression="userSession.level ge T(com.receiptofi.domain.types.UserLevelEnum).TECHNICIAN" />
                <c:choose>
                    <c:when test="${isTech}">
                        <h3><a href="${pageContext.request.contextPath}/emp/landing.htm" style="color: #065c14">Home</a></h3>
                    </c:when>
                    <c:otherwise>
                        <h3><a href="${pageContext.request.contextPath}/landing.htm" style="color: #065c14">Home</a></h3>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="../../images/gear.png" width="18px" height="15px" style="float: right;"/>
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
    <c:when test="${!empty receiptOCRForm.receiptOCR}">

    <spring:eval var="documentStat" expression="receiptOCRForm.receiptOCR.documentStatus == T(com.receiptofi.domain.types.DocumentStatusEnum).TURK_RECEIPT_REJECT" />
    <c:choose>
        <c:when test="${!documentStat}">
            <h2 class="demoHeaders">Document pending</h2>
        </c:when>
        <c:otherwise>
            <h2 class="demoHeaders">Document rejected</h2>
        </c:otherwise>
    </c:choose>

    <spring:eval var="isNotTech" expression="userSession.level lt T(com.receiptofi.domain.types.UserLevelEnum).TECHNICIAN" />
    <c:if test="${isNotTech}">
    <c:choose>
        <c:when test="${empty receiptOCRForm.receiptOCR.receiptId}">
        <form:form method="post" action="../delete.htm" modelAttribute="receiptOCRForm">
            <form:hidden path="receiptOCR.receiptId"/>
            <form:hidden path="receiptOCR.id"/>
            <input type="submit" value="Delete" name="delete" id="deleteId"/>
        </form:form>
        </c:when>
        <c:otherwise>
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width: auto">
                    This receipt is in the process of being Re-Checked.
                </span>
                </p>
            </div>
        </div>
        </c:otherwise>
    </c:choose>
    </c:if>

    <c:choose>
    <c:when test="${!empty receiptOCRForm.errorMessage}">
        <%--Currently this section of code is not executed unless the error message is added to the form directly without using 'result' --%>
        <div class="ui-widget" id="existingErrorMessage">
            <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width: auto">
                        ${receiptOCRForm.errorMessage}
                    </span>
                </p>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="ui-widget" id="errorMessage">

        </div>
    </c:otherwise>
    </c:choose>

    <c:if test="${isTech}">
    <div class="leftAlign">
        <form:label for="receiptOCRForm.receiptOCR.documentOfType" path="receiptOCRForm.receiptOCR.documentOfType" cssErrorClass="error">
            Document Type:
        </form:label>
        <form:select path="receiptOCRForm.receiptOCR.documentOfType" id="documentId">
            <form:option value="NONE" label="--- Select ---"/>
            <form:options itemValue="name" itemLabel="description" />
        </form:select>
    </div>
    </c:if>

    <table>
        <tr>
            <td style="vertical-align: top;">
                <c:choose>
                    <c:when test="${isTech}">
                    <div id="activeReceipt" class="hidden">
                    <form:form method="post" action="../submit.htm" modelAttribute="receiptOCRForm" id="receiptUpdateForm">
                        <form:errors path="errorMessage"    cssClass="error" id="existingErrorMessage"/>
                        <form:errors path="receiptOCR"      cssClass="error" />
                        <form:hidden path="receiptOCR.receiptBlobId"/>
                        <form:hidden path="receiptOCR.receiptScaledBlobId"/>
                        <form:hidden path="receiptOCR.id"/>
                        <form:hidden path="receiptOCR.userProfileId"/>
                        <form:hidden path="receiptOCR.version"/>
                        <form:hidden path="receiptOCR.documentStatus"/>
                        <form:hidden path="receiptOCR.receiptId"/>
                        <form:hidden path="receiptOCR.receiptOCRTranslation"/>
                        <form:hidden path="receiptOCR.receiptOf" value="EXPENSE"/>
                        <form:hidden path="receiptOCR.documentOfType" value="RECEIPT"/>

                        <table border="0" style="width: 550px" class="etable">
                            <tr>
                                <td colspan="6">
                                    <div class="leftAlign">
                                        <form:label for="receiptOCR.bizName.name" path="receiptOCR.bizName.name" cssErrorClass="error">Biz Name</form:label>
                                        <form:input path="receiptOCR.bizName.name" id="bizName" size="52"/>
                                    </div>
                                    <div class="rightAlign">
                                        <form:label for="receiptOCR.receiptDate" path="receiptOCR.receiptDate" cssErrorClass="error">Date</form:label>
                                        <form:input path="receiptOCR.receiptDate" id="date" size="32" class="tooltip" title="Accepted Date Format: 'MM/dd/yyyy 23:59:59', or 'MM/dd/yyyy 11:59:59 PM' or 'MM/dd/yyyy'"/>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="6">
                                    <div class="leftAlign"><form:errors path="receiptOCR.bizName.name" cssClass="error" /></div>
                                    <div class="rightAlign"><form:errors path="receiptOCR.receiptDate" cssClass="error" /></div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="6">
                                    <div class="leftAlign">
                                        <form:label for="receiptOCR.bizStore.address" path="receiptOCR.bizStore.address" cssErrorClass="error">Address : </form:label>
                                        <form:input path="receiptOCR.bizStore.address" id="address" size="70"/>
                                    </div>
                                    <div class="rightAlign">
                                        <form:label for="receiptOCR.bizStore.phone" path="receiptOCR.bizStore.phone" cssErrorClass="error">Phone: </form:label>
                                        <form:input path="receiptOCR.bizStore.phone" id="phone" size="20"/>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th>&nbsp;</th>
                                <th>&nbsp;</th>
                                <th style="text-align: left">&nbsp;Name</th>
                                <th style="text-align: left">&nbsp;Quantity</th>
                                <th style="text-align: left">&nbsp;Price</th>
                                <th>&nbsp;</th>
                            </tr>
                            <tbody id="itemListContainer">
                            <c:forEach items="${receiptOCRForm.items}" varStatus="status">
                                <tr class="itemRow">
                                    <td style="text-align: left">
                                        <a href="#" class="removeItem">X</a>
                                    </td>
                                    <td style="text-align: left">
                                        ${status.index + 1}
                                    </td>
                                    <td style="text-align: left">
                                        <form:input path="items[${status.index}].name" cssClass="items" size="64"/>
                                    </td>
                                    <td style="text-align: left">
                                        <form:input path="items[${status.index}].quantity" size="4" />
                                    </td>
                                    <td style="text-align: right">
                                        <form:input path="items[${status.index}].price" size="8"/>
                                        <form:errors path="items[${status.index}].price" cssClass="error" />
                                    </td>
                                    <td>
                                        <form:select path="items[${status.index}].taxed">
                                            <form:option value="NONE" label="--- Select ---"/>
                                            <form:options itemValue="name" itemLabel="description" />
                                        </form:select>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                            <tr>
                                <td colspan="6">
                                    <a href="#" id="addItemRow">Add Item Row</a>&nbsp;&nbsp;
                                </td>
                            </tr>
                            <tr>
                                <td colspan="4" style="text-align: right; font-size: 12px; font-weight: bold">
                                    <span>&nbsp;&nbsp;Tax &nbsp;</span>
                                </td>
                                <td colspan="1" style="font-size: 12px; font-weight: bold">
                                    <span class="leftAlign">&nbsp;&nbsp;Sub Total &nbsp;</span>
                                </td>
                                <td colspan="1" style="font-size: 12px; font-weight: bold">
                                    <span class="leftAlign">&nbsp;&nbsp;Total &nbsp;</span>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="4" style="text-align: right; width: 300px; vertical-align: top">
                                    <b><label id="expectedTax" style="font-size: 14px"></label></b> &nbsp;&nbsp;
                                    <form:input path="receiptOCR.tax" id="tax" size="5"/>
                                    <form:errors path="receiptOCR.tax" cssClass="error" />
                                </td>
                                <td colspan="1" style="vertical-align: top">
                                    <form:input path="receiptOCR.subTotal" id="subTotal" size="8"/>
                                    <form:errors path="receiptOCR.subTotal" cssClass="error" />
                                </td>
                                <td colspan="1" style="vertical-align: top">
                                    <form:input path="receiptOCR.total" id="total" size="8"/>
                                    <form:errors path="receiptOCR.total" cssClass="error" />
                                </td>
                            </tr>
                            <tr style="height: 6em;">
                                <td colspan="4">
                                    <input type="submit" style="color: white; background-color: darkred;" value="**   Reject   **" name="receipt-reject" id="reject" />
                                </td>
                                <td colspan="2">
                                    <input type="submit" style="color: white; background-color: darkgreen" value="   Submit   " name="receipt-submit" id="submit" />
                                </td>
                            </tr>
                        </table>
                    </form:form>
                    </div>

                    <div id="activeMileage" class="hidden">
                    <form:form method="post" action="../submitMileage.htm" modelAttribute="receiptOCRForm" id="receiptUpdateForm">
                        <form:errors path="errorMessage"    cssClass="error" id="existingErrorMessage"/>
                        <form:errors path="receiptOCR"      cssClass="error" />
                        <form:hidden path="receiptOCR.receiptBlobId"/>
                        <form:hidden path="receiptOCR.receiptScaledBlobId"/>
                        <form:hidden path="receiptOCR.id"/>
                        <form:hidden path="receiptOCR.userProfileId"/>
                        <form:hidden path="receiptOCR.version"/>
                        <form:hidden path="receiptOCR.documentStatus"/>
                        <form:hidden path="receiptOCR.receiptId"/>
                        <form:hidden path="receiptOCR.receiptOCRTranslation"/>
                        <form:hidden path="receiptOCR.receiptOf"/>
                        <form:hidden path="receiptOCR.documentOfType" value="MILEAGE"/>

                        <table border="0" style="width: 550px" class="etable">
                            <tr>
                                <td colspan="6">
                                    <div class="leftAlign">
                                        <form:label for="mileage.start" path="mileage.start" cssErrorClass="error">Begin</form:label>
                                        <form:input path="mileage.start" id="startMileage" size="25" class="tooltip" title="Mile before starting the trip"/>
                                    </div>
                                    <div class="rightAlign">
                                        <form:label for="mileage.end" path="mileage.end" cssErrorClass="error">End</form:label>
                                        <form:input path="mileage.end" id="endMileage" size="25" class="tooltip" title="Miles driven during this trip"/>
                                    </div>
                                </td>
                            </tr>
                            <tr style="height: 6em;">
                                <td colspan="4">
                                    <input type="submit" style="color: white; background-color: darkred;" value="**   Reject   **" name="mileage-reject" id="rejectMileage" />
                                </td>
                                <td colspan="2">
                                    <input type="submit" style="color: white; background-color: darkgreen" value="   Submit   " name="mileage-submit" id="submitMileage" />
                                </td>
                            </tr>
                        </table>
                    </form:form>

                    <div>&nbsp;</div><div>&nbsp;</div><div>&nbsp;</div><div>&nbsp;</div>
                    <div id="restricted-fine-uploader" style="margin-left: 10px; font-size: 1.05em" class="hidden"></div>

                    </div>
                    </c:when>
                <c:otherwise>
                    &nbsp;
                </c:otherwise>
                </c:choose>
            </td>
            <td>&nbsp;</td>
            <td style="vertical-align: top;">
                <c:choose>
                <c:when test="${empty receiptOCRForm.receiptOCR}">
                    &nbsp;
                </c:when>
                <c:otherwise>
                    <div id="container" style="height: 850px"></div>
                </c:otherwise>
                </c:choose>
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
            <c:choose>
            <c:when test="${isTech}">
                Oops! Seems like user has deleted this receipt recently.
            </c:when>
            <c:otherwise>
                No receipt found!! Please hit back button and submit a valid request
            </c:otherwise>
            </c:choose>
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
    $('#subTotal').change(function() {
        var subTotalValue = $('#subTotal').val();
        var totalValue = $('#total').val();

        if(subTotalValue != '' && subTotalValue > 0 && totalValue != '' && totalValue > 0) {
            $('#expectedTax').text('{ Calculated Tax : ' + (totalValue/subTotalValue -1).toFixed(4) + ' % }');
        }
    });
    $('#total').change(function() {
        var subTotalValue = $('#subTotal').val();
        var totalValue = $('#total').val();

        if(subTotalValue != '' && subTotalValue > 0 && totalValue != '' && totalValue > 0) {
            $('#expectedTax').text('{ Calculated Tax : ' + (totalValue/subTotalValue -1).toFixed(4) + ' % }');
        }
    });
</script>

<script>
    $(function() {
        $("#submit").focus();
    });

    $(document).ready(function() {
        $('#documentId').on('change', function (e) {
            var optionSelected = $("option:selected", this);
            var valueSelected = this.value;
            if(valueSelected == 'RECEIPT') {
                $('#activeReceipt').removeClass('hidden');
                $('#activeMileage').hide();
                $('#restricted-fine-uploader').hide();
            }

            if(valueSelected == 'INVOICE') {
                $('#activeReceipt').removeClass('hidden');
                $('#activeMileage').hide();
                $('#restricted-fine-uploader').hide();
            }

            if(valueSelected == 'MILEAGE') {
                $('#activeMileage').removeClass('hidden');
                $('#restricted-fine-uploader').removeClass('hidden');
                $('#activeReceipt').hide();
            }

            $('#documentId').prop('disabled', true);
        });
    });
</script>

<script type="text/javascript">
    // http://outbottle.com/spring-3-mvc-adding-objects-to-a-list-element-on-the-fly-at-form-submit-generic-method/
    function rowAdded(rowElement) {
        //clear the input fields for the row
        $(rowElement).find("input").val('');

        saveNeeded();
    }

    function saveNeeded() {
        //Currently does nothing
    }

    $(document).ready( function() {
        var config = {
            rowClass : 'itemRow',
            addRowId : 'addItemRow',
            removeRowClass : 'removeItem',
            formId : 'receiptUpdateForm',
            rowContainerId : 'itemListContainer',
            indexedPropertyName : 'items',
            indexedPropertyMemberNames : 'name,quantity,price,taxed',
            rowAddedListener : rowAdded
        };
        new DynamicListHelper(config);
    });
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
                    my: "left+250 center",
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
    };
    function calculateTop(imageHeight) {
        if (topHeight == 0 ) {
            return topHeight + 5;
        }
        return topHeight + imageHeight + 5;
    }

    // JSON data
    var topHeight = 0,
        info = [
            <c:forEach items="${receiptOCRForm.receiptOCR.receiptBlobId}" var="arr" varStatus="status">
            {
                src: "${pageContext.request.contextPath}/filedownload/receiptimage/${arr.blobId}.htm",
                pos: {
                    top: topHeight = calculateTop(${arr.height}),
                    left: 185
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
        el.style.zIndex = info[i].zIndex;
        rotate(el, info[i].rotate);
        df.appendChild(el);
    }
    document.getElementById("container").appendChild(df);
</script>

</body>
</html>