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

    <%--This makes the other JQuery fail--%>
    <%--<script type="text/javascript">--%>
    <%--$("document").ready(function(){--%>
    <%--$(".alternativeRow").btnAddRow({oddRowCSS:"oddRow",evenRowCSS:"evenRow"});--%>
    <%--$(".delRow").btnDelRow();--%>
    <%--});--%>
    <%--</script>--%>

    <script>
        /* add background color to holder in tr tag */
        window.onload = function () {
            var src = document.getElementById("receipt.image").src,
                    angle = 0;
            document.getElementById("holder").innerHTML = "";
            var R = Raphael("holder", 930, 800);
            /* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */
            var img = R.image('${pageContext.request.contextPath}/receiptimage.htm?id=${receiptOCRForm.receipt.receiptBlobId}', 80, 20, 750, 750);
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
                            extraParam: $("#bizName").val()
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
                            extraParam: $("#bizName").val()
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
</head>
<body>
<div class="wrapper">
    <div style='width:229px;'>
        <div style='width:19.25px; height: 19.25px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .05em;'>
            <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="19px" width="19px">
        </div>
        <div style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            &nbsp;&nbsp;&nbsp;
            <c:choose>
            <%--//TODO change from constant--%>
            <c:when test="${userSession.level.value ge 5}">
                <a href="${pageContext.request.contextPath}/emp/landing.htm" style="text-decoration:none;">
                    <img src="../images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                    <img src="../images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
                </a>
            </c:otherwise>
            </c:choose>

        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <br/>

    <h2 class="demoHeaders">Pending receipt recheck</h2>

    <table>
        <tr>
            <td valign="top">
                <c:choose>
                    <%--//TODO change from constant--%>
                    <c:when test="${userSession.level.value ge 5}">
                        <c:choose>
                        <c:when test="${empty receiptOCRForm.receipt}">
                            Oops! Seems like user has deleted this receipt recently.
                        </c:when>
                        <c:otherwise>
                        <form:form method="post" action="recheck.htm" modelAttribute="receiptOCRForm">
                            <form:errors path="receipt" cssClass="error" />
                            <form:hidden path="receipt.receiptBlobId"/>
                            <form:hidden path="receipt.id"/>
                            <form:hidden path="receipt.description"/>
                            <form:hidden path="receipt.userProfileId"/>
                            <form:hidden path="receipt.version"/>
                            <form:hidden path="receipt.receiptStatus"/>
                            <form:hidden path="receipt.receiptId"/>

                            <form:hidden path="receipt.receiptOCRTranslation"/>
                            <table border="0" style="width: 550px" class="etable">
                                <tr>
                                    <td colspan="4">
                                        <div class="leftAlign">
                                            <form:label for="receipt.bizName.name" path="receipt.bizName.name" cssErrorClass="error">Biz Name</form:label>
                                            <form:input path="receipt.bizName.name" id="bizName" size="52"/>
                                        </div>
                                        <div class="rightAlign">
                                            <form:label for="receipt.receiptDate" path="receipt.receiptDate" cssErrorClass="error">Date</form:label>
                                            <form:input path="receipt.receiptDate" size="32"/>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">
                                        <div class="leftAlign"><form:errors path="receipt.bizName.name" cssClass="error" /></div>
                                        <div class="rightAlign"><form:errors path="receipt.receiptDate" cssClass="error" /></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">
                                        <div class="leftAlign">
                                            <form:label for="receipt.bizStore.address" path="receipt.bizStore.address" cssErrorClass="error">Address : </form:label>
                                            <form:input path="receipt.bizStore.address" id="address" size="70"/>
                                        </div>
                                        <div class="rightAlign">
                                            <form:label for="receipt.bizStore.phone" path="receipt.bizStore.phone" cssErrorClass="error">Phone: </form:label>
                                            <form:input path="receipt.bizStore.phone" id="phone" size="20"/>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th align="left">&nbsp;</th>
                                    <th align="left">&nbsp;Name</th>
                                    <th align="left">&nbsp;Price</th>
                                    <th align="left">&nbsp;</th>
                                </tr>
                                <c:forEach items="${receiptOCRForm.items}" varStatus="status">
                                    <form:hidden path="items[${status.index}].expenseType.id"/>
                                    <tr>
                                        <td align="left">
                                                ${status.index + 1}
                                        </td>
                                        <td align="left">
                                            <form:input path="items[${status.index}].name" class="items" size="64"/>
                                        </td>
                                        <td align="right">
                                            <form:input path="items[${status.index}].price" size="16"/>
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
                                <tr>
                                    <td colspan="2" style="text-align: right; font-size: 12px; font-weight: bold">
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
                                    <td colspan="2" style="text-align: right;">
                                        <b><label id="expectedTax" name="expectedTax" style="font-size: 14px"></label></b> &nbsp;&nbsp;
                                        <form:input path="receipt.tax" id="tax" size="5"/>
                                    </td>
                                    <td colspan="1">
                                        <form:input path="receipt.subTotal" id="subTotal" size="16"/>
                                        <form:errors path="receipt.subTotal" cssClass="error" />
                                    </td>
                                    <td colspan="1">
                                        <form:input path="receipt.total" id="total" size="16"/>
                                        <form:errors path="receipt.total" cssClass="error" />
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2">&nbsp;</td>
                                    <td colspan="2" align="left"><input type="submit" value="Receipt Re-Check" name="recheck"/></td>
                                </tr>
                            </table>
                        </form:form>

                        </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        &nbsp;
                    </c:otherwise>
                </c:choose>

            </td>
            <td>&nbsp;</td>
            <td>
                <div id="holder">
                    <c:choose>
                    <c:when test="${empty receiptOCRForm.receipt}">
                        &nbsp;
                    </c:when>
                    <c:otherwise>
                        <div src="" width="700px" height="700px" id="receipt.image"></div>
                    </c:otherwise>
                    </c:choose>
                </div>
            </td>
        </tr>
    </table>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>Copyright &copy; 2013 receipt-o-fi. All Rights Reserved.</p>
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

</body>
</html>