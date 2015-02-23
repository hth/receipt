<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="receipt.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>

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
                        url: '${pageContext. request. contextPath}/ws/nc/rn.htm',
                        beforeSend: function(xhr) {
                            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
                        },
                        data: JSON.stringify({
                            notes: request.term,
                            receiptId: $("#receiptId").val()
                        }),
                        contentType: 'application/json;charset=UTF-8',
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

        $(document).ready(function () {
            "use strict";

            $('#notes').NobleCount('#notesCount', {
                on_negative: 'error',
                on_positive: 'okay',
                max_chars: 250
            });

            $('.timestamp').cuteTime({refresh: 10000});
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
                                url: '${pageContext. request. contextPath}/access/expensofi/items.htm',
                                data: JSON.stringify(jsonItems),
                                dataType: 'json',
                                beforeSend: function(xhr) {
                                    xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
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
                                                "<a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receiptForm.receipt.id}.htm'>" +
                                                "<img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png' width='30' height='32' class='downloadIconBlink'>" +
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

            $("#receiptExpenseTagId").change(
                    function() {
                        $.ajax({
                            type: "POST",
                            url: '${pageContext. request. contextPath}/ws/r/updateReceiptExpenseTag.htm',
                            beforeSend: function(xhr) {
                                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
                            },
                            data: {
                                receiptId: $("#receiptId").val(),
                                expenseTagId: $(this).val()
                            },
                            mimeType: 'application/json',
                            dataType:'json',
                            success: function(data) {
                                console.log(data);
                                if(data === true) {
                                    //TODO update items drop down
                                }
                            },
                            error: function(data) {
                                console.log(data);
                            }
                        })
                    }
            );

            $(document).on('change', '#itemId',
                    function () {
                        $.ajax({
                            type: "POST",
                            url: '${pageContext. request. contextPath}/ws/r/updateItemExpenseTag.htm',
                            beforeSend: function(xhr) {
                                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
                            },
                            data: {
                                itemId: $('input:hidden[name="' + $(this).attr("name").split(".")[0] + ".id" + '"]').val(),
                                expenseTagId: $(this).val()
                            },
                            mimeType: 'application/json',
                            dataType:'json',
                            success: function(data) {
                                console.log("update item expense tag successfully");
                            },
                            error: function(data) {
                                console.log(data);
                            }
                        })
                    }
            );

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
<div class="clear"></div>
<div class=" is-visible" role="alert">
    <div class="detail-view-container" style="box-shadow:none; overflow: hidden;">

        <form:form method="post" action="../receipt.htm" modelAttribute="receiptForm">
        <form:hidden path="receipt.id" id="receiptId"/>
        <form:hidden path="receipt.notes.id"/>
        <form:hidden path="receipt.notes.version"/>
        <div style="float:left;width:55%;margin-right: 3%;">
            <h1 class="h1"><fmt:formatDate pattern="MMMM dd, yyyy" value="${receiptForm.receipt.receiptDate}"/>
                <span style="color: #6E6E6E;font-weight: normal;"><fmt:formatDate value="${receiptForm.receipt.receiptDate}" type="time"/></span>
            </h1>
            <hr style="width: 100%;">
            <div class="mar10px">
                <h1 class="font3em"><spring:eval expression="receiptForm.receipt.bizName.businessName" /></h1>
                <p class="address">
                    <spring:eval expression="receiptForm.receipt.bizStore.addressWrapped"/>
                    <br />
                    <spring:eval expression="receiptForm.receipt.bizStore.phoneFormatted"/>
                </p>
            </div>
            <div class="detailHead">
                <h1 class="font2em" style="margin-left: 5px;">Map-93 <span class="colorblue right"><spring:eval expression="receiptForm.receipt.total" /></span></h1>
            </div>
            <div class="receipt-detail-holder border">
                <table width="100%" style="margin-left: 4px; margin-right: 4px">
                    <tr style="border-bottom: 1px dotted #919191;">
                        <th class="receipt-item-check"><input type="checkbox" id="select_expense_all"/></th>
                        <th class="rightside-li-date-text" style="width: 25px">&nbsp;</th>
                        <th style="vertical-align: middle">
                            <div class="receipt-tag" style="float: left;">
                                <select id="actionId" name="action" style="width: 140px;  background: #FFFFFF url('/static/images/select_down.png') no-repeat 90% 50%; background-size: 15px 15px;">
                                    <option value="NONE">ACTION</option>
                                    <option value="expenseReport">EXPENSE REPORT</option>
                                    <option value="recheck">RE-CHECK</option>
                                </select>
                            </div>
                        </th>
                        <th class="receipt-tag" style="margin-left: -5px;">
                            <form:select path="receipt.expenseTag.id" id="receiptExpenseTagId">
                                <form:option value="NONE" label="SELECT" />
                                <form:options items="${receiptForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                            </form:select>
                        </th>
                        <th class="receipt-li-price-text"></th>
                        <th class="receipt-li-price-text"></th>
                    </tr>
                    <c:forEach items="${receiptForm.items}" var="item" varStatus="status">
                    <form:hidden path="items[${status.index}].id"/>
                    <tr style="border-bottom: 1px dotted #919191;">
                        <td class="receipt-item-check">
                            <input type="checkbox" value="${item.id}" class="expensofiItem" onclick="resetSelectItemExpenseAll();" />
                        </td>
                        <td class="rightside-li-date-text" style="width: 25px">
                            ${status.count}.
                        </td>
                        <td class="receipt-item-name">
                            <c:choose>
                                <c:when test="${item.quantity eq 1}">
                                    <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">${item.name}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">${item.name}</a>
                                    <div>
                                        ${item.quantity} @ <fmt:formatNumber value="${item.price}" type="currency" pattern="###,###.####" /> each
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="receipt-tag">
                            <form:select path="items[${status.index}].expenseTag.id" id="itemId">
                                <form:option value="NONE" label="SELECT" />
                                <form:options items="${receiptForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                            </form:select>
                        </td>
                        <td class="receipt-li-price-text">
                            <spring:eval expression="item.taxed == T(com.receiptofi.domain.types.TaxEnum).TAXED" var="isValid" />
                            <c:choose>
                                <c:when test="${!isValid}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <spring:eval expression="item.totalTax"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="receipt-li-price-text">
                            <spring:eval expression="item.totalPriceWithoutTax" />
                        </td>
                    </tr>
                    </c:forEach>
                    <tr>
                        <td class="receipt-item-check"></td>
                        <td class="rightside-li-date-text" style="width: 25px"></td>
                        <td class="receipt-item-name" style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">Sub Total</td>
                        <td class="receipt-tag" style="background: none;"></td>
                        <td class="receipt-li-price-text"></td>
                        <td class="receipt-li-price-text"><spring:eval expression="receiptForm.receipt.subTotal" /></td>
                    </tr>
                    <tr>
                        <td class="receipt-item-check"></td>
                        <td class="rightside-li-date-text" style="width: 25px"></td>
                        <td class="receipt-item-name" style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">Tax</td>
                        <td class="receipt-tag" style="background: none;"></td>
                        <td class="receipt-li-price-text"><spring:eval expression="receiptForm.receipt.tax" /></td>
                        <td class="receipt-li-price-text"></td>
                    </tr>
                    <tr style="border-bottom: 1px solid #919191;">
                        <td class="receipt-item-check"></td>
                        <td class="rightside-li-date-text" style="width: 25px"></td>
                        <td class="receipt-item-name" style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">Grand Total</td>
                        <td class="receipt-tag" style="background: none;"></td>
                        <td class="receipt-li-price-text"></td>
                        <td class="receipt-li-price-text"><spring:eval expression="receiptForm.receipt.total" /></td>
                    </tr>
                </table>

                <div style="padding-left: 10px">
                    <h2 class="h2" style="padding-bottom:2%; margin-top: 14px;">Receipt notes</h2>
                    <form:textarea path="receipt.notes.text" id="notes" cols="54" rows="5" placeholder="Write receipt notes here..." cssStyle="font-size: 1.2em;"/>
                    <br/>
                    <span class="si-general-text remaining-characters"><span id="notesCount"></span> characters remaining.</span>
                    <c:choose>
                        <c:when test="${!empty receiptForm.receipt.notes.id}">
                            <span id="savedNotes" class="si-general-text remaining-characters">
                                Saved - <span class="timestamp"><fmt:formatDate value="${receiptForm.receipt.notes.updated}" type="both"/></span>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span id="savedNotes" class="si-general-text remaining-characters"></span>
                        </c:otherwise>
                    </c:choose>
                    <br/>
                    <form:errors path="receipt.notes.text" cssClass="first first-small ajx-content" />

                    <input type="submit" value="DELETE" class="read_btn" name="delete"
                            style="background:#FC462A; margin: 77px 10px 0px 0px;" />
                    <input type="submit" value="RE-CHECK" class="read_btn" name="re-check"
                            style="margin: 77px 10px 0px 0px;" />
                </div>
            </div>
        </div>
        <div style="width:38%;float: left;padding-top: 4%;">
            <img style="width: 390px;height: 590px;padding-left: 8%;" src="static/img/details.JPG"/>
        </div>
        </form:form>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#64; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>

<script>
    $(function() {
        "use strict";

        $("#notes").blur();
    });
</script>
</body>
</html>