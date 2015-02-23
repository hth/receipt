<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title>Receipt Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
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
            "use strict";

            $("#notes").blur();
        });
    </script>
</head>
<body>
<div class="clear"></div>
<div class=" is-visible" role="alert">
    <div class="detail-view-container" style="box-shadow:none; overflow: hidden;">

        <form:form method="post" action="../receipt.htm" modelAttribute="receiptForm">
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
                    <c:forEach items="${receiptForm.items}" var="item" varStatus="status">
                    <form:hidden path="items[${status.index}].id"/>
                    <tr>
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
                            <spring:eval expression="item.totalPriceWithoutTax" />
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
                    </tr>
                    </c:forEach>
                    <tr style="border-top: 1px dotted #919191;">
                        <td colspan="2" class="receipt-item-name">
                            Sub Total
                        </td>
                        <td class="receipt-tag" style="background: none">
                            &nbsp;
                        </td>
                        <td class="receipt-li-price-text">
                            <spring:eval expression="receiptForm.receipt.subTotal" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="receipt-item-name">
                            Tax
                        </td>
                        <td class="receipt-tag" style="background: none">
                            &nbsp;
                        </td>
                        <td class="receipt-li-price-text">
                            <spring:eval expression="receiptForm.receipt.tax" />
                        </td>
                    </tr>
                    <tr style="border-bottom: 1px solid #919191;">
                        <td colspan="2" class="receipt-item-name">
                            Grand Total
                        </td>
                        <td class="receipt-tag" style="background: none">
                            &nbsp;
                        </td>
                        <td class="receipt-li-price-text">
                            <spring:eval expression="receiptForm.receipt.total" />
                        </td>
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
                </div>

                <input type="button" value="DELETE" style="background:#FC462A;"></input>
                <input type="button" value="SAVE" style="background:#0079FF"></input>
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
</body>
</html>