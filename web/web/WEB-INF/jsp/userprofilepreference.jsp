<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<title><fmt:message key="profile.title" /></title>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui-1.10.4.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/jquery/jquery-ui-1.10.4.custom.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/noble-count/jquery.NobleCount.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/clip/jquery.zclip.min.js"></script>

    <!-- For dashboard tabs -->
    <script>
        $(function () {
            $("#tabs").tabs();
        });

        $(document).ready(function () {
            $("#copy-button").zclip({
                path: '${pageContext.request.contextPath}/static/jquery/js/clip/ZeroClipboard.swf',
                copy: $('#auth b').text()
            });
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
            $('#expenseTagId').NobleCount('#expenseTagIdCount', {
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
                <img src="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
            </div>
            <div class="divOfCell75" style="height: 46px">
                <h3><a href="${pageContext.request.contextPath}/access/landing.htm" style="color: #065c14">Home</a></h3>
            </div>
            <div class="divOfCell250">
                <h3>
                    <div class="dropdown" style="height: 17px">
                        <div>
                            <a class="account" style="color: #065c14">
                                <sec:authentication property="principal.username" />
                                <img src="${pageContext.request.contextPath}/static/images/gear.png" width="18px" height="15px" style="float: right;"/>
                            </a>
                        </div>
                        <div class="submenu">
                            <ul class="root">
                                <li>
                                    <a href="#">
                                        <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                                            <input type="submit" value="Log out" class="button"/>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        </form>
                                    </a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/access/eval/feedback.htm">Send Feedback</a></li>
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
            <div class="divTable">
                <div class="divRow">
                    <div class="divOfCell700">
                        Name:
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <b><spring:eval expression="userProfilePreferenceForm.userProfile.name" /></b>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                        User Id: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <b><spring:eval expression="userProfilePreferenceForm.userProfile.receiptUserId" /></b>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                        Email on file: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <b><spring:eval expression="userProfilePreferenceForm.userProfile.email" /></b>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                    Registration:
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <b><fmt:formatDate value="${userProfilePreferenceForm.userProfile.created}" type="both" /></b></div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                        Profile changed:&nbsp;&nbsp;
                        <b><fmt:formatDate value="${userProfilePreferenceForm.userProfile.updated}" type="both" /></b>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">&nbsp;</div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700" id="auth">
                        ** Auth Code:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <b><spring:eval expression="userProfilePreferenceForm.userAuthentication.authenticationKeyEncoded" /></b>
                        <button id='copy-button'>copy</button>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                        Auth changed:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <b><fmt:formatDate value="${userProfilePreferenceForm.userAuthentication.updated}" type="both" /></b>
                    </div>
                </div>
            </div>
            <p>
                ** <b>Auth Code</b> is like password. Keep it secure.
            </p>

            <sec:authorize access="hasRole('ROLE_ADMIN')">
            <!-- If changing the access level here then update the condition check in POST method -->

            <div>&nbsp;</div>

            <form:form method="post" modelAttribute="userProfilePreferenceForm" action="update.htm">
            <form:hidden path="userProfile.receiptUserId"/>
            <div class="divTable">
                <div class="divRow">
                    <div class="divOfCell700">
                        Profile Id: &nbsp;&nbsp;&nbsp;&nbsp;
                        <b><spring:eval expression="userProfilePreferenceForm.userProfile.receiptUserId" /></b>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                        Level: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                        <form:select path="userProfile.level" >
                            <form:option value="0" label="Select Account Type" />
                            <form:options itemLabel="description" />
                        </form:select>
                    </div>
                </div>
                <div class="divRow">
                    <div class="divOfCell700">
                        Active: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                        <form:checkbox path="active" id="active" />Active
                    </div>
                </div>
            </div>
            <!-- If changing the access level here then update the condition check in POST method -->
            <div class="divRow">
                <div class="divOfCell700"><input type="reset" value="Reset" name="Reset" class="btn btn-default"/> <input type="submit" value="Update" name="Update" class="btn btn-default" /></div>
            </div>
            </form:form>
            </sec:authorize>
		</div>
		<div id="tabs-2">
            <spring:eval expression="${userProfilePreferenceForm.userProfile.receiptUserId eq pageContext.request.userPrincipal.principal.rid}" var="isSameUser" />

            <form:form modelAttribute="expenseTypeForm" method="post" action="i.htm">
                <div style="width: 325px">
                    <section class="chunk">
                        <fieldset>
                            <legend class="hd">
                                <span class="text">Add New Expense Tag</span>
                            </legend>
                            <div class="bd">
                                <div class="text">
                                    Expense Tag:
                                    <form:input class="tooltip" path="tagName" size="6" title="Help's mark an item with specific expense tag." id="expenseTagId"/>
                                    <input type="submit" value="Add" name="Add" style="text-align: right;" <c:out value="${(isSameUser) ? '' : 'disabled'}"/> class="btn btn-default" />
                                    <br/><br/>
                                </div>
                                <div class="text">
                                    <form:errors path="tagName" cssClass="error" />
                                </div>
                                <div class="text">
                                    <span id='expenseTagIdCount'></span> characters remaining.
                                </div>
                            </div>
                        </fieldset>
                    </section>
                </div>
            </form:form>

            <c:if test="${!empty userProfilePreferenceForm.expenseTags}">
            <br/>
            <!-- Used in displaying all the Expense Tags for the user -->
            <c:choose>
                <c:when test="${userProfilePreferenceForm.visibleExpenseTags eq 1}">
                    <p>${userProfilePreferenceForm.visibleExpenseTags} - Expense Tag is available in selection </p>
                </c:when>
                <c:when test="${userProfilePreferenceForm.visibleExpenseTags gt 1}">
                    <p>${userProfilePreferenceForm.visibleExpenseTags} - Expense Tags are available in selection</p>
                </c:when>
                <c:otherwise>
                    <p>No Expense Tag visible</p>
                </c:otherwise>
            </c:choose>
            <table style="width: 325px" class="etable">
                <tr>
                    <th style="padding:3px;"># Used</th>
                    <th style="padding:3px;">Show</th>
                    <th style="padding:3px;">Expense Tag</th>
                    <th style="padding:3px;">Since</th>
                </tr>
                <c:forEach var="expenseTag" items="${userProfilePreferenceForm.expenseTags}" varStatus="status">
                <tr>
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseTag.active eq true}">
                                <c:choose>
                                    <c:when test="${isSameUser}">
                                        <a href="${pageContext.request.contextPath}/access/expenses/${expenseTag.tagName}.htm">
                                            <spring:eval expression="userProfilePreferenceForm.expenseTagCount.get(expenseTag.tagName)" />
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:eval expression="userProfilePreferenceForm.expenseTagCount.get(expenseTag.tagName)" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${isSameUser}">
                                        <del>
                                            <a href="${pageContext.request.contextPath}/access/expenses/${expenseTag.tagName}.htm">
                                                <spring:eval expression="userProfilePreferenceForm.expenseTagCount.get(expenseTag.tagName)" />
                                            </a>
                                        </del>
                                    </c:when>
                                    <c:otherwise>
                                        <del>
                                            <spring:eval expression="userProfilePreferenceForm.expenseTagCount.get(expenseTag.tagName)" />
                                        </del>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                        <c:when test="${userProfilePreferenceForm.expenseTagCount.get(expenseTag.tagName) eq 0}">
                            <c:choose>
                                <c:when test="${isSameUser}">
                                    <a href="${pageContext.request.contextPath}/access/userprofilepreference/expenseTagVisible.htm?id=${expenseTag.id}&status=${expenseTag.active}">
                                        <c:choose>
                                            <c:when test="${expenseTag.active eq true}">
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
                                        <c:when test="${expenseTag.active eq true}">
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
                            <c:when test="${expenseTag.active eq true}">
                                <spring:eval expression="expenseTag.tagName" />
                            </c:when>
                            <c:otherwise>
                                <del><spring:eval expression="expenseTag.tagName" /></del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="padding:3px;">
                        <c:choose>
                            <c:when test="${expenseTag.active eq true}">
                                <spring:eval expression="expenseTag.forYear" />
                            </c:when>
                            <c:otherwise>
                                <del><spring:eval expression="expenseTag.forYear" /></del>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                </c:forEach>
            </table>
            <br/>
            Show - &nbsp;To un-hide the Expense Tag click on Show <br/>
            Hide - &nbsp;&nbsp;&nbsp;To hide the Expense Tag click on Hide <br/>
            Always Shown - This will be shown when the Expense Tag is being used by at least one item

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
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
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

    $(function () {
        $(document).tooltip();
    });
</script>

</body>
</html>