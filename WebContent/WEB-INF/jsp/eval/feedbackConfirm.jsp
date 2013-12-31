<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fomr" uri="http://www.springframework.org/tags/form" %>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="feedback.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <meta http-equiv="Refresh" content="3;url=../landing.htm">

    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>
    <link rel='stylesheet' type='text/css' href="../jquery/fineuploader/fineuploader-3.6.3.css" />

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../jquery/js/raty/jquery.raty.min.js"></script>
    <script type="text/javascript" src="../jquery/fineuploader/jquery.fineuploader-3.6.3.min.js"></script>

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

    <script>
        $(document).ready(function () {
            var errorHandler = function (event, id, fileName, reason) {
                qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
            };

            <%-- TODO http://blog.fineuploader.com/2013/01/resume-failed-uploads-from-previous.html --%>
            var restricteduploader = new qq.FineUploader({
                element: $('#restricted-fine-uploader')[0],
                callbacks: {
                    onError: errorHandler,
                    onComplete: function (id, fileName, responseJSON) {
                        if (responseJSON.success == true) {
                            $(this.getItemByFileId(id)).hide('slow');

                            $.ajax({
                                type: 'POST',
                                url:  '${pageContext. request. contextPath}/fetcher/pending.htm',
                                success: function(response) {
                                    if(response > 0) {
                                        var html = '';
                                        html = html +   "<div class='ui-widget'>" +
                                                            "<div class='ui-state-highlight ui-corner-all alert-success' style='margin-top: 0px; padding: 0 .7em;'>" +
                                                                "<p>" +
                                                                    "<span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;' title='Shows number of pending receipt(s) to be processed'></span>" +
                                                                    "<span style='display:block; width:310px;'>";
                                        if(response == 1) {
                                            html = html +               "Pending receipt to be processed: ";
                                        } else {
                                            html = html +               "Pending receipts to be processed: ";
                                        }
                                        html = html +                   "<a href='${pageContext.request.contextPath}/pending.htm'><strong style='color: #065c14;' class='timer' id='pendingCountValue'>" + 0 + "</strong></a>";
                                        html = html +               "</span>" +
                                                                "</p>" +
                                                            "</div>" +
                                                        "</div>";
                                        $('#pendingCountInitial').hide();
                                        $('#pendingCountId').html(html).show();
                                        $(runCounter(response));
                                    }
                                }
                            });
                        }
                    }
                },
                request: {
                    endpoint: '${pageContext. request. contextPath}/landing/upload.htm',
                    customHeaders: { Accept: 'multipart/form-data' }
                },
                multiple: true,
                validation: {
                    allowedExtensions: ['jpeg', 'jpg', 'gif', 'png'],
                    sizeLimit: 10485760 // 10 MB in bytes
                },
                text: {
                    uploadButton: '&uarr; &nbsp; Click or Drop to upload Attachment(s)'
                },
                showMessage: function (message) {
                    $('#restricted-fine-uploader').append('<div class="alert-error">' + message + '</div>');
                }
            });
        });
    </script>

</head>
<body>
<div class="wrapper">
    <div class="divTable">
        <div class="divRow">
            <div class="divOfCell50" style="height: 46px">
                <img src="${pageContext.request.contextPath}/images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="46px"/>
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
                            </ul>
                        </div>

                    </div>
                </h3>
            </div>
        </div>
    </div>

    <p>&nbsp;</p>

    <div>
        <div class='ui-widget'>
            <div class='ui-state-highlight ui-corner-all alert-success' style='margin-top: 0px; padding: 0 .7em;'>
                <p>
                   <span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;' title='Shows number of pending receipt(s) to be processed'></span>
                   <span style='display:block; width:310px;'>
                       <p>Thank you for providing valuable feedback.</p>
                   </span>
                </p>
            </div>
        </div>
    </div>

    <p>
        <br/><br/><br/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        Redirecting to home page in couple of seconds... If not redirected then please <a href="../landing.htm">click here</a>
    </p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>