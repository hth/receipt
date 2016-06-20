<%@ include file="include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="profile.title"/></title>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui-1.10.4.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/external/js/noble-count/jquery.NobleCount.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/jquery/js/clip/jquery.zclip.min.js"></script>
</head>
<body>
    Ask user to provide with email Id; first name, last name, date of birth, password; <a href="landing.htm">5 more skip remaining</a>
</body>
</html>