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
    <script type="text/javascript" src="../jquery/js/noble-count/jquery.NobleCount.min.js"></script>

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

        $(document).ready(function () {
            $('#expenseTypeId').NobleCount('#expenseTypeIdCount', {
                on_negative: 'error',
                on_positive: 'okay',
                max_chars: 6
            });
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="../images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                ${sessionScope['userSession'].emailId}
                                <img src="../images/gear.png" width="18px" height="15px" style="float: right;"/>
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

    <c:if test="${!empty userProfilePreferenceForm.errorMessage}">
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
            <p>
                <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width: auto">${userProfilePreferenceForm.errorMessage}</span>
            </p>
        </div>
    </div>
    </c:if>

    <c:if test="${!empty userProfilePreferenceForm.successMessage}">
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all alert-success" style="margin-top: 0px; padding: 0 .7em;">
            <p>
                <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width: auto">${userProfilePreferenceForm.successMessage}</span>
            </p>
        </div>
    </div>
    </c:if>

	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Profile</a></li>
			<li><a href="#tabs-2">Preferences</a></li>
		</ul>
		<div id="tabs-1">
			<form:form method="post" modelAttribute="userProfilePreferenceForm" action="update.htm">
				<form:hidden path="userProfile.id"/>
				<div class="divTable">
					<div class="divRow">
						<div class="divOfCell600">
                            Name:
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <b><spring:eval expression="userProfilePreferenceForm.userProfile.name" /></b>
                        </div>
					</div>
					<div class="divRow">
					    <div class="divOfCell600">
                        Registration:
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <b><fmt:formatDate value="${userProfilePreferenceForm.userProfile.registration}" type="both" /></b></div>
					</div>
                    <div class="divRow">
                        <div class="divOfCell600">
                            Profile changed:&nbsp;
                            <b><fmt:formatDate value="${userProfilePreferenceForm.userProfile.updated}" type="both" /></b>
                        </div>
                    </div>
                    <div class="divRow">
                        <div class="divOfCell600">&nbsp;</div>
                    </div>
                    <div class="divRow">
                        <div class="divOfCell600">
                            Auth Code:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <b><spring:eval expression="userProfilePreferenceForm.userProfile.userAuthentication.authenticationKey" /></b>
                        </div>
                    </div>
                    <div class="divRow">
                        <div class="divOfCell600">
                            Auth changed:&nbsp;
                            <b><fmt:formatDate value="${userProfilePreferenceForm.userProfile.userAuthentication.updated}" type="both" /></b>
                        </div>
                    </div>
                    <spring:eval expression="userSession.level eq T(com.tholix.domain.types.UserLevelEnum).ADMIN" var="isAdmin" />
                    <!-- If changing the access level here then update the condition check in POST method -->
                    <c:if test="${isAdmin}">
                    <div class="divRow">
                        <div class="divOfCell600">&nbsp;</div>
                    </div>
					<div class="divRow">
						<div class="divOfCell600">
                        Level: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

						<form:select path="userProfile.level" >
							<form:option value="0" label="Select Account Type" />
							<form:options itemValue="name" itemLabel="description" />
						</form:select>
						</div>
					</div>
                    <div class="divRow">
                        <div class="divOfCell600">
                        Active: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                        <form:checkbox path="active" id="active" />Active
                        </div>
                    </div>
					</c:if>
			   	</div>
			   	<div>&nbsp;</div>

                <c:if test="${isAdmin}">
                <!-- If changing the access level here then update the condition check in POST method -->
                <div class="divRow">
					<div class="divOfCell600"><input type="reset" value="Reset" name="Reset"/> <input type="submit" value="Update" name="Update"/></div>
				</div>
				</c:if>
			</form:form>
		</div>
		<div id="tabs-2">
            <div class="divTable">
                <div class="divRow">
                    <div class="divOfCell600">Account Type: <b><spring:eval expression="userProfilePreferenceForm.userPreference.accountType.description" /></b></div>
                </div>
            </div>

            <p/>

            <spring:eval expression="${userProfilePreferenceForm.userProfile.id eq sessionScope['userSession'].userProfileId}" var="isSameUser" />

            <form:form modelAttribute="expenseTypeForm" method="post" action="i.htm">
                <div style="width: 325px">
                    <section class="chunk">
                        <fieldset>
                            <legend class="hd">
                                <span class="text">Add New Expense Type</span>
                            </legend>
                            <div class="bd">
                                <div class="text">
                                    Expense Name:
                                    <form:input class="tooltip" path="expName" size="6" title="Help's mark an item with specific expense type." id="expenseTypeId"/>
                                    <input type="submit" value=" Add " name="Add" style="text-align: right;" <c:out value="${(isSameUser) ? '' : 'disabled'}"/> />&nbsp;
                                    <br/><br/>
                                </div>
                                <div class="text">
                                    <form:errors path="expName" cssClass="error" />
                                </div>
                                <div class="text">
                                    <span id='expenseTypeIdCount'></span> characters remaining.
                                </div>
                            </div>
                        </fieldset>
                    </section>
                </div>
            </form:form>

            <c:if test="${!empty userProfilePreferenceForm.expenseTypes}">
            <br/>
            <!-- Used in displaying all the Expense Tags for the user -->
            <c:choose>
                <c:when test="${userProfilePreferenceForm.visibleExpenseTypes eq 1}">
                    <p>${userProfilePreferenceForm.visibleExpenseTypes} - Expense Type is available in selection </p>
                </c:when>
                <c:when test="${userProfilePreferenceForm.visibleExpenseTypes gt 1}">
                    <p>${userProfilePreferenceForm.visibleExpenseTypes} - Expense Types are available in selection</p>
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
                <c:forEach var="expenseType" items="${userProfilePreferenceForm.expenseTypes}" varStatus="status">
                <tr>
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseType.active eq true}">
                                <c:choose>
                                    <c:when test="${isSameUser}">
                                        <a href="${pageContext.request.contextPath}/expenses.htm?type=${expenseType.expName}">
                                            <spring:eval expression="userProfilePreferenceForm.expenseTypeCount.get(expenseType.expName)" />
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:eval expression="userProfilePreferenceForm.expenseTypeCount.get(expenseType.expName)" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${isSameUser}">
                                        <del>
                                            <a href="${pageContext.request.contextPath}/expenses.htm?type=${expenseType.expName}">
                                                <spring:eval expression="userProfilePreferenceForm.expenseTypeCount.get(expenseType.expName)" />
                                            </a>
                                        </del>
                                    </c:when>
                                    <c:otherwise>
                                        <del>
                                            <spring:eval expression="userProfilePreferenceForm.expenseTypeCount.get(expenseType.expName)" />
                                        </del>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                        <c:when test="${userProfilePreferenceForm.expenseTypeCount.get(expenseType.expName) eq 0}">
                            <c:choose>
                                <c:when test="${isSameUser}">
                                    <a href="${pageContext.request.contextPath}/userprofilepreference/expenseTypeVisible.htm?id=${expenseType.id}&status=${expenseType.active}">
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
                                    <c:choose>
                                        <c:when test="${expenseType.active eq true}">
                                            Hide
                                        </c:when>
                                        <c:otherwise>
                                            Show
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
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

    <p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
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