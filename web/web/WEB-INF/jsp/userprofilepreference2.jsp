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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin1.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script>
        $(function () {
            $("#tabs").tabs({
                beforeLoad: function (event, ui) {
                    ui.jqXHR.error(function () {
                        ui.panel.html(
                                "Couldn't load this tab. We'll try to fix this as soon as possible. " +
                                "If this wouldn't be a demo.");
                    });
                }
            });
        });
    </script>
    <script src="static/js/classie.js"></script>
    <script>
        function init() {
            window.addEventListener('scroll', function (e) {
                var distanceY = window.pageYOffset || document.documentElement.scrollTop,
                        shrinkOn = 300,
                        header = document.querySelector("header");
                if (distanceY > shrinkOn) {
                    classie.add(header, "smaller");
                } else {
                    if (classie.has(header, "smaller")) {
                        classie.remove(header, "smaller");
                    }
                }
            });
        }
        window.onload = init();
    </script>

</head>
<body>
<div class="main_wrapper">
    <div class="header">
        <div class="header_wrapper">
            <div class="header_left_content">
                <div id="logo">
                    <h1><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" href="#">LOG OUT</a>
                <a class="top-account-bar-text" href="/access/eval/feedback.htm">FEEDBACK</a>
                <a class="top-account-bar-text" href="/access/userprofilepreference/i.htm">PROFILE</a>
                <a class="top-account-bar-text" href="#">REPORT</a>
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
    <div class="sidebar_no_use">
    </div>
    <div class="rightside-content">
        <div id="tabs" class="nav-list">
            <ul class="nav-block">
                <li><a href="#tabs-1">PROFILE</a></li>
                <li><a href="#tabs-2">PREFERENCES</a></li>
            </ul>

            <div id="tabs-1" class="report_my ajx-content ui-tabs-panel ui-widget-content ui-corner-bottom" aria-labelledby="ui-id-4" role="tabpanel" aria-hidden="false" style="display: block;">
                <h1 class="h1">PROFILE</h1>
                <hr>
                <div class="photo_section">
                    <div class="photo_part">
                        <h2 class="h2">Photo</h2>
                        <div class="pic"></div>
                    </div>
                    <div class="photo_button">
                        <input type="button" value="TAKE NEW PHOTO" style="background:#0079FF" class="read_btn">
                        <input type="button" value="UPLOAD IMAGE" style="background:#0079FF;margin: 39px 96px 0px 0px;" class="read_btn">
                    </div>
                </div>
                <div class="down_form">
                    <form>
                        <div class="row_field">
                            <label class="profile_label">First name</label>
                            <input type="text" name="" required="true" size="20" class="name_txt"
                                    value="<spring:eval expression="userProfilePreferenceForm.userProfile.firstName"/>">
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Last name</label>
                            <input type="text" required="true" size="20" name="" class="name_txt"
                                    value="<spring:eval expression="userProfilePreferenceForm.userProfile.lastName"/>">
                        </div>
                        <div class="row_field">
                            <label class="profile_label">Email address</label>
                            <input type="text" name="" size="20" class="name_txt"
                                    value="<spring:eval expression="userProfilePreferenceForm.userProfile.email"/>">
                        </div>
                        <input type="button" value="SAVE" style="background:#0079FF" class="read_btn">
                    </form>
                </div>
            </div>

            <div id="tabs-2" class="ajx-content report_my ">
                <h1 class="h1">PREFERENCES</h1>
                <hr>
                <h2 class="h2" style="padding-bottom:2%;">Tags</h2>
                <div class="">
                    <input type="button" value="Home &nbsp; &times;" style="" class="white_btn">
                    <input type="button" value="Home1 &nbsp; &times;" style="" class="white_btn">
                    <input type="button" value="Home2 &nbsp; &times;" style="" class="white_btn">
                    <input type="button" value="Home3 &nbsp; &times;" style="" class="white_btn">
                </div>
                <h3 class="h3 padtop2per" style="padding-top:25px;color:#0079FF">&#43; ADD TAG</h3>
                <input type="text" placeholder="New tag" name="" size="20" class="tag_txt">

                <div class="full">
                    <input type="button" value="SAVE" style="background:#0079FF; margin-top:126px;" class="read_btn">
                </div>
            </div>
        </div>
    </div>
    <div class="footer-tooth clearfix">
        <div class="footer-tooth-middle"></div>
        <div class="footer-tooth-right"></div>
    </div>
</div>
</body>
</html>