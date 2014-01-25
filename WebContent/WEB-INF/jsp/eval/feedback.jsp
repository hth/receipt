<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><fmt:message key="feedback.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script type="text/javascript" src="../jquery/js/raty/jquery.raty.min.js"></script>
    <script type="text/javascript" src="../jquery/js/noble-count/jquery.NobleCount.min.js"></script>

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
            $('#comment').NobleCount('#feedbackComment', {
                on_negative: 'error',
                on_positive: 'success',
                max_chars: 250
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
        <section class="chunk">
            <fieldset>
                <legend class="hd">
                    <span class="text"><fmt:message key="feedback.title" /></span>
                </legend>
                <div class="bd">
                <form:form method="post" action="feedback.htm" modelAttribute="evalFeedbackForm" enctype="multipart/form-data">
                    <form:hidden path="rating" />
                    <div class="divTable">
                        <div class="divRow">
                            <div class="divOfCell110" style="background-color: #eee">
                                Message:
                            </div>
                            <div class="divOfCell500" style="background-color: #eee">
                                <form:textarea path="comment" id="comment" size="250" cols="50" rows="4" />
                                <br/>
                                <span id='feedbackComment'></span> characters remaining remaining
                                <br/>
                                <form:errors path="comment" cssClass="error" />
                            </div>
                        </div>
                        <div class="divRow">
                            <div class="divOfCell110" style="background-color: #eee">
                                Please rate:
                            </div>
                            <div class="divOfCell500" style="background-color: #eee">
                                <div id="star"></div>
                            </div>
                        </div>
                        <div class="divRow">
                            <div class="divOfCell110" style="background-color: #eee">
                                Attachment:
                            </div>
                            <div class="divOfCell500" style="background-color: #eee">
                                <form:input path="fileData" type="file" size="17"/>
                                <br/>
                                <form:errors path="fileData" cssClass="error" />
                            </div>
                        </div>
                        <div class="divRow">
                            <div class="divOfCell110" style="background-color: #eee">
                                &nbsp;
                            </div>
                            <div class="divOfCell500" style="background-color: #eee">
                                <input type="submit" value="Submit" name="submit" class="btn btn-default" />
                            </div>
                        </div>
                    </div>
                </form:form>
                </div>
            </fieldset>
        </section>
    </div>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved.</p>
</div>

<script>
    $('#star').raty({
        score   : $('#rating').val(),
        cancel  : true,
        size    : 25
    });
</script>
<script>
    $("#star > img").click(function(){
        var score = $(this).attr("alt");
        $('#rating').val(score);
    });
</script>

</body>