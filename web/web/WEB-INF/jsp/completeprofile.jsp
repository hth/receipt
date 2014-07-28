<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="profile.title" /></title>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/clip/jquery.zclip.min.js"></script>
</head>
<body>
    Ask user to provide with email Id; first name, last name, date of birth, password; <a href="landing.htm">5 more skip remaining</a>
</body>
</html>