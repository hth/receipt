$(document).ready(function ($) {
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
            onSubmit: function(id, fileName) {
                $('#couponImageSize')
                    .html('<sup>*</sup> Image max width 600px')
                    .css('color', '#484848');
            },
            onComplete: function (id, fileName, responseJSON) {
                if (responseJSON.success == false) {
                    console.log("success fail " + fileName);
                    $('#couponImageSize')
                        .html(responseJSON.reason)
                        .css('color', 'red');
                }

                if (responseJSON.success == true) {
                    console.log("success true " + fileName);
                    $(this.getItemByFileId(id)).hide('slow');
                }
            }
        },
        request: {
            endpoint: ctx + '/business/upload.htm',
            customHeaders: {
                Accept: 'multipart/form-data',
                'X-CSRF-TOKEN': $("meta[name='_csrf']").attr("content")
            },
            params : {
                campaignId : document.getElementById('campaignId').value,
                bizId : document.getElementById('bizId').value
            }
        },
        multiple: false,
        validation: {
            allowedExtensions: ['jpeg', 'jpg', 'gif', 'png'],
            sizeLimit: 10485760 // 10 MB in bytes
        },
        text: {
            uploadButton: '&uarr; &nbsp; UPLOAD COUPON'
        },
        showMessage: function (message) {
            $('#fine-uploader-validation').append('<div class="alert-error">' + message + '</div>');
        }
    });
    //Fineuploader ends
});