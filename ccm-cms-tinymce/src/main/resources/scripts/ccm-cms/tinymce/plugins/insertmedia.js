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

    editor.windowManager.open({
        body: [
            {
                items: [
                    {
                        label: "Search term",
                        name: "searchterm",
                        onPostRender: function()  {
                            this.getEl().value = "Hello there";
                        },
                        type: "textbox",
                    },
                    {
                        html: `<ul><li id="alpha">eins</li><li id="bravo">zwei</li></ul>`,
                        onClick: function(event) {
                            alert(`${event.target.id} clicked.`);
                        },
                        type: "container",
                    }
                ],
                name: "insertmedia_dialog_container",
                type: "container",
            },
        ],
        onsubmit: function(event) {

        },
        // onPostRender: function(event) {
        //     console.log(`this = ${this}`);
        //     console.log(`this.getEl = ${this.getEl}`);
        //     this.getEl("searchterm").value = "Hello there";
        // },
        title: "Insert media",
    });

    console.log("Dialog opened.");
    // const dialog = editor.windowManager.open(
    //         body: [
    //             {
    //                 items: [
    //                     {
    //                         label: "Search term",
    //                         name: "searchterm",
    //                         type: "textbox",
    //                     },
    //                 ],
    //                 name: "insertmedia_dialog_container",
    //                 type: "container",
    //             },
    //         ],
    //         onsubmit: function(event) {
    //
    //         },
    //         onPostRender: function() {
    //             this.getEl("searchterm").value = "Hello there";
    //         },
    //         title: "Insert media",
    // );

    // console.log("Dialog opened.");

    // dialog.add(
    //     [
    //         {
    //             label: "Group",
    //             name: "group",
    //             type: "textbox",
    //         },
    //     ],
    // );
    //
    // console.log("Element added");

    // dialog.insert([
    //     {
    //         label: "Group",
    //         name: "group",
    //         type: "textbox",
    //     },
    // ],
    // dialog.items().length,
    // false);
    //
    // console.log("element inserted");
}
