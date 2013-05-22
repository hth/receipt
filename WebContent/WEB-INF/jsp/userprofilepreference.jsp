<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="profile.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

	<script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

	<!-- For tabs -->
	<script>
		$(function() {

			$( "#accordion" ).accordion();

			var availableTags = [
				"ActionScript",
				"AppleScript",
				"Asp",
				"BASIC",
				"C",
				"C++",
				"Clojure",
				"COBOL",
				"ColdFusion",
				"Erlang",
				"Fortran",
				"Groovy",
				"Haskell",
				"Java",
				"JavaScript",
				"Lisp",
				"Perl",
				"PHP",
				"Python",
				"Ruby",
				"Scala",
				"Scheme"
			];
			$( "#autocomplete" ).autocomplete({
				source: availableTags
			});

			$( "#button" ).button();
			$( "#radioset" ).buttonset();

			$( "#tabs" ).tabs();

			$( "#dialog" ).dialog({
				autoOpen: false,
				width: 400,
				buttons: [
					{
						text: "Ok",
						click: function() {
							$( this ).dialog( "close" );
						}
					},
					{
						text: "Cancel",
						click: function() {
							$( this ).dialog( "close" );
						}
					}
				]
			});

			// Link to open the dialog
			$( "#dialog-link" ).click(function( event ) {
				$( "#dialog" ).dialog( "open" );
				event.preventDefault();
			});

			$( "#datepicker" ).datepicker({
				inline: true
			});

			$( "#slider" ).slider({
				range: true,
				values: [ 17, 67 ]
			});

			$( "#progressbar" ).progressbar({
				value: 20
			});

			// Hover states on the static widgets
			$( "#dialog-link, #icons li" ).hover(
				function() {
					$( this ).addClass( "ui-state-hover" );
				},
				function() {
					$( this ).removeClass( "ui-state-hover" );
				}
			);
		});
	</script>

    <script>
        <c:if test="${showTab != null}">
            $(function() {
                <c:if test="${showTab == '#tabs-2'}">
                    $( "#tabs" ).tabs({ active: 1 });
                </c:if>
            });
        </c:if>
    </script>
</head>
<body>
    <div id="content" style='width:210px;'>
        <div id="leftcolumn" style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div id="rightcolumn" style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <br/>

	<!-- Tabs -->
	<h2 class="demoHeaders">Profile And Preferences</h2>

	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Profile</a></li>
			<li><a href="#tabs-2">Preferences</a></li>
		</ul>
		<div id="tabs-1">
			<form:form method="post" modelAttribute="userProfile" action="update.htm">
				<form:hidden path="id"/>
				<div class="divTable">
					<div class="divRow">
						<div class="divOfCell400">Name: ${userProfile.firstName}  ${userProfile.lastName}</div>
					</div>
					<div class="divRow">
					    <div class="divOfCell400">Registration: ${userProfile.registration}</div>
					</div>
					<c:if test="${userSession.level.value > 5}">
					<div class="divRow">
						<div class="divOfCell400">Level:

						<form:select path="level" >
							<form:option value="0" label="Chose Account Type" />
							<form:options itemValue="name" itemLabel="description" />
						</form:select>
						</div>
					</div>
					</c:if>
			   	</div>
			   	<div>&nbsp;</div>

			   	<c:if test="${userSession.level.value > 5}">
			   	<div class="divRow">
					<div class="divOfCell400"><input type="reset" value="Reset" name="Reset"/> <input type="submit" value="Update" name="Update"/></div>
				</div>
				</c:if>
			</form:form>
		</div>
		<div id="tabs-2">
			<form:form modelAttribute="userPreference">
				<div class="divTable">
					<div class="divRow">
						<div class="divOfCell400">Account Type: ${userPreference.accountType.description}</div>
					</div>
					<div class="divRow">
					    <div class="divOfCell400"></div>
					</div>
			   	</div>
			</form:form>

            <br/>

            <form:form modelAttribute="expenseTypeForm" method="post" action="addExpenseType.htm">
                <form:errors path="expName" cssClass="error" />
                <form:hidden path="forYear" />
                <table border="0" style="width: 350px" class="etable">
                    <tr>
                        <td style="padding:3px;">New Expense Type <form:input path="expName" size="34" /></td>
                    </tr>
                    <tr>
                        <td style="padding:3px; text-align: right;">
                            <input type="submit" value="Add" name="Add"/>
                        </td>
                    </tr>
                </table>
            </form:form>

            <c:if test="${expenseTypes.size() > 0}">
            <br/>

            <c:choose>
                <c:when test="${visibleExpenseTypes == 1}">
                    <p>${visibleExpenseTypes} - Expense Type is available in selection </p>
                </c:when>
                <c:when test="${visibleExpenseTypes > 1}">
                    <p>${visibleExpenseTypes} - Expense Types are available in selection</p>
                </c:when>
                <c:otherwise>
                    <p>No Expense Type visible</p>
                </c:otherwise>
            </c:choose>
            <table style="width: 400px" class="etable">
                <tr>
                    <th style="padding:3px;"># Used</th>
                    <th style="padding:3px;">Show</th>
                    <th style="padding:3px;">Expense Type</th>
                    <th style="padding:3px;">Since</th>
                </tr>
                <c:forEach var="expenseType" items="${expenseTypes}" varStatus="status">
                <tr>
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseType.active == true}">
                                <spring:eval expression="expenseTypeCount.get(expenseType.expName)" />
                            </c:when>
                            <c:otherwise>
                                <del><spring:eval expression="expenseTypeCount.get(expenseType.expName)" /></del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                        <c:when test="${expenseTypeCount.get(expenseType.expName) == 0}">
                            <a href="${pageContext.request.contextPath}/userprofilepreference/expenseTypeVisible.htm?uid=${sessionScope['userSession'].userProfileId}&id=${expenseType.id}&status=${expenseType.active}">
                            <c:choose>
                                <c:when test="${expenseType.active == true}">
                                    Hide
                                </c:when>
                                <c:otherwise>
                                    Show
                                </c:otherwise>
                            </c:choose>
                            </a>
                        </c:when>
                        <c:otherwise>
                            Always Shown
                        </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px; width: 30px">
                        <c:choose>
                            <c:when test="${expenseType.active == true}">
                                <spring:eval expression="expenseType.expName" />
                            </c:when>
                            <c:otherwise>
                                <del><spring:eval expression="expenseType.expName" /></del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseType.active == true}">
                                <spring:eval expression="expenseType.forYear" />
                            </c:when>
                            <c:otherwise>
                                <del><spring:eval expression="expenseType.forYear" /></del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                </c:forEach>
            </table>
            <br/>
            Show - &nbsp;To un-hide the Expense Type click on Show <br/>
            Hide - &nbsp;&nbsp;&nbsp;To hide the Expense Type click on Hide <br/>
            Always Shown - This will be shown when the Expense Type is being used by at least one item

            </c:if>

		</div>
	</div>
</body>
</html>