<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="profile.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

	<script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

    <!-- For dashboard tabs -->
    <script>
        $(function () {
            $("#tabs").tabs();
        });
    </script>

    <script>
        <c:if test="${!empty showTab}">
            $(function() {
                <c:if test="${showTab eq '#tabs-2'}">
                    $( "#tabs" ).tabs({ active: 1 });
                </c:if>
            });
        </c:if>
    </script>

    <!-- For drop down menu -->
    <script>
        $(document).ready(function () {

            $(".account").click(function () {
                var X = $(this).attr('id');
                if (X == 1) {
                    $(".submenu").hide();
                    $(this).attr('id', '0');
                }
                else {
                    $(".submenu").show();
                    $(this).attr('id', '1');
                }

            });

            //Mouse click on sub menu
            $(".submenu").mouseup(function () {
                return false
            });

            //Mouse click on my account link
            $(".account").mouseup(function () {
                return false
            });

            //Document Click
            $(document).mouseup(function () {
                $(".submenu").hide();
                $(".account").attr('id', '');
            });
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50">
                <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="40px">
            </div>
            <div class="divOfCell75">
                <h3><a href="${pageContext.request.contextPath}/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown">
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="../images/gear.png" width="18px" height="15px" style="float: right;">
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li><a href="${pageContext.request.contextPath}/signoff.htm">Sign off</a></li>
                                <li><a href="${pageContext.request.contextPath}/eval/feedback.htm">Send Feedback</a></li>
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
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
						<div class="divOfCell600">
                            Name:
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <b>${userProfile.firstName}  ${userProfile.lastName}</b>
                        </div>
					</div>
					<div class="divRow">
					    <div class="divOfCell600">
                        Registration:
                        &nbsp;
                        <b>${userProfile.registration}</b></div>
					</div>
                    <div class="divRow">
                        <div class="divOfCell600">&nbsp;</div>
                    </div>
                    <div class="divRow">
                        <div class="divOfCell600">
                            Auth Code:&nbsp;&nbsp;&nbsp;&nbsp;
                            <b>${userProfile.userAuthentication.authenticationKey}</b>
                        </div>
                    </div>
                    <div class="divRow">
                        <div class="divOfCell600">
                            Last changed:
                            <b><fmt:formatDate value="${userProfile.userAuthentication.updated}" type="both" /></b>
                        </div>
                    </div>
                    <spring:eval expression="userSession.level gt T(com.tholix.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
                    <c:if test="${isValid}">
                    <div class="divRow">
                        <div class="divOfCell600">&nbsp;</div>
                    </div>
					<div class="divRow">
						<div class="divOfCell600">
                        Level:

						<form:select path="level" >
							<form:option value="0" label="Select Account Type" />
							<form:options itemValue="name" itemLabel="description" />
						</form:select>
						</div>
					</div>
					</c:if>
			   	</div>
			   	<div>&nbsp;</div>

                <spring:eval expression="userSession.level gt T(com.tholix.domain.types.UserLevelEnum).TECHNICIAN" var="isValid" />
                <c:if test="${isValid}">
			   	<div class="divRow">
					<div class="divOfCell600"><input type="reset" value="Reset" name="Reset"/> <input type="submit" value="Update" name="Update"/></div>
				</div>
				</c:if>
			</form:form>
		</div>
		<div id="tabs-2">
			<form:form modelAttribute="userPreference">
				<div class="divTable">
					<div class="divRow">
						<div class="divOfCell600">Account Type: <b>${userPreference.accountType.description}</b></div>
					</div>
			   	</div>
			</form:form>

            <p/>

            <form:form modelAttribute="expenseTypeForm" method="post" action="addExpenseType.htm">
                <form:errors path="expName" cssClass="error" />
                <form:hidden path="forYear" />
                <table border="0" style="width: 225px" class="etable">
                    <tr>
                        <td style="padding:3px;">
                            &nbsp;Add Expense Type <form:input class="tooltip" path="expName" size="6" title="Help's mark an item with specific expense type." /> <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding:3px; text-align: left;">
                            <sup>*</sup>(Max of 6 characters) <input type="submit" value="Add" name="Add" style="text-align: right;"/>&nbsp;&nbsp;
                        </td>
                    </tr>
                </table>
            </form:form>

            <c:if test="${!empty expenseTypes}">
            <br/>

            <c:choose>
                <c:when test="${visibleExpenseTypes eq 1}">
                    <p>${visibleExpenseTypes} - Expense Type is available in selection </p>
                </c:when>
                <c:when test="${visibleExpenseTypes gt 1}">
                    <p>${visibleExpenseTypes} - Expense Types are available in selection</p>
                </c:when>
                <c:otherwise>
                    <p>No Expense Type visible</p>
                </c:otherwise>
            </c:choose>
            <table style="width: 325px" class="etable">
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
                            <c:when test="${expenseType.active eq true}">
                                <a href="${pageContext.request.contextPath}/expenses.htm?type=${expenseType.expName}">
                                    <spring:eval expression="expenseTypeCount.get(expenseType.expName)" />
                                </a>
                            </c:when>
                            <c:otherwise>
                                <del>
                                <a href="${pageContext.request.contextPath}/expenses.htm?type=${expenseType.expName}">
                                    <spring:eval expression="expenseTypeCount.get(expenseType.expName)" />
                                </a>
                                </del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                        <c:when test="${expenseTypeCount.get(expenseType.expName) eq 0}">
                            <a href="${pageContext.request.contextPath}/userprofilepreference/expenseTypeVisible.htm?uid=${sessionScope['userSession'].userProfileId}&id=${expenseType.id}&status=${expenseType.active}">
                            <c:choose>
                                <c:when test="${expenseType.active eq true}">
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
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseType.active eq true}">
                                <spring:eval expression="expenseType.expName" />
                            </c:when>
                            <c:otherwise>
                                <del><spring:eval expression="expenseType.expName" /></del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseType.active eq true}">
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
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

<script>
    $(function () {
        $('.tooltip').each(function () {
            var $this, id, t;

            $this = $(this);
            id = this.id;
            t = $('<span />', {
                title: $this.attr('title')
            }).appendTo($this.parent()).tooltip({
                position: {
                    of: '#' + id,
                    my: "left+190 center",
                    at: "left center",
                    collision: "fit"
                }
            });
            // remove the title from the real element.
            $this.attr('title', '');
            $('#' + id).focusin(function () {
                t.tooltip('open');
            }).focusout(function () {
                t.tooltip('close');
            });
        });
    });
</script>

</body>
</html>