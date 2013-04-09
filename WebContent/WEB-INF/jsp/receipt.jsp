<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="receipt.title" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
	<script type="text/javascript" src="jquery/js/raphael/raphael-min.js"></script>

	<style type="text/css">
		.leftAlign {
	    	float: left;
		}
		.rightAlign {
	    	float: right;
		}
	</style>

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
            <a href="${pageContext.request.contextPath}/landing.htm">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>Home</span>
            </a>
        </div>
        <div id=?rightcolumn? style='width:130px; height: 16px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

	<br/>

	<table>
		<tr>
			<td valign="top">
				<table border="1" style="width: 400px" class="atable">
					<tr>
						<td colspan="3">
							<div class="leftAlign"><b>${receipt.title}</b></div>
							<div class="rightAlign"><b><spring:eval expression="receipt.receiptDate" /></b></div>
						</td>
					</tr>
					<tr>
						<th align="left">&nbsp;Name</th>
						<th align="left">&nbsp;Price</th>
						<th align="left">&nbsp;</th>
					</tr>
					<c:forEach items="${items}" var="item" varStatus="status">
					<tr>
						<td align="left">
							<a href="${pageContext.request.contextPath}/itemanalytic.htm?id=${item.id}">
				    		${item.name}
				    		</a>
						</td>
						<td align="right">
				    		<spring:eval expression="item.price" />
						</td>
						<td>
							${item.taxed.description}
						</td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="2" align="right">
							Sub Total &nbsp;&nbsp;&nbsp; <fmt:formatNumber value="${receipt.total - receipt.tax}" type="currency" currencySymbol="$" />
						</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td colspan="1" align="right">
							<span>Tax &nbsp;</span>
							<b><spring:eval expression="receipt.tax" /></b>
							<span>&nbsp;&nbsp;Total &nbsp;</span>
						</td>
						<td align="right">
							<b><spring:eval expression="receipt.total" /></b>
						</td>
					</tr>
					<tr>
						<form:form method="post" action="receipt.htm" modelAttribute="receiptForm">
						<form:hidden path="id" />
						<tr height="60em">
							<td colspan="3">
								<div class="rightAlign"><input type="submit" value="Receipt Delete" name="receipt_delete"/></div>
							</td>
						</tr>
						</form:form>
					</tr>
				</table>
			</td>
			<td width="6px">&nbsp;</td>
			<td valign="top">
				<div id="holder">
		 		<img src="${pageContext.request.contextPath}/receiptimage.htm?id=${receipt.receiptBlobId}" width="700px" height="700px" id="receipt.image"/>
		 		</div>
			</td>
		</tr>
	</table>


</body>
</html>