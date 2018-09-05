document.addEventListener("DOMContentLoaded", function() {
    tinymce.init({
        contentsection: document.querySelector(".tinymce").getAttribute("data-contentsection"),
        contextprefix: document.querySelector(".tinymce").getAttribute("data-contextprefix"),
        menubar: "edit view insert format tools table",
        plugins: "ccm-cms-insertimage code lists nonbreaking noneditable paste searchreplace table template visualblocks wordcount",
        selector: ".tinymce",
        toolbar: "undo redo formatselect bold italic underline strikethrough alignleft aligncenter alignright alignjustify alignnone insert indent outdent blockquote bullist numlist link unlink openlink insertimage table",
    });
});
