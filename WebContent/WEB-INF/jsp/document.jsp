<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><fmt:message key="receipt.update" /></title>

    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>
    <link rel='stylesheet' type='text/css' href="../jquery/fineuploader/fineuploader-3.6.3.css" />

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../jquery/js/raphael/raphael-min.js"></script>
    <script type="text/javascript" src="../jquery/js/dynamic_list_helper2.js"></script>
    <script type="text/javascript" src="../jquery/fineuploader/jquery.fineuploader-3.6.3.min.js"></script>
    <%--<script type="text/javascript" src="../../jquery/js/beatak-imageloader/jquery.imageloader.js"></script>--%>

    <script>
        /* add background color to holder in tr tag */
        window.onload = function () {
            <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
                fetchReceiptImage('${pageContext.request.contextPath}/filedownload/receiptimage/${arr.blobId}.htm', "holder_" + ${status.index}, '${arr.id}', ${arr.imageOrientation}, '${arr.blobId}', '${receiptDocumentForm.receiptDocument.userProfileId}');
            </c:forEach>
        };

        function fetchReceiptImage(location, holder, id, angle, blobId, userProfileId) {
            document.getElementById(holder).innerHTML = "";
            var R = Raphael(holder, 930, 800);
            /* R.circle(470, 400, 400).attr({fill: "#000", "fill-opacity": .5, "stroke-width": 5}); */
            var img = R.image(location, 80, 20, 750, 750);
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
                orientation(id, -90, blobId, userProfileId);
            }).mouseover(function () {
                butt1[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt1[1].stop().attr({fill: "#000"});
            });
            butt2[2].click(function () {
                angle += 90;
                img.animate({transform: "r" + angle}, 1000, "<>");
                orientation(id, 90, blobId, userProfileId);
            }).mouseover(function () {
                butt2[1].animate({fill: "#fc0"}, 300);
            }).mouseout(function () {
                butt2[1].stop().attr({fill: "#000"});
            });
            // setTimeout(function () {R.safari();});

            img.rotate(angle);
        }

        function orientation(id, angle, blobId, userProfileId) {
            $.ajax({
                url: '${pageContext. request. contextPath}/rws/change_fs_image_orientation.htm',
                data: {
                    fileSystemId: id,
                    orientation: angle,
                    blobId: blobId,
                    userProfileId: userProfileId
                },
                type: "POST",
                success: function (data) {
                    if(data == true) {
                        console.log("Success: Receipt_ Image Orientation Updated");
                    } else {
                        console.log("Failed: Receipt_ Image Orientation Updated");
                    }
                }
            });
        }
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
                            <li><a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">Profile And Preferences</a></li>
                            <li><a href="${pageContext.request.contextPath}/signoff.htm">Sign off</a></li>
                            <li><a href="${pageContext.request.contextPath}/eval/feedback.htm">Send Feedback</a></li>
                        </ul>
                    </div>

                </div>
            </h3>
        </div>
    </div>
</div>

<c:choose>
    <c:when test="${!empty receiptDocumentForm.receiptDocument}">

        <spring:eval var="documentStat" expression="receiptDocumentForm.receiptDocument.documentStatus == T(com.receiptofi.domain.types.DocumentStatusEnum).TURK_RECEIPT_REJECT" />
        <c:choose>
            <c:when test="${!documentStat}">
                <h2 class="demoHeaders">Document pending</h2>
            </c:when>
            <c:otherwise>
                <h2 class="demoHeaders">Document rejected</h2>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${empty receiptDocumentForm.receiptDocument.receiptId}">
                <form:form method="post" action="delete.htm" modelAttribute="receiptDocumentForm">
                    <form:hidden path="receiptDocument.receiptId"/>
                    <form:hidden path="receiptDocument.id"/>
                    <input type="submit" value="Delete" name="delete" id="deleteId"/>
                </form:form>
            </c:when>
            <c:otherwise>
                <div class="ui-widget">
                    <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                        <p>
                            <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                            <span style="display:block; width: auto">
                                This receipt is in the process of being Re-Checked.
                            </span>
                        </p>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${!empty receiptDocumentForm.errorMessage}">
                <%--Currently this section of code is not executed unless the error message is added to the form directly without using 'result' --%>
                <div class="ui-widget" id="existingErrorMessage">
                    <div class="ui-state-highlight ui-corner-all alert-error" style="margin-top: 0px; padding: 0 .7em;">
                        <p>
                            <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                            <span style="display:block; width: auto">
                                ${receiptDocumentForm.errorMessage}
                            </span>
                        </p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="ui-widget" id="errorMessage">

                </div>
            </c:otherwise>
        </c:choose>

        <table>
            <tr>
                <td style="vertical-align: top;">
                    <c:forEach items="${receiptDocumentForm.receiptDocument.fileSystemEntities}" var="arr" varStatus="status">
                        <div id="holder_${status.index}" style="height: 850px; border-color:#ff0000 #0000ff;"></div>
                    </c:forEach>
                </td>
            </tr>
        </table>
    </c:when>
    <c:otherwise>
        <div class="ui-widget">
            <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
            <p>
            <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
            <span style="display:block; width:700px;">
            <c:choose>
            <c:when test="${isTech}">
                Oops! Seems like user has deleted this receipt recently.
            </c:when>
            <c:otherwise>
                No receipt found!! Please hit back button and submit a valid request
            </c:otherwise>
            </c:choose>
            </span>
            </p>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>
</html>