<%@ include file="/WEB-INF/jsp/include.jsp"%>
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
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/cute-time/jquery.cuteTime.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.0.4/highcharts.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
</head>
<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" href="#">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">PROFILE</a>
                <a class="top-account-bar-text" href="/access/reportAnalysis.htm">REPORT</a>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
<div class="report_sidebar">
    <div class="sidebar-top-summary">
        <div class="sidebar-top-summary-upper clearfix">
            <ul>
                <li>
                    <a class="ll-t" href="#">2014</a>
                </li>
                <li>
                    <a class="ll-t" href="#">AUGUST</a>
                </li>
                <li>
                    <a class="ll-t" href="#">JULY $243.83</a>
                </li>
                <li>
                    <a class="ll-t" href="#">JUNE</a>
                </li>
                <li>
                    <a class="ll-t" href="#">APRIL</a>
                </li>
                <li>
                    <a class="ll-t" href="#">MARCH</a>
                </li>
                <li>
                    <a class="ll-t" href="#">FEBRUARY</a>
                </li>
                <li>
                    <a class="ll-t" href="#">JANUARY</a>
                </li>
                <li>
                    <a class="ll-t" href="#">2013</a>
                </li>
            </ul>
        </div>
    </div>
</div>

<div class="rightside-content">
    <div id="tabs" class="nav-list">
        <ul class="nav-block">
            <li><a href="#tab1">OVERVIEW</a></li>
            <li><a href="#tab2">FIRST</a></li>
        </ul>
        <div id="tab1" class="ajx-content">
            <div class="rightside-title">
                <h1 class="rightside-title-text left">
                    JULY 2014 <span style="color: #007aff;">$243.83</span>
                </h1>
            </div>
            <div class="rightside-list-holder" id="receiptListId">
                <ul>
                    <li>
                        <span class="rightside-li-date-text">JULY 20, 2014</span>
                        <a class="rightside-li-middle-text" href="#">Some& Some</a>
                        <span class="rightside-li-right-text">$121.00</span>
                    </li>
                    <li>
                        <span class="rightside-li-date-text">JULY 15, 2014</span>
                        <a class="rightside-li-middle-text" href="#">Express</a>
                        <span class="rightside-li-right-text">$22.90</span>
                    </li>
                    <li>
                        <span class="rightside-li-date-text">JULY 10, 2014</span>
                        <a class="rightside-li-middle-text" href="#">OLA</a>
                        <span class="rightside-li-right-text">$57.96</span>
                    </li>
                    <li>
                        <span class="rightside-li-date-text">JULY 03, 2014</span>
                        <a class="rightside-li-middle-text" href="#">Dee</a>
                        <span class="rightside-li-right-text">$23.75</span>
                    </li>
                    <li>
                        <span class="rightside-li-date-text">JULY 02, 2014</span>
                        <a class="rightside-li-middle-text" href="#">Some Data</a>
                        <span class="rightside-li-right-text">$14.02</span>
                    </li>
                    <li>
                        <span class="rightside-li-date-text">JULY 01, 2014</span>
                        <a class="rightside-li-middle-text" href="#">Collection Collective</a>
                        <span class="rightside-li-right-text">$4.20</span>
                    </li>
                </ul>
            </div>
        </div>
        <div id="tab2" class="first ajx-content">
            <img style="margin-top: 5px;" width="3%;" src="${pageContext.request.contextPath}/static/img/cross_circle.png"/>

            <p><strong>No data here submitted for August 2014</strong></p>
        </div>
    </div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
</div>
<!-- cd-popup -->
<%--<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css">--%>
<!-- Resource style -->
<script src="${pageContext.request.contextPath}/static/js/modernizr.js"></script>
<!-- Modernizr -->
<script src="${pageContext.request.contextPath}/static/js/mainpop.js"></script>
<!-- Resource jQuery -->
</body>
</html>
