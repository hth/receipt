<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/admin/landing.htm">Receiptofi</a></h1>
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
                            <sec:authentication property="principal.username"/>
                            <span class="notification-counter">1</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text user-email" href="#">
                            <sec:authentication property="principal.username"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<header>
</header>
<div class="main clearfix" style="width: 1560px;">
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            Edit Business or Store information
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: 1000px; width: 1560px;">
        <c class="down_form" style="width: 1530px;">
            <h2 class="h2" style="padding-bottom:5px; text-decoration: underline;">Edit Business or Store information</h2>
            <form:form method="post" modelAttribute="bizForm" action="../businessSearch.htm">
                <form:hidden path="nameId" />
                <form:hidden path="addressId" />

                <c:if test="${!empty bizForm.errorMessage}">
                    <div class="r-error" style="width: 98%; margin: 0 0 0 0;">
                        <c:out value="${bizForm.errorMessage}" />
                    </div>
                </c:if>

                <c:if test="${!empty bizForm.successMessage}">
                    <div class="r-success" style="width: 98%; margin: 0 0 0 0;">
                        <c:out value="${bizForm.successMessage}" />
                    </div>
                </c:if>

                <spring:hasBindErrors name="bizForm">
                    <div class="r-validation" style="width: 98%; margin: 0 0 0 0;">
                        <c:if test="${errors.hasFieldErrors('businessName')}">
                            <form:errors path="businessName" /><br>
                        </c:if>
                        <c:if test="${errors.hasFieldErrors('address')}">
                            <form:errors path="address" /><br>
                        </c:if>
                        <c:if test="${errors.hasFieldErrors('phone')}">
                            <form:errors path="phone" /><br>
                        </c:if>
                    </div>
                </spring:hasBindErrors>

                <div class="row_field">
                    <label class="profile_label">Name</label>
                    <form:input path="businessName" id="businessName" class="name_txt" cssStyle="width: 690px;"/>
                </div>
                <c:if test="${not empty bizForm.addressId}">
                <div class="row_field">
                    <label class="profile_label">Address</label>
                    <form:input path="address" id="address" class="name_txt" cssStyle="width: 690px;"/>
                </div>
                <div class="row_field">
                    <label class="profile_label">Phone</label>
                    <form:input path="phone" id="phone" class="name_txt" cssStyle="width: 690px;"/>
                </div>
                </c:if>

                <input type="submit" value="Edit" name="edit" class="gd-button" style="width: 150px; margin: 20px 5px 10px 0;" name="search" />
                <input type="submit" value="Delete Store" name="delete_store" class="gd-button" style="width: 150px; margin: 20px 5px 10px 0;" name="add" />
                <input type="submit" value="Search Business" name="search" class="gd-button" style="width: 150px; margin: 20px 5px 10px 0;" name="reset" />
            </form:form>

            <c:if test="${!empty bizForm.last10BizStore}">
            <div class="row_field">
                Last 10 records for same business. Search is limited to just 10 records.
            </div>
            <div class="rightside-list-holder" style="width: 1520px; min-height: 40px; height: 40px; overflow-y: hidden; margin-bottom: 0;">
                <ul>
                    <li style="width: 1485px;">
                        <span class="rightside-li-date-text" style="width: 20px;"></span>
                        <a href="#" class="rightside-li-middle-text" style="width: 20px;"></a>
                        <a href="#" class="rightside-li-middle-text" style="width: 350px;">Store Name</a>
                        <a href="#" class="rightside-li-middle-text" style="width: 655px;">Address</a>
                        <span class="rightside-li-right-text" style="width: 60px;">Lat</span>
                        <span class="rightside-li-right-text" style="width: 60px;">Lng</span>
                        <span class="rightside-li-right-text" style="width: 130px;">Phone</span>
                        <span class="rightside-li-right-text" style="width: 160px;">Created</span>
                    </li>
                </ul>
            </div>
            <div class="rightside-list-holder mouseScroll" style="width: 1520px;">
                <ul>
                    <c:forEach var="bizStore" items="${bizForm.last10BizStore}"  varStatus="status">
                        <li style="width: 1485px;">
                            <span class="rightside-li-date-text" style="width: 20px;">${status.count}</span>
                            <a href="#" class="rightside-li-middle-text" style="width: 20px; padding-left: 0; !important;" target="_blank">
                                <img src="${pageContext.request.contextPath}/static/images/search-icon-small.png" style="width: 20px; height: 20px; margin-top: 20px;">
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/businessSearch/edit.htm?nameId=${bizStore.bizName.id}&storeId="
                                    class="rightside-li-middle-text" style="width: 350px;" target="_blank">
                                <spring:eval expression="bizStore.bizName.businessName" /> &nbsp;(<spring:eval expression="bizForm.receiptCount.get(bizStore.id)" />)
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/businessSearch/edit.htm?nameId=${bizStore.bizName.id}&storeId=${bizStore.id}"
                                    class="rightside-li-middle-text" style="width: 655px;" target="_blank">
                                <spring:eval expression="bizStore.address" />
                            </a>
                            <span class="rightside-li-date-text" style="width: 60px;"><spring:eval expression="bizStore.lat" /></span>
                            <span class="rightside-li-date-text" style="width: 60px;"><spring:eval expression="bizStore.lng" /></span>
                            <span class="rightside-li-date-text" style="width: 130px;" title="<spring:eval expression="bizStore.phone"/>"><spring:eval expression="bizStore.phoneFormatted"/></span>
                            <span class="rightside-li-date-text" style="width: 160px;"><fmt:formatDate value="${bizStore.created}" type="both" /></span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
            </c:if>
        </div>
    </div>
    <div class="footer-tooth clearfix" style="width: 1560px;">
        <div class="footer-tooth-middle" style="width: 1558px;"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED. (<fmt:message key="build.version" />)
    </div>
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