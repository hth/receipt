<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>Detail</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script async src="${pageContext.request.contextPath}/static/js/login.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/popup.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>
</head>
<body>

<span class="timestamp"></span>
<div class="clear"></div>
<div class=" is-visible" role="alert">
    <div class="cd-popup-container" style="box-shadow:none;overflow: hidden;">

        <div id="tabde" class="">
            <div style="float:left;width:55%;margin-right: 3%;">
                <h1 class="h1">AUGUST 26, 2014
                    <span style="color: #919191;font-size: 0.8em;font-weight: normal;">12:36PM</span>

                </h1>
                <hr style="width: 100%;">
                <div class="mar10px">
                    <h1 class="font3em">Dds Art</h1>

                    <p class="padtop2per">Near 123</p>

                    <p>Some Where 345</p>
                </div>
                <div class="detailHead">
                    <h1 class="font2em" style="margin-left: 5px;">Map-93 <span class="colorblue right">$1.25</span></h1>
                </div>
                <div class="rightside-list-holder border">
                    <ul>
                        <li>
                            <span class="rightside-li-date-text">1. KJHG Med</span>
                            <select>
                                <option value="volvo">Home</option>
                                <option value="saab">Home</option>
                            </select>
                            <span class="rightside-li-right-text">$1.99</span>
                        </li>
                        <li>
                            <span class="rightside-li-date-text">2. LKJ - Ether</span>
                            <select>
                                <option value="volvo">Home</option>
                                <option value="saab">Home</option>
                            </select>
                            <span class="rightside-li-right-text">$15.99</span>
                        </li>
                        <li>
                            <span class="rightside-li-date-text">3. This thing</span>
                            <select>
                                <option value="volvo">Home</option>
                                <option value="saab">Home</option>
                            </select>
                            <span class="rightside-li-right-text">$22.99</span>
                        </li>
                        <li>
                            <span class="rightside-li-date-text">4. Pink stuff</span>
                            <select>
                                <option value="volvo">Home</option>
                                <option value="saab">Home</option>
                            </select>
                            <span class="rightside-li-right-text">$14.49</span>
                        </li>
                        <li style="border-bottom: 1px dotted #919191;">
                            <span class="rightside-li-date-text">5. Somethings</span>
                            <select>
                                <option value="volvo">Home</option>
                                <option value="saab">Home</option>
                            </select>
                            <span class="rightside-li-right-text">$13.19</span>
                        </li>
                    </ul>


                    <!-- second list starts-->
                    <ul>
                        <li>
                            <span class="rightside-li-date-text">ABC</span>

                            <span class="rightside-li-right-text">$81.65</span>
                        </li>
                        <li>
                            <span class="rightside-li-date-text">BB</span>
                            <span class="rightside-li-right-text">$7.60</span>
                        </li>
                        <li style="border-bottom: 1px solid #919191;">
                            <span class="rightside-li-date-text">ZZZ</span>
                            <span class="rightside-li-right-text">$89.25</span>
                        </li>
                    </ul>


                    <!-- second list ends -->
                    <h1 class="h1 padtop2per" style="padding-bottom:2%;">My notes</h1>
                    <textarea style="width: 561px;height: 145px; padding:1%;" placeholder="Write notes here..."></textarea>
                    <input type="button" value="DELETE" style="background:#FC462A;">
                    <input type="button" value="SAVE" style="background:#0079FF">


                </div>


            </div>

            <div style="width:38%;float: left;padding-top: 4%;">
                <img style="width: 390px;height: 590px;padding-left: 8%;" src="static/img/details.JPG">
            </div>
        </div>

    </div>
    <!-- cd-popup-container -->
</div>

</body>
</html>