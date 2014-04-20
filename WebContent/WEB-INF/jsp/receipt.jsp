<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
	<title><fmt:message key="receipt.title" /></title>

    <link rel="icon" type="image/x-icon" href="../static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../static/images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../static/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script type="text/javascript" src="../static/jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../static/jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="../static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>

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
        $.ajaxSetup ({
            cache: false
        });

        $(document).focusout(function() {
            "use strict";

            $( "#notes" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        type: "POST",
                        url: '${pageContext. request. contextPath}/ncws/rn.htm',
                        data: JSON.stringify({
                            notes: request.term,
                            receiptId: $("#receiptId").val()
                        }),
                        contentType: 'application/json;charset=utf-8',
                        mimeType: 'application/json',
                        dataType:'json',
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
            "use strict";

            $( "#recheckComment" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        type: "POST",
                        url: '${pageContext. request. contextPath}/ncws/rc.htm',
                        data: JSON.stringify({
                            notes: request.term,
                            receiptId: $("#receiptId").val()
                        }),
                        contentType: 'application/json;charset=utf-8',
                        mimeType: 'application/json',
                        dataType:'json',
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
            "use strict";

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

            $('.timestamp').cuteTime({ refresh: 10000 });
            blinkDownloadIcon();
        });

        function blinkDownloadIcon(){
            $('.downloadIconBlink').delay(100).fadeTo(100,0.5).delay(100).fadeTo(100,1, blinkDownloadIcon);
        }

        $(function() {
            $('.expensofiItem').change(updateExpensofiItemList);
        });

        var items;
        function updateExpensofiItemList() {
            items = $('.expensofiItem:checked').map(function() {
                return this.value
            }).get();
        }

        $(function() {
            $("#expensofi_button").click(
                function() {
                    if(items && items.length > 0) {
                        var jsonItems = {items:items};

                        $.ajax({
                            type: 'POST',
                            url: '${pageContext. request. contextPath}/expensofi/items.htm',
                            data: JSON.stringify(jsonItems),
                            dataType: 'json',
                            beforeSend: function() {
                                $('#download_expense_excel').html(
                                    "<div class='spinner small' id='spinner'></div>"
                                ).show();
                            },
                            success: function(data) {
                                console.log(data.filename);
                                if(data.filename.length > 0) {
                                    $('#download_expense_excel').html(
                                        "<input type='button' value='Expensofi' name='expensofi' id='expensofi_button' class='btn btn-default' />" +
                                        "&nbsp;&nbsp;&nbsp;" +
                                        "<a href='${pageContext.request.contextPath}/filedownload/expensofi/${receiptForm.receipt.id}.htm'>" +
                                            "<img src='../static/images/download_icon_lg.png' width='30' height='32' class='downloadIconBlink'>" +
                                        "</a>"
                                    ).show();
                                }
                            },
                            complete: function() {
                                //no need to remove spinner as it is removed during show $('#spinner').remove();
                                blinkDownloadIcon();
                            }
                        });
                    } else {
                        alert("Please select a checkbox to generate expense report");
                    }
                }
            );

            $(document).ready(function () {
                $('#select_expense_all').click(function () {
                    $('.expensofiItem').prop('checked', isChecked('select_expense_all'));
                    updateExpensofiItemList();
                });
            });
        });

        function isChecked(checkboxId) {
            var id = '#' + checkboxId;
            return $(id).is(":checked");
        }

        function resetSelectItemExpenseAll() {
            if ($(".expensofiItem").length == $(".expensofiItem:checked").length) {
                $("#select_expense_all").attr("checked", "checked");
            } else {
                $("#select_expense_all").removeAttr("checked");
            }

            if ($(".expensofiItem:checked").length > 0) {
                $('#edit').attr("disabled", false);
            } else {
                $('#edit').attr("disabled", true);
            }

            updateExpensofiItemList();
        }
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="../static/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
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
                                <img src="../static/images/gear.png" width="18px" height="15px" style="float: right;"/>
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
                            <td colspan="5">
                                <div style="text-align: center; font-size: 15px">
                                    <b><spring:eval expression="receiptForm.receipt.bizName.businessName" /></b>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
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
                            <th><input type="checkbox" id="select_expense_all"/></th>
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
                                <td style="text-align: center; width: 1px">
                                    <input type="checkbox" value="${item.id}" class="expensofiItem" onclick="resetSelectItemExpenseAll();" />
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
                                                <fmt:formatNumber value="${item.price}" type="currency" pattern="###,###.####" /> each
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: right;">
                                    <spring:eval expression="item.totalPriceWithoutTax" />
                                </td>
                                <td style="text-align: left;">
                                    <spring:eval expression="item.taxed == T(com.receiptofi.domain.types.TaxEnum).TAXED" var="isValid" />
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
                                    <form:select path="items[${status.index}].expenseTag.id" id="itemId">
                                        <form:option value="NONE" label="--- Select ---" />
                                        <form:options items="${receiptForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                                    </form:select>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td style="text-align: right;" colspan="3">
                                Sub Total
                            </td>
                            <td style="text-align: right;"><fmt:formatNumber value="${receiptForm.receipt.total - receiptForm.receipt.tax}" type="currency" /></td>
                            <td style="text-align: right;">&nbsp;</td>
                            <td style="text-align: right;">&nbsp;</td>
                        </tr>
                        <tr>
                            <td style="text-align: right; white-space: nowrap;" colspan="3">
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
                            <td colspan="5">
                                <div class="leftAlign" id="download_expense_excel">
                                    <input type="button" value="Expensofi" name="expensofi" id="expensofi_button" class="btn btn-default" />
                                    &nbsp;
                                    <c:if test="${!empty receiptForm.receipt.expenseReportInFS}">
                                        <a href="${pageContext.request.contextPath}/filedownload/expensofi/${receiptForm.receipt.id}.htm">
                                            <img src="../static/images/download_icon_lg.png" class="downloadIcon" width="30" height="32">
                                        </a>
                                    </c:if>
                                </div>
                                <div class="rightAlign"><input type="submit" value="Re-Check" name="re-check" class="btn btn-default" /></div>
                                <div class="rightAlign">&nbsp;&nbsp;</div>
                                <div class="rightAlign"><input type="submit" value="Delete" name="delete" class="btn btn-danger" /></div>
                            </td>
                            <td>
                                <div class="leftAlign"><input type="submit" value="Update Expense Type" name="update-expense-type" class="btn btn-default" /></div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
                                <form:label for="receipt.notes.text" path="receipt.notes.text" cssErrorClass="error">
                                    Receipt Notes:
                                </form:label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
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
                            <td colspan="6">
                                <form:errors path="receipt.notes.text" cssClass="error" />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
                                <form:label for="receipt.recheckComment.text" path="receipt.recheckComment.text" cssErrorClass="error">
                                    Re-Check message:
                                </form:label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="6">
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
                            <td colspan="6">
                                <form:errors path="receipt.recheckComment.text" cssClass="error" />
                            </td>
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
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
</div>

<script>
    $(function() {
        "use strict";

        $("#notes").blur();
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
            <c:forEach items="${receiptForm.receipt.fileSystemEntities}" var="arr" varStatus="status">
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