jQuery(document).ready(function ($) {
    //open popup
    $('.cd-popup-trigger').on('click', function (event) {
        event.preventDefault();
        $('.cd-popup').addClass('is-visible');
    });

    //close popup
    $('.cd-popup').on('click', function (event) {
        if ($(event.target).is('.cd-popup-close') || $(event.target).is('.cd-popup')) {
            event.preventDefault();
            $(this).removeClass('is-visible');
        }
    });

    //close popup when clicking the esc keyboard button
    $(document).keyup(function (event) {
        if (event.which == '27') {
            $('.cd-popup').removeClass('is-visible');
        }
    });

    //close starts
    $("#btnlist").click(function () {
        $("#btnlist").addClass("btnborder");
        $("#btndetail").removeClass("btnborder");
        $(".rightside-list-holder").slideUp(1000);
        $(".pie-chart").slideUp(1000);
    });


    $("#btndetail").click(function () {
        $("#btndetail").addClass("btnborder");
        $("#btnlist").removeClass("btnborder");
        $(".rightside-list-holder").slideDown(1000);
        $(".pie-chart").slideDown(1000);

    });
    //close ends

    $('.timestamp').cuteTime({refresh: 10000});

    $("#tabs").tabs({
        beforeLoad: function (event, ui) {
            ui.jqXHR.error(function () {
                ui.panel.html(
                    "Couldn't load this tab. We'll try to fix this as soon as possible. If this wouldn't be a demo.");
            });
        }
    });
});

function runCounter(max, field) {
    "use strict";
    incCounter();

    function incCounter() {
        var currCount = parseInt($(field).html());
        if (currCount < max) {
            $(field).text(currCount + 1);
            setTimeout(incCounter, 1);
        }
    }
}

function extracted(object, inviteEmailId, link) {
    $.ajax({
        type: "POST",
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            $('#sendInvite_bt').css('background', '#2b3e51').attr('disabled', 'disabled');
            $('#inviteEmailId').attr('disabled', 'disabled');
        },
        url: ctx + link,
        data: object,
        success: function (response) {
            console.debug(response);
            var json = $.parseJSON(response);
            $('#sendInvite_bt').css('background', '#2c97de');
            $('#inviteEmailId').removeAttr('disabled');
            if (json.status) {
                $('#inviteTextMessage').html(json.message).addClass("r-success").css("margin-left", "0px").css("width", "100%").delay(5000)
                    .fadeOut('fast', function () {
                        if ($("#inviteEmailId").val() == '' || inviteEmailId == $("#inviteEmailId").val()) {
                            $("#inviteEmailId").val("").attr('placeholder', 'Email address of friend here ...');
                            $('#sendInvite_bt').css('background', '#808080').attr('disabled', 'disabled');
                        }
                        $("#inviteTextMessage").html("").removeClass("r-success").show();
                    });
            } else {
                $('#inviteTextMessage').html(json.message).addClass("r-error").css("margin-left", "0px").css("width", "100%").delay(5000)
                    .fadeOut('fast', function () {
                        if ($("#inviteEmailId").val() == '') {
                            $("#inviteEmailId").val("").attr('placeholder', 'Email address of friend here ...');
                            $('#sendInvite_bt').css('background', '#808080').attr('disabled', 'disabled');
                        }
                        $("#inviteTextMessage").html("").removeClass("r-error").show();
                    });
            }
        },
        error: function (response, xhr, ajaxOptions, thrownError) {
            console.error(response, xhr.status, thrownError);
        }
    });
}

function submitInvitationForm() {
    "use strict";

    var inviteEmailId = $("#inviteEmailId").val();
    var object = {mail: inviteEmailId};

    extracted(object, inviteEmailId, "/access/landing/invite.htm");
}

function submitAccountantInvitationForm() {
    "use strict";

    var inviteEmailId = $("#inviteEmailId").val();
    var object = {mail: inviteEmailId};

    extracted(object, inviteEmailId, "/access/invite/accountant.htm");
}

function submitBusinessInvitationForm() {
    "use strict";

    var inviteEmailId = $("#inviteEmailId").val();
    var object = {mail: inviteEmailId};

    extracted(object, inviteEmailId, "/access/invite/business.htm");
}

function computeDeliveryCount(id, url) {
    $.ajax({
        type: "GET",
        url: ctx + url,
        success: function (response) {
            console.log(response);
            $(id).html(response.m).show();
        },
        error: function (response, xhr, ajaxOptions, thrownError) {
            console.error(response, xhr.status, thrownError);
        }
    });
}

function changeInviteText(field, text) {
    if (text === 'blur') {
        if (field.value == '') {
            field.placeholder = 'Email address of friend here ...';
            field.value = '';
            $('#sendInvite_bt').css('background', '#808080').attr('disabled', 'disabled');
        }
    } else {
        field.placeholder = '';
        $('#inviteText').html('Invitation is sent with your name and email address');
        $('#sendInvite_bt').removeAttr('style').removeAttr('disabled');
    }
}

function loadMonthlyExpenses(date) {
    $.ajax({
        type: "POST",
        url: ctx + '/access/landing/monthly_expenses.htm',
        data: {
            monthView: date
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            $('#onLoadReceiptForMonthId').remove();
            //Add spinner here
        },
        success: function (response) {
            console.log("Date:" + date);
            $('#refreshReceiptForMonthId').html(response).show();

            $(".fc-prev-button").removeClass('fc-state-disabled').prop('disabled', false);
            $(".fc-next-button").removeClass('fc-state-disabled').prop('disabled', false);

            $("#btnList").removeClass('toggle_disabled');
            $("#btnCalendar").removeClass('toggle_disabled');
        },
        complete: function () {
            //do nothing as load removes spinner
        }
    });
}

/**
 * Delete pending document confirmation box.
 * Note: added document ready around the function call and import main js at the end of the page.
 */
function confirmBeforeAction() {
    $("#deletePendingDocument").click(function (event) {
        event.preventDefault();
        swal({
            title: "Are you sure?",
            text: "You will not be able to recover this file!",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: 'btn-danger',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: "No, cancel please!",
            closeOnConfirm: true,
            closeOnCancel: true
        }, function (isConfirm) {
            if (isConfirm) {
                $('form#receiptDocumentForm').submit();
            }
        })
    });

    $("#deleteBtnId").click(function (event) {
        event.preventDefault();
        swal({
            title: "Are you sure?",
            text: "You will not be able to recover this file!",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: 'btn-danger',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: "No, cancel please!",
            closeOnConfirm: true,
            closeOnCancel: true
        }, function (isConfirm) {
            if (isConfirm) {
                var bn = $('.font3em').text();
                var gt = $('#gtId').text();
                $.ajax({
                    type: 'POST',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(
                            $("meta[name='_csrf_header']").attr("content"),
                            $("meta[name='_csrf']").attr("content")
                        );
                    },
                    url: 'delete.htm',
                    data: JSON.stringify({
                        receiptId : $('#receiptId').val()
                    }),
                    contentType : "application/json",
                    success: function (responseData) {
                        if (responseData.result == true) {
                            $('#actionMessageId').attr('hidden', false);
                            $(".r-success").html("'" + bn + "' receipt for " + gt + " deleted successfully.").show();
                            $("div.detail-view-container").html("");
                        } else {
                            $('#actionMessageId').attr('hidden', false);
                            $(".r-error").html(responseData.message).show();
                            $("div.detail-view-container").html("");
                        }

                        /*Add css since its not loaded when receipt is deleted*/
                        var cssId = 'myCss';
                        if (!document.getElementById(cssId)) {
                            var head = document.getElementsByTagName('head')[0];
                            var link = document.createElement('link');
                            link.id = cssId;
                            link.rel = 'stylesheet';
                            link.type = 'text/css';
                            link.href = ctx + '/static/css/stylelogin.css';
                            link.media = 'all';
                            head.appendChild(link);
                        }
                    },
                    error: function () {
                        console.log("Error deleting receipt");
                    }
                })
            }
        })
    });

    $("#recheckBtnId").click(function (event) {
        event.preventDefault();
        swal({
            title: "Are you sure?",
            text: "We are sorry to hear you found errors. \n " +
            "This receipt will be re-processed for correction if any. \n\n " +
            "During re-process receipt will be listed under pending.",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: 'btn-danger',
            confirmButtonText: 'Yes, re-process it!',
            cancelButtonText: "No, cancel please!",
            closeOnConfirm: true,
            closeOnCancel: true
        }, function (isConfirm) {
            if (isConfirm) {
                var bn = $('.font3em').text();
                var gt = $('#gtId').text();
                $.ajax({
                    type: 'POST',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(
                            $("meta[name='_csrf_header']").attr("content"),
                            $("meta[name='_csrf']").attr("content")
                        );
                    },
                    url: 'recheck.htm',
                    data: JSON.stringify({
                        receiptId : $('#receiptId').val()
                    }),
                    contentType : "application/json",
                    success: function (responseData) {
                        if (responseData.result == true) {
                            $('#actionMessageId').attr('hidden', false);
                            $(".r-success").html("'" + bn + "' receipt for " + gt + " sent for re-check.").show();
                            $('link[href="/static/css/popup.css"]').attr('href','/static/css/stylelogin.css');
                            $("div.detail-view-container").html("");
                        } else {
                            $('#actionMessageId').attr('hidden', false);
                            $(".r-error").html(responseData.message).show();
                            $("div.detail-view-container").html("");
                        }

                        /*Add css since its not loaded when receipt is deleted*/
                        var cssId = 'myCss';
                        if (!document.getElementById(cssId)) {
                            var head = document.getElementsByTagName('head')[0];
                            var link = document.createElement('link');
                            link.id = cssId;
                            link.rel = 'stylesheet';
                            link.type = 'text/css';
                            link.href = ctx + '/static/css/stylelogin.css';
                            link.media = 'all';
                            head.appendChild(link);
                        }
                    },
                    error: function () {
                        console.log("Error deleting receipt");
                    }
                })
            }
        })
    });

    $("#expenseTagDelete_bt").click(function (event) {
        event.preventDefault();
        swal({
            title: "Are you sure?",
            text: "Receipts and Items using this expense tag will be reset to blank.",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: 'btn-danger',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: "No, cancel please!",
            closeOnConfirm: true,
            closeOnCancel: true
        }, function (isConfirm) {
            if (isConfirm) {
                $.ajax({
                    type: 'POST',
                    beforeSend: function (xhr) {
                        $("#expenseTagSuccess").hide();
                        $("#expenseTagError").hide();

                        xhr.setRequestHeader(
                            $("meta[name='_csrf_header']").attr("content"),
                            $("meta[name='_csrf']").attr("content")
                        );
                    },
                    url: 'deleteExpenseTag.htm',
                    data: JSON.stringify({
                        tagColor : $('input[name=tagColor]').val(),
                        tagId : $('input[name=tagId]').val(),
                        tagName : $('input[name=tagName]').val()
                    }),
                    contentType : "application/json",
                    success: function (responseData) {
                        if (responseData.result == true) {
                            $("#expenseTagSuccess").html(responseData.message).show();
                            $("#"+$('input[name=tagId]').val()).remove();
                        } else {
                            $("#expenseTagError").html(responseData.message).show();
                        }
                        $("#tagName").val('');
                        $('#expenseTagSaveUpdate_bt').val('SAVE');
                        $('#expenseTagDelete_bt').attr('hidden', true);
                        $('#tagId').val('');
                        inactiveExpenseTagSaveUpdate_bt.call(this);
                    },
                    error: function () {
                        console.log()
                    }
                })
            }
        });
    });
}

function userProfilePreferences() {
    $('#tagName').NobleCount('#textCount', {
        on_negative: 'error',
        on_positive: 'success',
        max_chars: 22
    });

    $("#tagName").on('click', function () {
        activeExpenseTagSaveUpdate_bt.call(this);
    });
}

function clickedExpenseTag(button, iconId) {
    var buttonValue = button.value.split(" ");
    var tagName = '', space = '';
    for(var i = 0; i < buttonValue.length - 1; i ++) {
        if(i != 0) {
            tagName = tagName + space;
            space = ' ';
            tagName = tagName + buttonValue[i];
        }
    }
    $('#tagColor').val($(button).attr('style').split(" ")[1].substring(0,7));
    $('#tagId').val($(button).attr('id'));

    $('#tagName').focus().val(tagName);
    $('.color-box').css('background-color', $(button).attr('style').split(" ")[1]);
    $('#textCount').text(22 - tagName.length);
    // $('#' + iconId).prop('selected', true);
    // $('#tagIcon').prop('selected', true);

    $('#expenseTagSaveUpdate_bt').val('UPDATE');
    $('#expenseTagDelete_bt').attr('hidden', false);

    $('#tagErrors').hide();
    activeExpenseTagSaveUpdate_bt();
}

function activeExpenseTagSaveUpdate_bt() {
    $(this).prop("readonly", false).focus();
    $('#expenseTagSaveUpdate_bt').attr('disabled', false).css('background', '#2c97de');
}

function inactiveExpenseTagSaveUpdate_bt() {
    $(this).prop("readonly", true).focus();
    $('#expenseTagSaveUpdate_bt').attr('disabled', true).css('background', '#808080');
}

function loadMonthlyExpensesByBusiness(month, bizNames, expenseTags, currencyCode) {
    Highcharts.setOptions({
        lang: {
            thousandsSep: ','
        }
    });

    $('#expenseByBusiness').highcharts({
        chart: {
            type: 'pie'
        },
        credits: {
            enabled: false
        },
        title: {
            text: 'Expense By Business: ' + month
        },
        yAxis: {
            title: {
                text: 'Total expense'
            }
        },
        plotOptions: {
            pie: {
                shadow: false,
                center: ['50%', '50%'],
                slicedOffset: 0
            }
        },
        tooltip: {
            valueSuffix: currencyCode,
            formatter: function () {
                return this.point.name + ": " + currencyCode + Highcharts.numberFormat(this.y, 2);
            }
        },
        series: [
            {
                name: 'Total',
                data: bizNames,
                size: '60%',
                dataLabels: {
                    enabled: false,
                    formatter: function () {
                        return this.y > 1 ? this.point.name : null;
                    },
                    color: 'white',
                    distance: -30
                },
                point: {
                    events: {
                        click: function (e) {
                            console.log(this.options.url);
                            location.href = this.options.url;
                        },
                        mouseOver: function (e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).toggleClass('highlight');
                        },
                        mouseOut: function (e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).removeClass('highlight');
                        }
                    }
                },
                allowPointSelect: true,
                cursor: 'pointer'
            },
            {
                name: 'Total',
                data: expenseTags,
                size: '80%',
                innerSize: '60%',
                dataLabels: {
                    enabled: false,
                    formatter: function () {
                        // display only if larger than 1
                        return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + currencyCode + Highcharts.numberFormat(this.y, 2) : null;
                    }
                },
                point: {
                    events: {
                        click: function (e) {
                            console.log(this.options.url);
                            location.href = this.options.url;
                        },
                        mouseOver: function (e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).toggleClass('highlight');
                        },
                        mouseOut: function (e) {
                            console.log('#' + this.options.id);
                            $('#tableReceiptForMonth tr#' + this.options.id).removeClass('highlight');
                        }
                    }
                },
                allowPointSelect: true,
                cursor: 'pointer'
            }
        ]
    });
}

function toggleListCalendarView(button) {
    console.log("Show " + button.value);
    var content = 'btn' + button.value;
    if(content === 'btnList' && !$("#btnList").hasClass('toggle_disabled')) {
        $("#calendarId").hide();
        $("#receiptListId").show();
        $("#noReceiptId").removeClass("temp_offset").show();
        $("#receiptListId_refreshReceiptForMonthId").removeClass("temp_offset");
        $(".rightside-list-holder").show().removeAttr("id");
        $("#btnList").addClass("toggle_selected");
        $("#btnCalendar").removeClass("toggle_selected");
    } else {
        $("#noReceiptId").hide();
        $("#receiptListId").hide();
        $(".rightside-list-holder").hide();
        $("#calendarId").show();
        $("#btnList").removeClass("toggle_selected");
        $("#btnCalendar").addClass("toggle_selected");
    }
}

function fetchReceiptImage(location, holder, id, angle, blobId, receiptUserId) {
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
        orientation(id, -90, blobId, receiptUserId);
    }).mouseover(function () {
        butt1[1].animate({fill: "#fc0"}, 300);
    }).mouseout(function () {
        butt1[1].stop().attr({fill: "#000"});
    });
    butt2[2].click(function () {
        angle += 90;
        img.animate({transform: "r" + angle}, 1000, "<>");
        orientation(id, 90, blobId, receiptUserId);
    }).mouseover(function () {
        butt2[1].animate({fill: "#fc0"}, 300);
    }).mouseout(function () {
        butt2[1].stop().attr({fill: "#000"});
    });
    // setTimeout(function () {R.safari();});

    img.rotate(angle);
}

function orientation(id, angle, blobId, receiptUserId) {
    $.ajax({
        url: ctx + '/ws/r/change_fs_image_orientation.htm',
        data: {
            fileSystemId: id,
            orientation: angle,
            blobId: blobId,
            receiptUserId: receiptUserId
        },
        beforeSend: function(xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        type: "POST",
        success: function (data) {
            if(data == true) {
                console.log("Success: Receipt Image Orientation Updated");
            } else {
                console.log("Failed: Receipt Image Orientation Updated");
            }
        }
    });
}

function displayMarker(title, latitude, longitude, xindex, map, infowindow) {
    // Add markers to the map

    // Marker sizes are expressed as a Size of X,Y
    // where the origin of the image (0,0) is located
    // in the top left of the image.

    // Origins, anchor positions and coordinates of the marker
    // increase in the X direction to the right and in
    // the Y direction down.
    var image = {
        url: ctx + '/static/images/beachflag.png',
        // This marker is 20 pixels wide by 32 pixels tall.
        size: new google.maps.Size(20, 32),
        // The origin for this image is 0,0.
        origin: new google.maps.Point(0,0),
        // The anchor for this image is the base of the flagpole at 0,32.
        anchor: new google.maps.Point(0, 32)
    };
    var shadow = {
        url: ctx + '/static/images/beachflag_shadow.png',
        // The shadow image is larger in the horizontal dimension
        // while the position and offset are the same as for the main image.
        size: new google.maps.Size(37, 32),
        origin: new google.maps.Point(0,0),
        anchor: new google.maps.Point(0, 32)
    };
    // Shapes define the clickable region of the icon.
    // The type defines an HTML &lt;area&gt; element 'poly' which
    // traces out a polygon as a series of X,Y points. The final
    // coordinate closes the poly by connecting to the first
    // coordinate.
    var shape = {
        coord: [1, 1, 1, 20, 18, 20, 18 , 1],
        type: 'poly'
    };

    var myLatLng = new google.maps.LatLng(latitude, longitude);

    //Why re-center the US Map
    //map.setCenter(myLatLng);

    var marker = new google.maps.Marker({
        position: myLatLng,
        map: map,
        shadow: shadow,
        icon: image,
        shape: shape,
        title: title,
        zIndex: xindex
    });

    google.maps.event.addListener(marker, 'click', function() {
        infowindow.setContent(title);
        infowindow.open(map, marker);
    });

    google.maps.event.addListener(marker, 'mouseover', function() {
        infowindow.setContent(title);
        infowindow.open(map, marker);
    });
}

function getGoogleMap(locations) {
    var bounds = new google.maps.LatLngBounds ();
    var map, infowindow;

    var myOptions = {
        zoom: 4,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    var $mapCanvas = $("#map-canvas");
    map = new google.maps.Map($mapCanvas.get(0), myOptions);
    //map.fitBounds(bounds);
    var listenerHandle = google.maps.event.addListener(map, 'idle', function() {
        $mapCanvas.appendTo($("#map-placeholder"));
        google.maps.event.removeListener(listenerHandle);
    });

    infowindow = new google.maps.InfoWindow();
    google.maps.event.addListener(map, 'click', function() {
        infowindow.close();
    });

    for (var i = 0; i < locations.length; i++) {
        var location    = locations[i];
        var title       = location[0];
        var latitude    = location[1];
        var longitude   = location[2];
        var xindex      = location[3];
        displayMarker(title, latitude, longitude, xindex, map, infowindow);

        // And increase the bounds to take this point
        bounds.extend(new google.maps.LatLng (latitude, longitude));
    }

    if (locations.length > 1) {
        //Fit these bounds to the map
        map.fitBounds(bounds);
    } else if (locations.length == 1) {
        map.setCenter(bounds.getCenter());
        map.setZoom(16);
    }
}

function reportTabClicked() {
    $("#analysisSidebarId").hide();
    $("#reportSidebarId").show();
}

function analysisTabClicked() {
    $("#reportSidebarId").hide();
    $("#analysisSidebarId").show();
}

function calendarActions() {
    $('body')
        .on('click', 'button.fc-prev-button', function () {
            $(".fc-prev-button").prop('disabled', true).addClass('fc-state-disabled');
            $(".fc-next-button").prop('disabled', true).addClass('fc-state-disabled');
            $("#btnList").addClass('toggle_disabled');
            $("#btnCalendar").addClass('toggle_disabled');

            loadMonthlyExpenses($("#calendar").fullCalendar('getDate').format("MMM, YYYY"));
            $("#monthShownId").html($("#calendar").fullCalendar('getDate').format("MMMM, YYYY"));
            $("#expenseByBusiness").html('');  //Set to blank pie chart and reload
        })
        .on('click', 'button.fc-next-button', function () {
            $(".fc-prev-button").prop('disabled', true).addClass('fc-state-disabled');
            $(".fc-next-button").prop('disabled', true).addClass('fc-state-disabled');
            $("#btnList").addClass('toggle_disabled');
            $("#btnCalendar").addClass('toggle_disabled');

            loadMonthlyExpenses($("#calendar").fullCalendar('getDate').format("MMM, YYYY"));
            $("#monthShownId").html($("#calendar").fullCalendar('getDate').format("MMMM, YYYY"));
            $("#expenseByBusiness").html('');  //Set to blank pie chart and reload
        });
}

function populateExpenseByBusiness(data, bizNames, categories, expenseTags) {
    for (var i = 0; i < data.length; i++) {

        // add browser data
        bizNames.push({
            name: categories[i],
            y: data[i].y,
            color: data[i].color,
            url: data[i].url,
            id: data[i].id
        });

        // add version data
        for (var j = 0; j < data[i].drilldown.data.length; j++) {
            var brightness = 0.2 - (j / data[i].drilldown.data.length) / 5;
            expenseTags.push({
                name: data[i].drilldown.categories[j],
                y: data[i].drilldown.data[j],
                color: Highcharts.Color(data[i].color).brighten(brightness).get(),
                url: data[i].drilldown.url,
                id: data[i].drilldown.id
            });
        }
    }
}

function friendRequest(id, auth, connectionType) {
    "use strict";

    var object = {id: id, auth: auth, ct: connectionType};

    $.ajax({
        type: "POST",
        beforeSend: function (xhr) {
            $('#acceptFriend_bt').attr('disabled', 'disabled');
            $('#declineFriend_bt').attr('disabled', 'disabled');
            $('#cancelFriend_bt').attr('disabled', 'disabled');

            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        url: ctx + "/access/split/friend.htm",
        data: object,
        success: function (response) {
            console.debug(response);
            $('#acceptFriend_bt').removeAttr('disabled');
            $('#declineFriend_bt').removeAttr('disabled');
            $('#cancelFriend_bt').removeAttr('disabled');

            if (response.success && connectionType === 'A') {
                $('#' + id).find("label").slice(1, 3).remove();
                $('#' + id).insertAfter('#friends');

                if (!$.trim($('#awaiting').html()).length) {
                    $('#awaiting').prev("h2").remove();
                    $('#awaiting').remove();
                    $('#pending').prev("h2").css("padding-top", "0%");
                }

                $('#tabs-2 #friends .r-info').remove();
            } else if(response.success && connectionType === 'D') {
                $('#' + id).remove();

                if (!$.trim($('#awaiting').html()).length) {
                    $('#awaiting').prev("h2").remove();
                    $('#awaiting').remove();
                    $('#pending').prev("h2").css("padding-top", "0%");
                }
            } else if(response.success && connectionType === 'C') {
                $('#' + id).remove();

                if (!$.trim($('#pending').html()).length) {
                    $('#pending').prev("h2").remove();
                    $('#pending').remove();
                }
            }
        },
        error: function (response, xhr, ajaxOptions, thrownError) {
            console.error(response, xhr.status, thrownError);
            $('#acceptFriend_bt').removeAttr('disabled');
            $('#declineFriend_bt').removeAttr('disabled');
            $('#cancelFriend_bt').removeAttr('disabled');
        }
    });
}

function unfriendRequest(mail, name, id, event) {
    event.preventDefault();
    swal({
        imageUrl: "/static/images/disconnectedx88.png",
        title: "Are you sure to unfriend?",
        text: "" +
        "<p style='text-align: left;'>You and " + name + " would not be able to split expenses among yourselves. " +
        "Lost connection can only be re-initiated by you.<br><br>" +
        "<p style='text-align: left;'><b>"  + name + "</b> with email: '<b>" + mail + "</b>' will be removed from " +
        "your connection immediately.",
        showCancelButton: true,
        confirmButtonClass: 'btn-danger',
        confirmButtonColor: "#DD6B55",
        confirmButtonText: 'Yes, unfriend me.',
        cancelButtonText: "No, cancel please!",
        closeOnConfirm: true,
        closeOnCancel: true,
        html: true
    }, function (isConfirm) {
        if (isConfirm) {
            $.ajax({
                type: 'POST',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(
                        $("meta[name='_csrf_header']").attr("content"),
                        $("meta[name='_csrf']").attr("content")
                    );
                },
                url: ctx + "/access/split/unfriend.htm",
                data: {mail: mail},
                success: function (responseData) {
                    if (responseData.success === true) {
                        $('#' + id).remove();

                        if (!$.trim($('#friends').html()).length) {
                            if(document.getElementById("pending")) {
                                $('#friends').html(
                                    "<div class='r-info' id='noReceiptId'>" +
                                    "Friend has yet to approve your request." +
                                    "</div>"
                                );
                            } else {
                                $('#friends').html(
                                    "<div class='r-info' id='noReceiptId'>" +
                                    "Invite friends to split expenses." +
                                    "</div>"
                                );
                            }
                        }
                    } else if (responseData.success === false) {
                        console.log("Failed to unfriend: " + name);
                    }
                },
                error: function () {
                    console.log("Error during unfriend: " + name);
                }
            })
        }
    })
}

function updateReceiptSplit(fid, receiptId) {
    var parentDiv = $('#' + fid).parent("div").attr("id");

    var splitAction;
    if (parentDiv === 'friends') {
        splitAction = 'A';
        $("#splits").append($('#' + fid));
    } else if (parentDiv === 'splits') {
        splitAction = 'R';
        $("#friends").append($('#' + fid));
    }

    $.ajax({
        type: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(
                $("meta[name='_csrf_header']").attr("content"),
                $("meta[name='_csrf']").attr("content")
            );
        },
        url: ctx + "/access/receipt/split.htm",
        data: {fid: fid, receiptId: receiptId, splitAction: splitAction},
        success: function (responseData) {
            if (responseData.result === true) {
                if (responseData.hasOwnProperty('splitTotal')) {
                    $('#my_total').html(responseData.splitTotal);
                }
            } else if (responseData.result === false) {
                if (parentDiv === 'splits') {
                    $("#splits").append($('#' + fid));
                } else if (parentDiv === 'friends') {
                    $("#friends").append($('#' + fid));
                }
            }
        },
        error: function () {
            console.log("Error during splitting expenses: " + name);
        }
    })
}

function settleSplit(id) {
    var parentDiv = $('#' + id).closest("div").attr("id");
    $('#' + id).fadeOut("slow");

    if ($("#" + parentDiv).children("ul").children("li").length == 0) {
        $('#' + parentDiv).hide();
    }

    $.ajax({
        type: 'POST',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(
                $("meta[name='_csrf_header']").attr("content"),
                $("meta[name='_csrf']").attr("content")
            );
        },
        url: ctx + "/access/split/settle.htm",
        data: {id: id},
        success: function (responseData) {
            if (responseData.success === true) {
                $('#' + id).remove();

                if ($("#" + parentDiv).children("ul").children("li").length == 0) {
                    $('#' + parentDiv).remove();
                }

            } else if (responseData.success === false) {
                $('#' + id).fadeIn("fast");

                if ($("#" + parentDiv).children("ul").children("li").length == 0) {
                    $('#' + parentDiv).show();
                }
            }
        },
        error: function () {
            console.log("Error during settling split expenses: " + id);
        }
    })
}