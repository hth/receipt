<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

	<script type="text/javascript" src="../jquery/js/jquery-1.10.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type="text/javascript" src="../jquery/js/raphael/raphael-min.js"></script>
    <script type="text/javascript" src="../jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="../jquery/js/cute-time/jquery.cuteTime.min.js"></script>

	<style type="text/css">
		.leftAlign {
	    	float: left;
		}
		.rightAlign {
	    	float: right;
		}
	</style>

    <script>
		/* add background color to holder in tr tag */
        window.onload = function () {
            var src = document.getElementById("receipt.image").src,
                angle = 90;
            document.getElementById("holder").innerHTML = "";
            var R = Raphael("holder", 930, 800);
            /* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */
            var img = R.image('${pageContext.request.contextPath}/receiptimage.htm?id=${receiptForm.receipt.receiptBlobId}', 80, 20, 750, 750);
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

            img.rotate(90);
        };
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

    <script>
        $(document).focusout(function() {
            $( "#notes" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '${pageContext. request. contextPath}/modify/receipt_notes.htm',
                        data: {
                            term: request.term,
                            nameParam: $("#receiptId").val()
                        },
                        success: function (data) {
                            console.log('response=', data);
                            if(data == true) {
                                var html = '';
                                html = html +   "Saved - <span class=\"timestamp\">" + $.now() + "</span>";
                                $('#savedNotes').html(html).show();
                                $('.timestamp').cuteTime({ refresh: 10000 });
                            }
                        }
                    });
                }
            });

        });

        $(document).focusout(function() {
            $( "#recheckComment" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '${pageContext. request. contextPath}/modify/receipt_recheckComment.htm',
                        data: {
                            term: request.term,
                            nameParam: $("#receiptId").val()
                        },
                        success: function (data) {
                            console.log('response=', data);
                            if(data == true) {
                                var html = '';
                                html = html +   "Saved - <span class=\"timestamp\">" + $.now() + "</span>";
                                $('#savedRecheckComment').html(html).show();
                                $('.timestamp').cuteTime({ refresh: 10000 });
                            }
                        }
                    });
                }
            });

        });

        $(document).ready(function () {
            $('#notes').NobleCount('#notesCount', {
                on_negative: 'error',
                on_positive: 'okay',
                max_chars: 250
            });
            $('#recheckComment').NobleCount('#recheckCount', {
                on_negative: 'error',
                on_positive: 'okay',
                max_chars: 250
            });
        });

        $(document).ready(function () {
            $('.timestamp').cuteTime({ refresh: 10000 });
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
    <c:when test="${!empty receiptForm.receipt}">
    <c:if test="${!empty receiptForm.errorMessage}">
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
            <p>
            <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
            <span style="display:block; width: auto">
            ${receiptForm.errorMessage}
            </span>
            </p>
        </div>
    </div>
    </c:if>
    <table>
        <tr>
            <td style="vertical-align: top;">
                <form:form method="post" action="../receipt.htm" modelAttribute="receiptForm">
                    <form:hidden path="receipt.id" id="receiptId"/>
                    <form:hidden path="receipt.notes.id"/>
                    <form:hidden path="receipt.notes.version"/>
                    <form:hidden path="receipt.recheckComment.id"/>
                    <form:hidden path="receipt.recheckComment.version"/>

                    <table style="width: 700px" class="etable">
                        <tr>
                            <td colspan="4">
                                <div style="text-align: center; font-size: 15px">
                                    <b><spring:eval expression="receiptForm.receipt.bizName.name" /></b>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <div class="leftAlign">
                                    <b><spring:eval expression="receiptForm.receipt.bizStore.addressWrappedMore"/></b>
                                </div>
                                <div class="rightAlign">
                                    <b><fmt:formatDate value="${receiptForm.receipt.receiptDate}" type="both"/></b>
                                    <p><b><spring:eval expression="receiptForm.receipt.bizStore.phoneFormatted"/></b></p>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th></th>
                            <th>Name</th>
                            <th>Price</th>
                            <th>Tax</th>
                            <th>Expense Type</th>
                        </tr>
                        <c:forEach items="${receiptForm.items}" var="item" varStatus="status">
                            <form:hidden path="items[${status.index}].id"/>
                            <tr>
                                <td style="padding: 3px; text-align: right; width: 6px">
                                    ${status.count}
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.quantity eq 1}">
                                            <a href="${pageContext.request.contextPath}/itemanalytic/${item.id}.htm">
                                                ${item.name}
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/itemanalytic/${item.id}.htm">
                                                ${item.name}
                                            </a>
                                            <div style="margin-top: 5px">
                                                ${item.quantity} @
                                                <fmt:formatNumber value="${item.price}" type="currency" pattern="###,###.####" /> each</div>
                                                <%--<spring:eval expression="item.price"/>--%>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: right;">
                                    <spring:eval expression="item.totalPriceWithoutTax" />
                                </td>
                                <td style="text-align: left;">
                                    <spring:eval expression="item.taxed == T(com.tholix.domain.types.TaxEnum).TAXED" var="isValid" />
                                    <c:choose>
                                        <c:when test="${!isValid}">
                                            &nbsp;
                                        </c:when>
                                        <c:otherwise>
                                            <spring:eval expression="item.totalTax"/> (T)
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: left;">
                                    <form:select path="items[${status.index}].expenseType.id" id="itemId">
                                        <form:option value="NONE" label="--- Select ---" />
                                        <form:options items="${receiptForm.expenseTypes}" itemValue="id" itemLabel="expName" />
                                    </form:select>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td style="text-align: right;" colspan="2">
                                Sub Total
                            </td>
                            <td style="text-align: right;"><fmt:formatNumber value="${receiptForm.receipt.total - receiptForm.receipt.tax}" type="currency" /></td>
                            <td style="text-align: right;">&nbsp;</td>
                            <td style="text-align: right;">&nbsp;</td>
                        </tr>
                        <tr>
                            <td style="text-align: right; white-space: nowrap;" colspan="2">
                                <label style="font-size: 11px">
                                    { Calculated Tax Rate : <b><spring:eval expression="receiptForm.receipt.percentTax4Display" /></b> }
                                </label>&nbsp;&nbsp;&nbsp;
                                <span>Tax &nbsp;</span>
                                <b><spring:eval expression="receiptForm.receipt.tax" /></b>
                                <span>&nbsp;&nbsp;Total</span>
                            </td>
                            <td style="text-align: right;">
                                <b><spring:eval expression="receiptForm.receipt.total" /></b>
                            </td>
                            <td style="text-align: right;">&nbsp;</td>
                            <td style="text-align: right;">&nbsp;</td>
                        </tr>
                        <tr style="height: 6em;">
                            <td colspan="4">
                                <div class="rightAlign"><input type="submit" value="Re-Check" name="re-check"/></div>
                                <div class="rightAlign">&nbsp;&nbsp;</div>
                                <div class="rightAlign"><input type="submit" value="Delete" name="delete"/></div>
                            </td>
                            <td>
                                <div class="leftAlign"><input type="submit" value="Update Expense Type" name="update-expense-type"/></div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <form:label for="receipt.notes.text" path="receipt.notes.text" cssErrorClass="error">
                                    Receipt Notes:
                                </form:label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <form:textarea path="receipt.notes.text" id="notes" size="250" cols="50" rows="4" />
                                <br/>
                                <span id='notesCount'></span> characters remaining.
                                <c:choose>
                                    <c:when test="${!empty receiptForm.receipt.notes.id}">
                                        <span id="savedNotes" class="okay">
                                            Saved - <span class="timestamp"><fmt:formatDate value="${receiptForm.receipt.notes.updated}" type="both"/></span>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span id="savedNotes" class="okay"></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <form:errors path="receipt.notes.text" cssClass="error" />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <form:label for="receipt.recheckComment.text" path="receipt.recheckComment.text" cssErrorClass="error">
                                    Re-Check message:
                                </form:label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <form:textarea path="receipt.recheckComment.text" id="recheckComment" size="250" cols="50" rows="4" />
                                <br/>
                                <span id='recheckCount'></span> characters remaining.
                                <c:choose>
                                    <c:when test="${!empty receiptForm.receipt.recheckComment.id}">
                                        <span id="savedRecheckComment" class="okay">
                                            Saved - <span class="timestamp"><fmt:formatDate value="${receiptForm.receipt.recheckComment.updated}" type="both"/></span>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span id="savedRecheckComment" class="okay"></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <form:errors path="receipt.recheckComment.text" cssClass="error" />
                            </td>
                        </tr>
                    </table>
                </form:form>
            </td>
            <td style="width: 6px;">&nbsp;</td>
            <td style="vertical-align: top;">
                <div id="holder" style="height: 850px">
                    <div src="" style="width: 700px; height: 700px" id="receipt.image"></div>
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
                No receipt found!! Please hit back button and submit a valid request
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
        $("#itemId").focus();
    });
</script>

</body>
</html>