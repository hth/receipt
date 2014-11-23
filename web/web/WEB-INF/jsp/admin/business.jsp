<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="receipt.admin.title" /></title>

    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/js/alpixel/jMenu.jquery.css'  />

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/jquery/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/alpixel/jMenu.jquery.js"></script>

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

            $(document).ready(function() {
                $("#jMenu").jMenu();
            });
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/access/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                <sec:authentication property="principal.username" />
                                <img src="${pageContext.request.contextPath}/static/images/gear.png" width="18px" height="15px" style="float: right;"/>
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/access/userprofilepreference/i.htm">Profile And Preferences</a></li>
                                <li>
                                    <a href="#">
                                        <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                                            <input type="submit" value="Log out" class="button"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        </form>
                                    </a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/access/eval/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <ul id="jMenu">
        <li>
            <a class="fNiv">User</a>
            <ul>
                <li class="arrow"></li>
                <li>
                    <a href="landing.htm">Search</a>
                </li>
            </ul>
        </li>

        <li>
            <a class="fNiv">Business &nbsp;&nbsp;&nbsp;</a>
            <ul>
                <li class="arrow"></li>
                <li>
                    <a href="business.htm">Search & Add</a>
                </li>
            </ul>
        </li>
    </ul>

    <p>&nbsp;</p>

    <c:if test="${!empty bizForm.bizError}">
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
            <p>
                <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width: auto">${bizForm.bizError}</span>
            </p>
        </div>
    </div>
    </c:if>

    <c:if test="${!empty bizForm.bizSuccess}">
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all alert-success" style="margin-top: 0px; padding: 0 .7em;">
                <p>
                    <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                    <span style="display:block; width: auto">${bizForm.bizSuccess}</span>
                </p>
            </div>
        </div>
    </c:if>

    <h2>Add new Business or Stores to existing business</h2>
    <form:form method="post" modelAttribute="bizForm" action="business.htm">
    <table style="width: 760px" class="etable">
        <tr>
            <td style="padding: 3px;">
                <form:label for="businessName" path="businessName" cssErrorClass="error">Biz Name:</form:label>
            </td>
            <td style="padding: 3px;" colspan="2">
                <form:input path="businessName" id="businessName" class="inputForBusinessName"/>
                <form:errors path="businessName" cssClass="error" />
            </td>
        </tr>
        <tr>
            <td style="padding: 3px;">
                <form:label for="address" path="address" cssErrorClass="error">Address:</form:label>
            </td>
            <td style="padding: 3px;">
                <form:input path="address" id="address" class="inputForBusinessAddress"/>
            </td>
            <td style="padding: 3px;">
                <form:label for="phone" path="phone" cssErrorClass="error">Phone: </form:label>
                <form:input path="phone" id="phone" class="inputForBusinessPhone"/>
            </td>
        </tr>
        <c:if test="${not empty requestScope['org.springframework.validation.BindingResult.bizForm'].allErrors}">
        <tr>
            <td style="padding: 3px;">
                &nbsp;
            </td>
            <td style="padding: 3px;">
                <form:errors path="address" cssClass="error" />
            </td>
            <td style="padding: 3px;">
                <form:errors path="phone" cssClass="error" />
            </td>
        </tr>
        </c:if>
        <tr>
            <td style="padding: 3px;" colspan="3">
                <input type="submit" value="Search Business" name="search" class="btn btn-default" />
                <input type="submit" value="Add a Store or New a Business" name="add" class="btn btn-default" />
                <input type="submit" value="Reset" name="reset" class="btn btn-default" />
            </td>
        </tr>
    </table>
    </form:form>

    <c:if test="${!empty bizStore}">
    <br/>
    Added...
    <table style="width: 760px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;">Store Name</th>
            <th style="padding:3px;">Address</th>
            <th style="padding:3px;">Lat, Lng</th>
            <th style="padding:3px;">Phone</th>
            <th style="padding:3px;">Created</th>
        </tr>
        </tbody>
        <tr>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <a href="business/edit.htm?nameId=${bizStore.bizName.id}&storeId=">
                <spring:eval expression="bizStore.bizName.businessName" />
                </a>
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <a href="business/edit.htm?nameId=${bizStore.bizName.id}&storeId=${bizStore.id}">
                <spring:eval expression="bizStore.addressWrappedMore" />
                </a>
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <spring:eval expression="bizStore.lat" />, <spring:eval expression="bizStore.lng" />
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top" title="<spring:eval expression="bizStore.phone"/>">
                <spring:eval expression="bizStore.phoneFormatted"/>
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <fmt:formatDate value="${bizStore.created}" type="both" />
            </td>
        </tr>
    </table>
    </c:if>

    <c:if test="${!empty bizForm.last10BizStore}">
    <br/>
    Last 10 records for same business. Search is limited to just 10 records.
    <table style="width: 760px" class="etable">
        <tbody>
        <tr>
            <th style="padding:3px;">Store Name</th>
            <th style="padding:3px;">Address</th>
            <th style="padding:3px;">Lat, Lng</th>
            <th style="padding:3px;">Phone</th>
            <th style="padding:3px;">Created</th>
        </tr>
        </tbody>
        <c:forEach var="bizStore" items="${bizForm.last10BizStore}"  varStatus="status">
        <tr>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <a href="business/edit.htm?nameId=${bizStore.bizName.id}&storeId=">
                <spring:eval expression="bizStore.bizName.businessName" />
                </a>

                <br/><br/>
                <i>Store Referred</i> - <b><spring:eval expression="bizForm.receiptCount.get(bizStore.id)" /></b> <i>time(s) in receipt</i>
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <a href="business/edit.htm?nameId=${bizStore.bizName.id}&storeId=${bizStore.id}">
                <spring:eval expression="bizStore.addressWrappedMore" />
                </a>
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <spring:eval expression="bizStore.lat" />, <spring:eval expression="bizStore.lng" />
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top" title="<spring:eval expression="bizStore.phone"/>">
                <spring:eval expression="bizStore.phoneFormatted"/>
            </td>
            <td style="padding:3px; text-align: left; vertical-align: top">
                <fmt:formatDate value="${bizStore.created}" type="both" />
            </td>
        </tr>
        </c:forEach>
    </table>
    </c:if>

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $( "#businessName" ).autocomplete({
            source: "${pageContext. request. contextPath}/ws/r/find_company.htm"
        });

    });

    $(document).ready(function() {
        $( "#address" ).autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: '${pageContext. request. contextPath}/ws/r/find_address.htm',
                    data: {
                        term: request.term,
                        nameParam: $("#businessName").val()
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
                    url: '${pageContext. request. contextPath}/ws/r/find_phone.htm',
                    data: {
                        term: request.term,
                        nameParam: $("#businessName").val(),
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
</script>
</body>
</html>