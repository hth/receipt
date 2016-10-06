<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <sec:authorize access="hasRole('ROLE_ADMIN')" var="isAdmin" />
    <sec:authorize access="hasRole('ROLE_BUSINESS')" var="isBusiness" />
    <sec:authorize access="hasRole('ROLE_ENTERPRISE')" var="isEnterprise" />
    <sec:authorize access="hasRole('ROLE_SUPERVISOR')" var="isSupervisor" />
    <sec:authorize access="hasRole('ROLE_TECHNICIAN')" var="isTechnician" />
    <sec:authorize access="hasRole('ROLE_USER')" var="isUser" />

    <title><fmt:message key="feedback.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/raty/jquery.raty.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
</head>
<body>
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

                <c:choose>
                    <c:when test="${isAdmin}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:when test="${isBusiness}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                    </c:when>
                    <c:when test="${isEnterprise}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                    </c:when>
                    <c:when test="${isSupervisor}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:when test="${isTechnician}">
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:when>
                    <c:otherwise>
                        <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                        <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">ACCOUNT</a>
                        <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT & ANALYSIS</a>
                        <a class="top-account-bar-text" href="/access/split.htm">SPLIT EXPENSES</a>
                    </c:otherwise>
                </c:choose>
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
            <fmt:message key="feedback.title" />
        </h1>
    </div>
    <div class="rightside-list-holder full-list-holder" style="height: 650px;">
        <form:form method="post" action="feedback.htm" modelAttribute="evalFeedbackForm" enctype="multipart/form-data">
            <form:hidden path="rating" />

            <spring:hasBindErrors name="evalFeedbackForm">
                <div class="r-validation">
                <c:if test="${errors.hasFieldErrors('comment')}">
                    <form:errors path="comment" /><br/>
                </c:if>
                <c:if test="${errors.hasFieldErrors('fileData')}">
                    <form:errors path="fileData" /><br/>
                </c:if>
                </div>
            </spring:hasBindErrors>

            <h2 class="h2" style="padding-bottom:2%;">We would love to hear from you!</h2>
            <form:textarea path="comment" id="comment" cols="54" rows="5" placeholder="Comment" cssStyle="font-size: 1.2em; "/>
            <br/>
            <span class="si-general-text remaining-characters">
                <span id="feedbackComment"></span> characters remaining
            </span>
            <br/>

            <h2 class="h2" style="padding-bottom: 2%; padding-top: 2%">Attachment</h2>
            <div id="choose_file" class="read_btn choose_file_btn" onclick="getFile()">CHOOSE FILE</div>
            <div style='height: 0;width: 0; overflow:hidden;'>
                <form:input path="fileData" type="file" value="upload" onchange="sub(this)"/>
            </div>
            <br/><br/>

            <h2 class="h2" style="padding-bottom:2%; padding-top: 2%">Please rate us!</h2>
            <div id="star"></div>

            <div class="gd-button-holder">
                <button class="gd-button">SUBMIT</button>
            </div>
        </form:form>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
        $('#comment').NobleCount('#feedbackComment', {
            on_negative: 'error',
            on_positive: 'success',
            max_chars: 250
        });
    });
    $('#star').raty({
        score: $('#rating').val(),
        cancel: true,
        size: 25,
        hints: ['Bad', 'Poor', 'Regular', 'Good', 'Gorgeous']
    });

    $("#star > img").click(function(){
        var score = $(this).attr("alt");
        $('#rating').val(score);
    });

    function getFile(){
        document.getElementById("fileData").click();
    }

    function sub(obj){
        var file = obj.value;
        var fileName = file.split("\\");
        if(fileName[fileName.length-1].length > 40) {
            fileName = fileName[fileName.length-1].substring(0, 37) + "...";
        } else {
            fileName = fileName[fileName.length-1];
        }
        document.getElementById("choose_file").innerHTML = fileName;
        document.evalFeedbackForm.submit();
        event.preventDefault();
    }
</script>
</html>