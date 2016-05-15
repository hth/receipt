<%@ include file="include.jsp"%>
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
    <c:choose>
        <c:when test="${!empty receiptForm.receipt}">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css"/>
            <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/sweetalert/0.5.0/sweet-alert.css"/>

            <script src="//cdnjs.cloudflare.com/ajax/libs/sweetalert/0.5.0/sweet-alert.min.js"></script>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
        </c:otherwise>
    </c:choose>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.3/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>

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

        $(document).focusout(function() {
            "use strict";

            $( "#recheckComment" ).autocomplete({
                source: function (request, response) {
                    $.ajax({
                        type: "POST",
                        url: '${pageContext. request. contextPath}/ws/nc/rc.htm',
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
            $("#actionId").on('change',
                    function() {
                        if (this.value === 'expenseReport') {
                            $("#notesContainer").show();
                            $("#deleteBtnId").show();
                            $('[id^="itemCell"]').show();
                            $("#recheckContainer").hide();
                            $("#recheckBtnId").hide();

                            if (items && items.length > 0) {
                                var jsonItems = {items: items};

                                $.ajax({
                                    type: 'POST',
                                    url: '${pageContext. request. contextPath}/access/expensofi/items.htm',
                                    data: JSON.stringify(jsonItems),
                                    dataTypes: "application/json",
                                    beforeSend: function(xhr) {
                                        xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
                                        $('#download_expense_excel').html(
                                                "<span class='spinner medium-small' id='spinner'>&nbsp;&nbsp;&nbsp;&nbsp;</span>"
                                        ).show();
                                    },
                                    success: function(data) {
                                        console.log(data.filename);
                                        if(data.filename.length > 0) {
                                            $('#download_expense_excel').html(
                                                    "<a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receiptForm.receipt.id}.htm'>" +
                                                    "<img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png' width='26' height='26' title='Download Expense Report' class='downloadIconBlink'>" +
                                                    "</a>"
                                            ).show();
                                        }
                                    },
                                    complete: function() {
                                        //no need to remove spinner as it is removed during show $('#spinner').remove();
                                        blinkDownloadIcon();
                                        $("#actionId").val($("#actionId option:first").val());
                                    }
                                });
                            } else {
                                alert("Please select a checkbox to generate expense report");
                                $("#actionId").val($("#actionId option:first").val());
                            }
                        } else if (this.value === 'recheck') {
                            $("#notesContainer").hide();
                            $("#deleteBtnId").hide();
                            $('[id^="itemCell"]').not('#itemCell1').hide();
                            $("#recheckContainer").show();
                            $("#recheckBtnId").show();
                        } else {
                            $("#notesContainer").show();
                            $("#deleteBtnId").show();
                            $('[id^="itemCell"]').show();
                            $("#recheckContainer").hide();
                            $("#recheckBtnId").hide();
                        }
                    }
            );

            $(document).ready(function () {
                $('#select_expense_all').click(function () {
                    $('.expensofiItem').prop('checked', isChecked('select_expense_all'));
                    updateExpensofiItemList();
                });

                <c:if test="${!empty receiptForm.receipt.expenseReportInFS}">
                $('#download_expense_excel').html(
                        "<a href='${pageContext.request.contextPath}/access/filedownload/expensofi/${receiptForm.receipt.id}.htm'>" +
                        "<img src='${pageContext.request.contextPath}/static/images/download_icon_lg.png' width='26' height='26' title='Download Expense Report' class='downloadIcon'>" +
                        "</a>"
                ).show();
                </c:if>
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
                                if(data.success === true) {
                                    $('.noClassItem').each(function () {
                                        $(this).val($("#receiptExpenseTagId").val());
                                    });
                                    $("#expenseTagColorId").css({'background-color' : data.tagColor})
                                } else {
                                    console.log("Response data is empty. This is error.");
                                }
                            },
                            error: function(data) {
                                console.error(data);
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
                            dataType:'json'
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

        $(document).ready(function () {
            confirmBeforeAction();
            $('#actionMessageId').attr('hidden', true).removeClass("temp_offset");
        })
    </script>
</head>
<body>
<c:choose>
<c:when test="${!empty receiptForm.receipt}">
<div class="clear"></div>
<div>
    <div class="temp_offset" id="actionMessageId">
        <div class="header_main">
            <div class="header_wrappermain">
                <div class="header_wrapper">
                    <div class="header_left_contentmain">
                        <div id="logo">
                            <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/access/landing.htm">Receiptofi</a></h1>
                        </div>
                    </div>
                    <div class="header_right_login">
                        <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                            <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                                <input type="submit" value="LOG OUT" class="logout_btn"/>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            </form>
                        </a>
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                        <sec:authentication var="validated" property="principal.accountValidated"/>
                        <c:choose>
                            <c:when test="${!validated}">
                                <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                                    <sec:authentication property="principal.username" />
                                    <span class="notification-counter">1</span>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a class="top-account-bar-text user-email" href="#">
                                    <sec:authentication property="principal.username" />
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <header>
        </header>
        <div class="main clearfix">
            <div class="rightside-title rightside-title-less-margin">
                <h1 class="rightside-title-text">
                    Receipt
                </h1>
            </div>
            <div style="height: 605px;">
                <div class="r-success" style="display: none;"></div>
                <div class="r-error" style="display: none;"></div>
            </div>
            <div class="footer-tooth clearfix">
                <div class="footer-tooth-middle"></div>
                <div class="footer-tooth-right"></div>
            </div>
        </div>
    </div>

    <div class="detail-view-container">
        <form:form method="post" action="../receipt.htm" modelAttribute="receiptForm">
        <form:hidden path="receipt.id" id="receiptId"/>
        <form:hidden path="receipt.notes.id"/>
        <form:hidden path="receipt.notes.version"/>
        <div class="left" style="width: 560px; margin-right: 18px; margin-left: 10px">
            <c:if test="${!empty receiptForm.errorMessage}">
            <div class="r-error">
                ${receiptForm.errorMessage}
            </div>
            </c:if>

            <h1 class="h1"><fmt:formatDate pattern="MMMM dd, yyyy" value="${receiptForm.receipt.receiptDate}"/>
                <span style="color: #6E6E6E;font-weight: normal;">&nbsp;<fmt:formatDate value="${receiptForm.receipt.receiptDate}" type="time"/></span>
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
            <div class="row_field">
                <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 360px;">
                    <span style="float: left; vertical-align: middle; width: 80px;">Splits Expenses:</span>
                    <div id="splits">
                        <c:choose>
                        <c:when test="${empty receiptForm.receipt.referReceiptId}">
                            <c:forEach var="friend" items="${receiptForm.jsonSplitFriends}" varStatus="status">
                                <div class="member" style="background-color: #00529B" id="${friend.rid}"
                                        onclick="updateReceiptSplit('${friend.rid}', '${receiptForm.receipt.id}');">
                                    <span class="member-initials">${friend.initials}</span>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="friend" items="${receiptForm.jsonSplitFriends}" varStatus="status">
                                <div class="member" style="background-color: #606060" id="${friend.rid}">
                                    <span class="member-initials">${friend.initials}</span>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </label>
            </div>
            <c:if test="${empty receiptForm.receipt.referReceiptId}">
            <div class="row_field">
                <label class="profile_label" style="!important; color: #606060; !important; font-weight: normal; !important; line-height: 30px; width: 360px;">
                    <span style="float: left; vertical-align: middle; width: 80px;">Friends:</span>
                    <div id="friends">
                        <c:forEach var="friend" items="${receiptForm.jsonFriends.values()}" varStatus="status">
                        <div class="member" style="background-color: #00529B" id="${friend.rid}"
                                onclick="updateReceiptSplit('${friend.rid}', '${receiptForm.receipt.id}');">
                            <span class="member-initials">${friend.initials}</span>
                        </div>
                        </c:forEach>
                    </div>
                </label>
            </div>
            </c:if>
            <div class="detailHead">
                <table width="100%">
                    <tr>
                        <td width="220px" class="font2em" style="margin-left: 5px; vertical-align: middle;">
                            <span id="download_expense_excel">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        </td>
                        <td width="160px" class="font2em" style="text-align: right; margin-left: 5px; vertical-align: middle;">
                            <span style="background-color: ${receiptForm.receipt.expenseTag.tagColor}; margin-left: 90px;" id="expenseTagColorId">
                                <a href="/access/userprofilepreference/i.htm#tabs-2" class="expense-tag" title="Expense Tag">&nbsp;&nbsp;&nbsp;</a>
                            </span>
                        </td>
                        <td width="170px" class="font2em" style="margin-left: 5px; vertical-align: middle;">
                            <span class="colorblue right" id="my_total"><spring:eval expression="receiptForm.receipt.splitTotal" /></span>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="receipt-detail-holder border">
                <table width="100%" style="margin-left: 4px; margin-right: 4px">
                    <tr style="border-bottom: 1px dotted #919191;">
                        <th class="receipt-item-check"><input type="checkbox" id="select_expense_all"/></th>
                        <th class="rightside-li-date-text" style="width: 25px">&nbsp;</th>
                        <th style="vertical-align: middle">
                            <div class="receipt-tag left">
                                <select id="actionId" name="action" style="width: 155px; background: #FFFFFF url('/static/images/select_down.png') no-repeat 90% 50%; background-size: 15px 15px;">
                                    <option value="NONE">ACTION</option>
                                    <option value="expenseReport">EXPENSE REPORT</option>
                                    <option value="recheck">RE-CHECK RECEIPT</option>
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
                    <tr style="border-bottom: 1px dotted #919191;" id="itemCell${status.count}">
                        <td class="receipt-item-check">
                            <input type="checkbox" value="${item.id}" class="expensofiItem" onclick="resetSelectItemExpenseAll();" />
                        </td>
                        <td class="rightside-li-date-text" style="width: 25px">
                            ${status.count}.
                        </td>
                        <td class="receipt-item-name">
                            <c:choose>
                                <c:when test="${item.quantity eq 1}">
                                    <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">${item.nameAbb}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/access/itemanalytic/${item.id}.htm">${item.nameAbb}</a>
                                    <div>
                                        ${item.quantity} @ <fmt:formatNumber value="${item.price}" type="currency" pattern="###,###.####" /> each
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="receipt-tag">
                            <form:select path="items[${status.index}].expenseTag.id" id="itemId" cssClass="noClassItem">
                                <form:option value="NONE" label="SELECT" />
                                <form:options items="${receiptForm.expenseTags}" itemValue="id" itemLabel="tagName" />
                            </form:select>
                        </td>
                        <td class="receipt-li-price-text">
                            <spring:eval expression="item.taxed == T(com.receiptofi.domain.types.TaxEnum).T" var="isValid" />
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
                        <td class="receipt-li-price-text" id="gtId"><spring:eval expression="receiptForm.receipt.total" /></td>
                    </tr>
                </table>

                <div style="padding-left: 10px">

                    <div id="notesContainer">
                    <h2 class="h2" style="padding-bottom:2%; margin-top: 14px;">Receipt notes</h2>
                    <form:textarea path="receipt.notes.text" id="notes" cols="50" rows="5" placeholder="Write receipt notes here..." cssStyle="font-size: 1.2em;"/>
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
                    </div>

                    <div id="recheckContainer" style="display: none;">
                    <h2 class="h2" style="padding-bottom:2%; margin-top: 14px;">Re-Check reason</h2>
                    <form:textarea path="receipt.recheckComment.text" id="recheckComment" cols="50" rows="5" placeholder="Write receipt recheck reason here..." cssStyle="font-size: 1.2em;"/>
                    <br/>
                    <span class="si-general-text remaining-characters"><span id="recheckCount"></span> characters remaining.</span>
                    <c:choose>
                        <c:when test="${!empty receiptForm.receipt.notes.id}">
                            <span id="savedRecheckComment" class="si-general-text remaining-characters">
                                Saved - <span class="timestamp"><fmt:formatDate value="${receiptForm.receipt.recheckComment.updated}" type="both"/></span>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span id="savedRecheckComment" class="si-general-text remaining-characters"></span>
                        </c:otherwise>
                    </c:choose>
                    <br/>
                    </div>

                    <input type="submit" value="DELETE" class="read_btn" name="delete" id="deleteBtnId"
                            style="background:#FC462A; margin: 77px 10px 0px 0px;" />
                    <c:if test="${empty receiptForm.receipt.referReceiptId}">
                    <input type="submit" value="RE-CHECK" class="read_btn" name="re-check" id="recheckBtnId"
                            style="margin: 77px 10px 0px 0px; display: none;" />
                    </c:if>

                    <div style="padding-bottom: 30px;"></div>
                </div>
            </div>
        </div>
        <div class="left" style="vertical-align: top;">
            <!-- Script is called to populate div element container -->
            <div id="container" style="height: 850px"></div>
        </div>
        </form:form>
    </div>
</div>
</c:when>
<c:otherwise>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                <sec:authentication var="validated" property="principal.accountValidated"/>
                <c:choose>
                    <c:when test="${!validated}">
                        <a class="top-account-bar-text user-email" href="/access/userprofilepreference/i.htm">
                            <sec:authentication property="principal.username" />
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username" />
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<header>
</header>
<div class="main clearfix">
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            Receipt Not Found
        </h1>
    </div>
    <div style="height: 605px;">
        <div class="r-error">
            Oops! we could not find this receipt.
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
</c:otherwise>
</c:choose>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>

<script>
    $(function() {
        "use strict";

        $("#notes").blur();
    });
</script>

<!-- Loads image -->
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
                    src: "https://s3-us-west-2.amazonaws.com/<spring:eval expression="@environmentProperty.getProperty('aws.s3.bucketName')" />/<spring:eval expression="@environmentProperty.getProperty('aws.s3.bucketName')" />/${arr.key}",
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
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
</body>
</html>