jQuery(document).ready(function ($) {
    //Fineuploader start
    var errorHandler = function (event, id, fileName, reason) {
        qq.log("id: " + id + ", fileName: " + fileName + ", reason: " + reason);
    };

    //TODO http://blog.fineuploader.com/2013/01/resume-failed-uploads-from-previous.html
    //$('#fine-uploader-validation').fineUploader({
    new qq.FineUploader({
        element: $('#fine-uploader-validation')[0],
        template: 'qq-template',
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
                            if (response.PENDING > 0) {
                                var html =
                                    "<a href='" + ctx + "/access/document/pending.htm' class='big-view'>" +
                                    "<span class='pendingCounter' id='pendingCountValue'>0</span>" +
                                    "</a>";
                                $('#pendingCountInitial').hide();
                                $('#pendingCountId').html(html).show();
                                $(runCounter(response.PENDING, '#pendingCountValue'));
                                $('#pendingCountSyncedId').attr('data-timestamp', 'asd');
                                $('#pendingCountSyncedId').text("just now");
                            }

                            if (response.REJECTED > 0) {
                                var html =
                                    "<a href='" + ctx + "/access/document/rejected.htm' class='big-view-lower'>" +
                                    "<span class='rejectedCounter' id='rejectedCountValue'>0</span>" +
                                    "</a>";
                                $('#rejectedCountInitial').hide();
                                $('#rejectedCountId').html(html).show();
                                $(runCounter(response.REJECTED, '#rejectedCountValue'));
                                $('#rejectedCountSyncedId').attr('data-timestamp', 'asd');
                                $('#rejectedCountSyncedId').text("just now");
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
            $('#fine-uploader-validation').append('<div class="alert-error">' + message + '</div>');
        }
    });
    //Fineuploader ends
});