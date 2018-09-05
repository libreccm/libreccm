tinymce.PluginManager.add("ccm-cms-insertimage", function(editor, url) {

    console.log("Adding plugin ccm-cms-insertimage...");

    editor.addButton(
        "insertimage",
        {
            icon: "image",
            onclick: function() {
                openDialog(editor);
            },
            tooltip: "Insert image",
        },
    );

    editor.addMenuItem(
        "insert-image",
        {
            text: "Insert image",
            context: "tools",
            onclick: function() {
                openDialog(editor);
            },
        },
    );

});

function openDialog(editor) {

    console.log("Opening dialog");

    editor.windowManager.open({
        body: [
            {
                items: [
                    {
                        direction: "row",
                        items: [
                            {
                                disabled: true,
                                label: "Image",
                                name: "imageUuid",
                                type: "textbox",
                                value: "foobar",
                            },
                            {
                                name: "browse",
                                onclick: function()  {
                                    openBrowseDialog(editor);
                                },
                                text: "Browse",
                                type: "button",
                            },
                        ],
                        label: "Image",
                        layout: "flex",
                        type: "container",
                    },
                    {
                        label: "Caption",
                        name: "caption",
                        type: "textbox",
                    },
                    {
                        label: "Width",
                        name: "width",
                        size: 4,
                        type: "textbox",
                    },
                    {
                        label: "Height",
                        name: "height",
                        size: 4,
                        type: "textbox",
                    },
                    {
                        name: "lightbox",
                        text: "Lightbox?",
                        type: "Checkbox",
                    },
                    {
                        border: "1 1 1 1",
                        name: "imagePanel",
                        type: "container",
                    },
                ],
                minHeight: 200,
                minWidth: 400,
                name: "insertimage_dialog_container",
                onPostRender: function() {
                    // console.log(`imageUuid = ${this.find("#imageUuid").value()}`);
                    const imageUuid = this.find("#imageUuid").value();
                    console.log(`imageUuid = ${imageUuid}`);
                    // this.find("#imagePanel").innerHTML = `<pre>${imageUuid}</pre>`;
                    this.find("#imagePanel").append([
                        {
                            html: `<pre>${imageUuid}</pre>`,
                            // text: `${imageUuid}`,
                            // type: "label",
                            type: "container",
                        }
                    ]);
                    console.log(this.find("#imagePanel"));
                },
                type: "form",
            },
        ],
        onsubmit: function(event) {

        },
        title: "Insert image",
    });

    console.log("Dialog opened.");
}

function openBrowseDialog(editor) {

    editor.windowManager.open({

        body: [
            {
                items: [
                    {
                        text: "foo",
                        type: "label",
                    }
                ],
                layout: "flow",
                minHeigth: 400,
                minWidth: 400,
                onPostRender: function() {
                    console.log(`contentSection: ${editor.getParam("contentsection")}`);
                    console.log(`contextPrefix: ${editor.getParam("contextprefix")}`);
                },
                type: "container",
            }
        ],
        title: "Select image",
    });
}
