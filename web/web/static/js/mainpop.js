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
                                    "<a href='" + ctx + "/access/pendingdocument.htm' style='text-decoration: none;'>" +
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
    } else {
        $("#receiptListId").hide();
        $("#calendarId").show();
    }
}