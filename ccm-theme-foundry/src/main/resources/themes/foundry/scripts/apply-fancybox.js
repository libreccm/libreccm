$(document).ready(function() {
    $("a.imageZoom").fancybox({'type': 'image'});
    $("a.imageGallery").fancybox({
        type: 'image',
        helpers: {
            title: {
                type: 'inside',
            },
            buttons: {
                position: 'bottom',
            }
        }
    });
});