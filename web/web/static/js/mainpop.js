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

    //Fineuploader start
    var errorHandler = function (event, id, fileName, reason) {
        qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
    };

    //TODO http://blog.fineuploader.com/2013/01/resume-failed-uploads-from-previous.html
    new qq.FineUploader({
        element: $('#restricted-fine-uploader')[0],
        callbacks: {
            onError: errorHandler,
            onComplete: function (id, fileName, responseJSON) {
                if (responseJSON.success == true) {
                    $(this.getItemByFileId(id)).hide('slow');

                    $.ajax({
                        type: 'POST',
                        beforeSend: function (xhr) {
                            xhr.setRequestHeader(
                                $("meta[name='_csrf_header']").attr("content"),
                                $("meta[name='_csrf']").attr("content")
                            );
                        },
                        url: ctx + '/ws/r/pending.htm',
                        success: function (response) {
                            if (response > 0) {
                                var html =
                                    "<a href='" + ctx + "/access/document/pending.htm' class='big-view'>" +
                                    "<span class='pendingCounter' id='pendingCountValue'>0</span>" +
                                    "</a>";
                                $('#pendingCountInitial').hide();
                                $('#pendingCountId').html(html).show();
                                $(runCounter(response));
                                $('#pendingCountSyncedId').attr('data-timestamp', 'asd');
                                $('#pendingCountSyncedId').text("just now");
                            }
                        }
                    });
                }
            }
        },
        request: {
            endpoint: ctx + '/access/landing/upload.htm',
            customHeaders: {
                Accept: 'multipart/form-data',
                'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
            }
        },
        multiple: true,
        validation: {
            allowedExtensions: ['jpeg', 'jpg', 'gif', 'png'],
            sizeLimit: 10485760 // 10 MB in bytes
        },
        text: {
            uploadButton: '&uarr; &nbsp; UPLOAD IMAGE(S)'
        },
        showMessage: function (message) {
            $('#restricted-fine-uploader').append('<div class="alert-error">' + message + '</div>');
        }
    });
    //Fineuploader ends

    // Load by hiding calendar by default
    $("#calendarId").hide();
    $("#btnList").addClass("toggle_selected");
});

function runCounter(max) {
    "use strict";
    incCounter();

    function incCounter() {
        var currCount = parseInt($('#pendingCountValue').html());
        if (currCount < max) {
            $('#pendingCountValue').text(currCount + 1);
            setTimeout(incCounter, 1);
        }
    }
}

function submitInvitationForm() {
    "use strict";

    var inviteEmailId = jQuery("#inviteEmailId").val();
    var object = {emailId: inviteEmailId};

    $.ajax({
        type: "POST",
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        url: ctx + "/access/landing/invite.htm",
        data: object,
        success: function (response) {
            $('#inviteText').html(response);
            $('#inviteEmailId').val('Email address of friend here ...');
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status);
            alert(thrownError);
        }
    });
}

function changeInviteText(field, text) {
    if (text === 'blur') {
        if (field.value == '') {
            field.value = 'Email address of friend here ...';
        }
    } else {
        field.value = '';
        $('#inviteText').html('Invitation sent with your name and email address');
    }
}

function loadMonthlyExpenses(date, clicked) {
    $.ajax({
        type: "POST",
        url: ctx + '/access/landing/monthly_expenses.htm',
        data: {
            monthView: date,
            buttonClick: clicked
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
            $('#onLoadReceiptForMonthId').hide();
            $('#refreshReceiptForMonthId').html(
                "<div class='spinner large' id='spinner'></div>"
            ).show();
        },
        success: function (response) {
            $('#refreshReceiptForMonthId').html(response).show();
        },
        complete: function () {
            //do nothing as load removes spinner
        }
    });
}

function loadMonthlyExpenses(month, bizNames, expenseTags) {
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
            valueSuffix: '$',
            formatter: function () {
                return this.point.name + ": " + '$' + Highcharts.numberFormat(this.y, 2);
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
                        return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + '$' + Highcharts.numberFormat(this.y, 2) : null;
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
    var content = 'btn' + button.value;
    if(content === 'btnList') {
        $("#calendarId").hide();
        $("#receiptListId").show();
        $("#btnList").addClass("toggle_selected");
        $("#btnCalendar").removeClass("toggle_selected");
    } else {
        $("#receiptListId").hide();
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
                console.log("Success: Receipt_ Image Orientation Updated");
            } else {
                console.log("Failed: Receipt_ Image Orientation Updated");
            }
        }
    });
}