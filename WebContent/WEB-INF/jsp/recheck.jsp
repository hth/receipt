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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="../../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../../jquery/js/raphael/raphael-min.js"></script>
    <script type="text/javascript" src="../../jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="../../jquery/js/cute-time/jquery.cuteTime.min.js"></script>

    <script>
        /* add background color to holder in tr tag */
        window.onload = function () {
            <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
            fetchReceiptImage('${pageContext.request.contextPath}/filedownload/receiptimage/${arr.blobId}.htm', "holder_" + ${status.index}, '${arr.id}', ${arr.imageOrientation}, '${arr.blobId}', '${receiptDocumentForm.receiptDocument.userProfileId}');
            </c:forEach>
        };

        function fetchReceiptImage(location, holder, id, angle, blobId, userProfileId) {
            document.getElementById(holder).innerHTML = "";
            var R = Raphael(holder, 930, 800);
            /* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */
            var img = R.image(location, 80, 20, 750, 750);
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
                orientation(id, -90, blobId, userProfileId);
            }).mouseover(function () {
                butt1[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt1[1].stop().attr({fill: "#000"});
            });
            butt2[2].click(function () {
                angle += 90;
                img.animate({transform: "r" + angle}, 1000, "<>");
                orientation(id, 90, blobId, userProfileId);
            }).mouseover(function () {
                butt2[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt2[1].stop().attr({fill: "#000"});
            });
            // setTimeout(function () {R.safari();});

            img.rotate(angle);
        }

        function orientation(id, angle, blobId, userProfileId) {
            $.ajax({
                url: '${pageContext. request. contextPath}/fetcher/change_fs_image_orientation.htm',
                data: {
                    fileSystemId: id,
                    orientation: angle,
                    blobId: blobId,
                    userProfileId: userProfileId
                },
                type: "POST",
                success: function (data) {
                    if(data == true) {
                        console.log("Success: Receipt_ Image Orientation Updated");
                    } else {
                        console.log("Failed: Receipt_ Image Orientation Updated");
                    }
                }
            });
        }
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

        $(document).ready(function() {
            $( "#total" ).autocomplete({
                source: function (request, response) {
                    $('#existingErrorMessage').hide();
                    $.ajax({
                        url: '${pageContext. request. contextPath}/fetcher/check_for_duplicate.htm',
                        data: {
                            date:  $("#date").val(),
                            total: $("#total").val(),
                            userProfileId: '${receiptDocumentForm.receiptDocument.userProfileId}'
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

        $(document).focusout(function() {
            $( "#recheckComment" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        url: '${pageContext. request. contextPath}/modify/receiptOCR_recheckComment.htm',
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
                <spring:eval expression="userSession.level ge T(com.receiptofi.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
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

    <h2 class="demoHeaders">Pending receipt recheck</h2>

    <c:choose>
        <c:when test="${!empty receiptDocumentForm.errorMessage}">
            <%--Currently this section of code is not executed unless the error message is added to the form directly without using 'result' --%>
            <div class="ui-widget" id="existingErrorMessage">
                <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                    <p>
                        <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <span style="display:block; width: auto">
                            ${receiptDocumentForm.errorMessage}
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

    <table>
        <tr>
            <td style="vertical-align: top;">
                <spring:eval expression="userSession.level ge T(com.receiptofi.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
                <c:choose>
                    <c:when test="${isValid}">
                    <c:choose>
                    <c:when test="${empty receiptDocumentForm.receiptDocument}">
                        Oops! Seems like user has deleted this receipt recently.
                    </c:when>
                    <c:otherwise>
                    <form:form method="post" action="../recheck.htm" modelAttribute="receiptDocumentForm">
                        <form:errors path="errorMessage"    cssClass="error" id="existingErrorMessage" />
                        <form:errors path="receiptDocument" cssClass="error" />
                        <form:hidden path="receiptDocument.id" id="receiptId"/>
                        <form:hidden path="receiptDocument.userProfileId"/>
                        <form:hidden path="receiptDocument.version"/>
                        <form:hidden path="receiptDocument.documentStatus"/>
                        <form:hidden path="receiptDocument.receiptId"/>
                        <form:hidden path="receiptDocument.notes.id"/>
                        <form:hidden path="receiptDocument.notes.version"/>
                        <form:hidden path="receiptDocument.notes.text"/>
                        <form:hidden path="receiptDocument.recheckComment.id"/>
                        <form:hidden path="receiptDocument.recheckComment.version"/>

                        <table border="0" style="width: 550px" class="etable">
                            <tr>
                                <td colspan="5">
                                    <div class="leftAlign">
                                        <form:label for="receiptDocument.bizName.name" path="receiptDocument.bizName.name" cssErrorClass="error">Biz Name</form:label>
                                        <form:input path="receiptDocument.bizName.name" id="bizName" size="52"/>
                                    </div>
                                    <div class="rightAlign">
                                        <form:label for="receiptDocument.receiptDate" path="receiptDocument.receiptDate" cssErrorClass="error">Date</form:label>
                                        <form:input path="receiptDocument.receiptDate" id="date" size="32" class="tooltip" title="Accepted Date Format: 'MM/dd/yyyy 23:59:59', or 'MM/dd/yyyy 11:59:59 PM' or 'MM/dd/yyyy'"/>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <div class="leftAlign"><form:errors path="receiptDocument.bizName.name" cssClass="error" /></div>
                                    <div class="rightAlign"><form:errors path="receiptDocument.receiptDate" cssClass="error" /></div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <div class="leftAlign">
                                        <form:label for="receiptDocument.bizStore.address" path="receiptDocument.bizStore.address" cssErrorClass="error">Address : </form:label>
                                        <form:input path="receiptDocument.bizStore.address" id="address" size="70"/>
                                    </div>
                                    <div class="rightAlign">
                                        <form:label for="receiptDocument.bizStore.phone" path="receiptDocument.bizStore.phone" cssErrorClass="error">Phone: </form:label>
                                        <form:input path="receiptDocument.bizStore.phone" id="phone" size="20"/>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th>&nbsp;</th>
                                <th style="text-align: left">&nbsp;Name</th>
                                <th style="text-align: left">&nbsp;Quantity</th>
                                <th style="text-align: left">&nbsp;Price</th>
                                <th>&nbsp;</th>
                            </tr>
                            <c:forEach items="${receiptDocumentForm.items}" varStatus="status">
                                <form:hidden path="items[${status.index}].expenseTag.id"/>
                                <tr>
                                    <td style="text-align: left">
                                        ${status.index + 1}
                                    </td>
                                    <td style="text-align: left">
                                        <form:input path="items[${status.index}].name" size="64" />
                                    </td>
                                    <td style="text-align: left">
                                        <form:input path="items[${status.index}].quantity" size="4" />
                                    </td>
                                    <td style="text-align: right">
                                        <form:input path="items[${status.index}].price" size="8" />
                                        <form:errors path="items[${status.index}].price" cssClass="error" />
                                    </td>
                                    <td>
                                        <form:select path="items[${status.index}].taxed" id="itemId">
                                            <form:option value="NONE" label="--- Select ---"/>
                                            <form:options itemValue="name" itemLabel="description" />
                                        </form:select>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td colspan="3" style="text-align: right; font-size: 12px; font-weight: bold">
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
                                <td colspan="3" style="text-align: right; vertical-align: top">
                                    <b><label id="expectedTax" style="font-size: 14px"></label></b> &nbsp;&nbsp;
                                    <form:input path="receiptDocument.tax" id="tax" size="5"/>
                                    <form:errors path="receiptDocument.tax" cssClass="error" />
                                </td>
                                <td colspan="1" style="vertical-align: top">
                                    <form:input path="receiptDocument.subTotal" id="subTotal" size="8"/>
                                    <form:errors path="receiptDocument.subTotal" cssClass="error" />
                                </td>
                                <td colspan="1" style="vertical-align: top">
                                    <form:input path="receiptDocument.total" id="total" size="8"/>
                                    <form:errors path="receiptDocument.total" cssClass="error" />
                                </td>
                            </tr>
                            <tr style="height: 6em;">
                                <td colspan="3">&nbsp;</td>
                                <td colspan="2" align="left"><input type="submit" value="Receipt Re-Check" name="recheck"/></td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <form:label for="receiptDocument.notes.text" path="receiptDocument.notes.text" cssErrorClass="error">
                                        Receipt Notes:
                                    </form:label>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <form:textarea path="receiptDocument.notes.text" id="notes" size="250" cols="50" rows="4" disabled="true"/>
                                    <br/>
                                    <span id='notesCount'></span> characters remaining.
                                    <span id="savedNotes" class="okay">Saved - <span class="timestamp"><fmt:formatDate value="${receiptDocumentForm.receiptDocument.notes.updated}" type="both"/></span></span>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <form:errors path="receiptDocument.notes.text" cssClass="error" />
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <form:label for="receiptDocument.recheckComment.text" path="receiptDocument.recheckComment.text" cssErrorClass="error">
                                        Re-Check message:
                                    </form:label>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <form:textarea path="receiptDocument.recheckComment.text" id="recheckComment" size="250" cols="50" rows="4" disabled="false"/>
                                    <br/>
                                    <span id='recheckCount'></span> characters remaining.
                                    <c:choose>
                                        <c:when test="${!empty receiptDocumentForm.receiptDocument.recheckComment.id}">
                                            <span id="savedRecheckComment" class="okay">Saved - <span class="timestamp"><fmt:formatDate value="${receiptDocumentForm.receiptDocument.recheckComment.updated}" type="both"/></span></span>
                                        </c:when>
                                        <c:otherwise>
                                            <span id="savedRecheckComment" class="okay"></span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="5">
                                    <form:errors path="receiptDocument.recheckComment.text" cssClass="error" />
                                </td>
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
            <td style="vertical-align: top;">
                <%--<div id="holder" style="height: 850px">--%>
                    <%--<c:choose>--%>
                    <%--<c:when test="${empty receiptDocumentForm.receiptDocument}">--%>
                        <%--&nbsp;--%>
                    <%--</c:when>--%>
                    <%--<c:otherwise>--%>
                        <%--<div src="" id="receiptDocument.image"></div>--%>
                    <%--</c:otherwise>--%>
                    <%--</c:choose>--%>
                <%--</div>--%>

                <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
                    <div id="holder_${status.index}" style="height: 850px; border-color:#ff0000 #0000ff;">
                            <%--<div src="" id="receipt.image"></div>--%>
                    </div>
                    <%--<div id="container" style="height: 850px"></div>--%>
                </c:forEach>
            </td>
        </tr>
    </table>

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
        $("#itemId").focus();
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
    }
    function calculateTop(imageHeight) {
        if (topHeight == 0 ) {
            return topHeight + 5;
        }
        return topHeight + imageHeight + 5;
    }

    // JSON data
    var topHeight = 0,
        info = [
            <c:forEach items="${receiptForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
            {
                src: "${pageContext.request.contextPath}/filedownload/receiptimage/${arr.blobId}.htm",
                pos: {
                    top: topHeight = calculateTop(${arr.height}),
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
        el.style.zIndex = info[i].zIndex;
        rotate(el, info[i].rotate);
        df.appendChild(el);
    }
    document.getElementById("container").appendChild(df);
</script>

</body>
</html>