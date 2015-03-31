<%@ include file="/WEB-INF/jsp/include.jsp" %>
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

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
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
                <a class="top-account-bar-text" href="/access/signoff.htm">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
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
<div class="main clearfix">
    <div class="rightside-title rightside-title-less-margin">
        <h1 class="rightside-title-text">
            Business Search
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder" style="overflow-y: hidden; height: 800px;">
        <div class="down_form" style="width: 930px;">
            <h2 class="h2" style="padding-bottom:5px; text-decoration: underline;">Search users to change profile settings</h2>
            <form:form method="post" modelAttribute="bizForm" action="businessSearch.htm">
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
                    <label class="profile_label">
                        Name
                    </label>
                    <form:input path="businessName" id="businessName" class="name_txt"/>
                </div>
                <div class="row_field">
                    <label class="profile_label">
                        Address
                    </label>
                    <form:input path="address" id="address" class="name_txt"/>
                </div>
                <div class="row_field">
                    <label class="profile_label">
                        Phone
                    </label>
                    <form:input path="phone" id="phone" class="name_txt"/>
                </div>

                <input type="submit" value="Search Business" class="gd-button" style="width: 150px; margin: 20px 5px 10px 0px;" name="search" />
                <input type="submit" value="Add a Store or New Business" class="gd-button" style="margin: 20px 5px 10px 0px;" name="add" />
                <input type="submit" value="Reset" class="gd-button" style="width: 130px;  margin: 20px 5px 10px 0px;" name="reset" />
            </form:form>
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED. (<fmt:message key="build.version" />)
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