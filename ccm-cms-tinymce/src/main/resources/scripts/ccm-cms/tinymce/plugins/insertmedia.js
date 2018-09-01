tinymce.PluginManager.add("ccm-cms-insertmedia", function(editor, url) {

    console.log("Adding plugin ccm-cms-insertmedia...");

    editor.addButton(
        "insertmedia",
        {
            icon: "image",
            onclick: function() {
                openDialog(editor);
            },
            tooltip: "Insert media",
        },
    );

    editor.addMenuItem(
        "insert-media",
        {
            text: "Insert media",
            context: "tools",
            onclock: function() {
                openDialog(editor);
            },
        },
    );

});

function openDialog(editor) {

    console.log("Opening dialog");

    editor.windowManager.open(
        {
            body: [
                {
                    label: "Search term",
                    name: "searchterm",
                    type: "textbox"
                },
            ],
            onsubmit: function(event) {

            },
            title: "Insert media",
    });
}
