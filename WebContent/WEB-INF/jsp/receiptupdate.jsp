<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="com.tholix.domain.types.UserLevelEnum" %>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="receipt.update" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='../jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='../jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

	<script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="../jquery/fullcalendar/fullcalendar.min.js"></script>
	<script type="text/javascript" src="../jquery/js/raphael/raphael-min.js"></script>

	<script type="text/javascript">
		$("document").ready(function(){
			$(".alternativeRow").btnAddRow({oddRowCSS:"oddRow",evenRowCSS:"evenRow"});
			$(".delRow").btnDelRow();
		});
	</script>

	<script>
		/* add background color to holder in tr tag */
        window.onload = function () {
            var src = document.getElementById("receipt.image").src,
                angle = 0;
            document.getElementById("holder").innerHTML = "";
            var R = Raphael("holder", 930, 800);
            /* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */
            var img = R.image(src, 80, 20, 750, 750);
            var butt1 = R.set(),
                butt2 = R.set();
            butt1.push(R.circle(24.833, 26.917, 26.667).attr({stroke: "#ccc", fill: "#fff", "fill-opacity": .4, "stroke-width": 2}),
                       R.path("M12.582,9.551C3.251,16.237,0.921,29.021,7.08,38.564l-2.36,1.689l4.893,2.262l4.893,2.262l-0.568-5.36l-0.567-5.359l-2.365,1.694c-4.657-7.375-2.83-17.185,4.352-22.33c7.451-5.338,17.817-3.625,23.156,3.824c5.337,7.449,3.625,17.813-3.821,23.152l2.857,3.988c9.617-6.893,11.827-20.277,4.935-29.896C35.591,4.87,22.204,2.658,12.582,9.551z").attr({stroke: "none", fill: "#000"}),
                       R.circle(24.833, 26.917, 26.667).attr({fill: "#fff", opacity: 0}));
            butt2.push(R.circle(24.833, 26.917, 26.667).attr({stroke: "#ccc", fill: "#fff", "fill-opacity": .4, "stroke-width": 2}),
                       R.path("M37.566,9.551c9.331,6.686,11.661,19.471,5.502,29.014l2.36,1.689l-4.893,2.262l-4.893,2.262l0.568-5.36l0.567-5.359l2.365,1.694c4.657-7.375,2.83-17.185-4.352-22.33c-7.451-5.338-17.817-3.625-23.156,3.824C6.3,24.695,8.012,35.06,15.458,40.398l-2.857,3.988C2.983,37.494,0.773,24.109,7.666,14.49C14.558,4.87,27.944,2.658,37.566,9.551z").attr({stroke: "none", fill: "#000"}),
                       R.circle(24.833, 26.917, 26.667).attr({fill: "#fff", opacity: 0}));
            butt1.translate(10, 181);
            butt2.translate(10, 245);
            butt1[2].click(function () {
                angle -= 90;
                img.stop().animate({transform: "r" + angle}, 1000, "<>");
            }).mouseover(function () {
                butt1[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt1[1].stop().attr({fill: "#000"});
            });
            butt2[2].click(function () {
                angle += 90;
                img.animate({transform: "r" + angle}, 1000, "<>");
            }).mouseover(function () {
                butt2[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt2[1].stop().attr({fill: "#000"});
            });
            // setTimeout(function () {R.safari();});
        };
	</script>
</head>
<body>
    <div id=?content? style='width:210px;'>
        <div id=?leftcolumn? style='width:60px; height: 16px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <c:choose>
            <%--//TODO change from constant--%>
            <c:when test="${userSession.level.value ge 5}">
                <a href="${pageContext.request.contextPath}/emp/landing.htm">
                    <img src="../images/home.png" width="10px" height="10px" alt="Home"><span>Home</span>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/landing.htm">
                    <img src="../images/home.png" width="10px" height="10px" alt="Home"><span>Home</span>
                </a>
            </c:otherwise>
            </c:choose>

        </div>
        <div id=?rightcolumn? style='width:130px; height: 16px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">${userSession.emailId}</a>
        </div>
    </div>

    <h2 class="demoHeaders">Pending receipt</h2>
	<br/>

	<table>
		<tr>
			<td valign="top">
                <c:choose>
                    <%--//TODO change from constant--%>
                    <c:when test="${userSession.level.value ge 5}">
                        <form:form method="post" action="receiptupdate.htm" modelAttribute="receiptForm">
                            <form:hidden path="receipt.receiptBlobId"/>
                            <form:hidden path="receipt.id"/>
                            <form:hidden path="receipt.userProfileId"/>
                            <form:hidden path="receipt.receiptOCRTranslation"/>
                            <form:hidden path="receipt.version"/>
                            <table border="0" style="width: 400px" class="atable">
                                <tr>
                                    <td colspan="4">
                                        <div class="leftAlign"><form:errors path="receipt.title" cssClass="error" /></div>
                                        <div class="rightAlign"><form:errors path="receipt.receiptDate" cssClass="error" /></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">
                                        <div class="leftAlign">Title <form:input path="receipt.title" size="32"/></div>
                                        <div class="rightAlign">Date <form:input path="receipt.receiptDate" size="32"/></div>
                                    </td>
                                </tr>
                                <tr>
                                    <th align="left">&nbsp;Name</th>
                                    <th align="left">&nbsp;Price</th>
                                    <th align="left">&nbsp;</th>
                                </tr>
                                <c:forEach items="${receiptForm.items}" varStatus="status">
                                    <tr>
                                        <td align="left">
                                            <form:input path="items[${status.index}].name" size="64"/>
                                        </td>
                                        <td align="right">
                                            <form:input path="items[${status.index}].price" size="16"/>
                                            <form:errors path="items[${status.index}].price" cssClass="error" />
                                        </td>
                                        <td>
                                            <form:select path="items[${status.index}].taxed">
                                                <form:option value="NONE" label="--- Select ---"/>
                                                <form:options itemValue="name" itemLabel="description" />
                                            </form:select>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td colspan="1" align="right">
                                        <span>Tax &nbsp;</span>
                                        <form:input path="receipt.tax" size="5"/>
                                        <span>&nbsp;&nbsp;Total &nbsp;</span>
                                    </td>
                                    <td align="right">
                                        <form:input path="receipt.total" size="5"/>
                                        <form:errors path="receipt.total" cssClass="error" />
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="1">&nbsp;</td>
                                    <td colspan="2" align="left"><input type="submit" value="Receipt Update" name="receipt_update"/></td>
                                </tr>
                            </table>
                        </form:form>
                    </c:when>
                    <c:otherwise>
                        &nbsp;
                    </c:otherwise>
                </c:choose>

			</td>
			<td width="6px">&nbsp;</td>
			<td valign="top">
			 	<div id="holder">
			 	<img src="${pageContext.request.contextPath}/receiptimage.htm?id=${receiptForm.receipt.receiptBlobId}" width="700px" height="700px" id="receipt.image"/>
			 	</div>
			</td>
		</tr>
	</table>
</body>
</html>