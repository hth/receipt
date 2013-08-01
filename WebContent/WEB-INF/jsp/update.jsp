<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.update" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

	<script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type="text/javascript" src="../jquery/js/raphael/raphael-min.js"></script>
    <script type="text/javascript" src="../jquery/js/dynamic_list_helper2.js"></script>

	<script>
		/* add background color to holder in tr tag */
        window.onload = function () {
            var src = document.getElementById("receiptOCR.image").src,
                angle = 0;
            document.getElementById("holder").innerHTML = "";
            var R = Raphael("holder", 930, 800);
            /* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */
            var img = R.image('${pageContext.request.contextPath}/receiptimage.htm?id=${receiptOCRForm.receiptOCR.receiptBlobId}', 80, 20, 750, 750);
            var butt1 = R.set(),
                butt2 = R.set();
            butt1.push(R.circle(24.833, 26.917, 26.667).attr({stroke: "#ccc", fill: "#fff", "fill-opacity": .4, "stroke-width": 2}),
                       R.path("M12.582,9.551C3.251,16.237,0.921,29.021,7.08,38.564l-2.36,1.689l4.893,2.262l4.893,2.262l-0.568-5.36l-0.567-5.359l-2.365,1.694c-4.657-7.375-2.83-17.185,4.352-22.33c7.451-5.338,17.817-3.625,23.156,3.824c5.337,7.449,3.625,17.813-3.821,23.152l2.857,3.988c9.617-6.893,11.827-20.277,4.935-29.896C35.591,4.87,22.204,2.658,12.582,9.551z").attr({stroke: "none", fill: "#000"}),
                       R.circle(24.833, 26.917, 26.667).attr({fill: "#fff", opacity: 0}));
            butt2.push(R.circle(24.833, 26.917, 26.667).attr({stroke: "#ccc", fill: "#fff", "fill-opacity": .4, "stroke-width": 2}),
                       R.path("M37.566,9.551c9.331,6.686,11.661,19.471,5.502,29.014l2.36,1.689l-4.893,2.262l-4.893,2.262l0.568-5.36l0.567-5.359l2.365,1.694c4.657-7.375,2.83-17.185-4.352-22.33c-7.451-5.338-17.817-3.625-23.156,3.824C6.3,24.695,8.012,35.06,15.458,40.398l-2.857,3.988C2.983,37.494,0.773,24.109,7.666,14.49C14.558,4.87,27.944,2.658,37.566,9.551z").attr({stroke: "none", fill: "#000"}),
                       R.circle(24.833, 26.917, 26.667).attr({fill: "#fff", opacity: 0}));
            butt1.translate(10, 181);
            butt2.translate(10, 245);
            butt1[2].click(function () {
                angle -= 90;
                img.stop().animate({transform: "r" + angle}, 1000, "<>");
            }).mouseover(function () {
                butt1[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt1[1].stop().attr({fill: "#000"});
            });
            butt2[2].click(function () {
                angle += 90;
                img.animate({transform: "r" + angle}, 1000, "<>");
            }).mouseover(function () {
                butt2[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt2[1].stop().attr({fill: "#000"});
            });
            // setTimeout(function () {R.safari();});
        };
	</script>

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
                        success: function (data) {
                            console.log('response=', data);
                            response(data);
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
                <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <spring:eval expression="userSession.level ge T(com.tholix.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
                <c:choose>
                    <c:when test="${isValid}">
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
    <c:when test="${!empty receiptOCRForm.receiptOCR}">

    <spring:eval expression="receiptOCRForm.receiptOCR.receiptStatus == T(com.tholix.domain.types.ReceiptStatusEnum).TURK_RECEIPT_REJECT" var="isValid" />
    <c:choose>
        <c:when test="${!isValid}">
            <h2 class="demoHeaders">Pending receipt</h2>
        </c:when>
        <c:otherwise>
            <h2 class="demoHeaders">Rejected receipt</h2>
        </c:otherwise>
    </c:choose>

    <spring:eval expression="userSession.level lt T(com.tholix.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
    <c:if test="${isValid}">
    <c:choose>
        <c:when test="${empty receiptOCRForm.receiptOCR.receiptId}">
        <form:form method="post" action="delete.htm" modelAttribute="receiptOCRForm">
            <form:hidden path="receiptOCR.receiptId"/>
            <form:hidden path="receiptOCR.id"/>
            <input id="deleteId" type="submit" value="Delete" name="delete"/>
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

    <c:if test="${!empty receiptOCRForm.errorMessage}">
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width: auto">
                        ${receiptOCRForm.errorMessage}
                    </span>
                </p>
            </div>
        </div>
    </c:if>

    <table>
        <tr>
            <td style="vertical-align: top;">
                <spring:eval expression="userSession.level ge T(com.tholix.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
                <c:choose>
                    <c:when test="${isValid}">
                    <form:form method="post" action="update.htm" modelAttribute="receiptOCRForm" id="receiptUpdateForm">
                        <form:errors path="receiptOCR" cssClass="error" />
                        <form:hidden path="receiptOCR.receiptBlobId"/>
                        <form:hidden path="receiptOCR.id"/>
                        <form:hidden path="receiptOCR.userProfileId"/>
                        <form:hidden path="receiptOCR.version"/>
                        <form:hidden path="receiptOCR.receiptStatus"/>
                        <form:hidden path="receiptOCR.receiptId"/>
                        <form:hidden path="receiptOCR.receiptOCRTranslation"/>
                        <table border="0" style="width: 550px" class="etable">
                            <tr>
                                <td colspan="6">
                                    <div class="leftAlign">
                                        <form:label for="receiptOCR.bizName.name" path="receiptOCR.bizName.name" cssErrorClass="error">Biz Name</form:label>
                                        <form:input path="receiptOCR.bizName.name" id="bizName" size="52"/>
                                    </div>
                                    <div class="rightAlign">
                                        <form:label for="receiptOCR.receiptDate" path="receiptOCR.receiptDate" cssErrorClass="error">Date</form:label>
                                        <form:input path="receiptOCR.receiptDate" id="date" size="32"/>
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
                                        <form:input path="items[${status.index}].name" size="64"/>
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
                                <td colspan="4" style="text-align: right; width: 300px">
                                    <b><label id="expectedTax" style="font-size: 14px"></label></b> &nbsp;&nbsp;
                                    <form:input path="receiptOCR.tax" id="tax" size="5"/>
                                </td>
                                <td colspan="1">
                                    <form:input path="receiptOCR.subTotal" id="subTotal" size="8"/>
                                    <form:errors path="receiptOCR.subTotal" cssClass="error" />
                                </td>
                                <td colspan="1">
                                    <form:input path="receiptOCR.total" id="total" size="8"/>
                                    <form:errors path="receiptOCR.total" cssClass="error" />
                                </td>
                            </tr>
                            <tr style="height: 6em;">
                                <td colspan="4">
                                    <input type="submit" style="color: white; background-color: darkred;" value="**   Reject   **" name="reject" id="reject"/>
                                </td>
                                <td colspan="2">
                                    <input type="submit" style="color: white; background-color: darkgreen" value="   Update   " name="update" id="update"/>
                                </td>
                            </tr>
                        </table>
                    </form:form>
                </c:when>
                <c:otherwise>
                    &nbsp;
                </c:otherwise>
                </c:choose>
            </td>
            <td>&nbsp;</td>
            <td style="vertical-align: top;">
                <div id="holder">
                    <c:choose>
                    <c:when test="${empty receiptOCRForm.receiptOCR}">
                        &nbsp;
                    </c:when>
                    <c:otherwise>
                        <div src="" width="700px" height="700px" id="receiptOCR.image"></div>
                    </c:otherwise>
                    </c:choose>
                </div>
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
            <spring:eval expression="userSession.level ge T(com.tholix.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
            <c:choose>
                <c:when test="${isValid}">
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
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
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
        $("#update").focus();
    });
</script>

<script type="text/javascript">
//    http://outbottle.com/spring-3-mvc-adding-objects-to-a-list-element-on-the-fly-at-form-submit-generic-method/
    function rowAdded(rowElement) {
        //clear the imput fields for the row
        $(rowElement).find("input").val('');
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

</body>
</html>