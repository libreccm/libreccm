import * as tinymce from "tinymce";

const plugin = (editor: tinymce.Editor, url: string): void => {
    editor.addButton(
        "insert-media",
        {
            text: "Insert media",
            icon: false,
            onclick: function() {
                openDialog(editor);
            }
        }
    );

    editor.addMenuItem(
        "insert-media",
        {
            text: "Insert media",
            context: "tools",
            onclock: function() {
                openDialog(editor);
            }
        }
    );
};

// tinymce.PluginManager.add("ccm-cms-insertmedia",
//                           function(editor: tinymce.Editor,
//                                    url: string): void {
//
//     editor.addButton(
//         "insert-media",
//         {
//             text: "Insert media",
//             icon: false,
//             onclick: function() {
//                 openDialog(editor);
//             }
//         }
//     );
//
//     editor.addMenuItem(
//         "insert-media",
//         {
//             text: "Insert media",
//             context: "tools",
//             onclock: function() {
//                 openDialog(editor);
//             }
//         }
//     );
// });

function openDialog(editor: tinymce.Editor): void {

    editor.windowManager.open(
        {},
        {
            title: "Insert media",
            body: [
                { label: "Search term", name: "searchterm", type: "textbox" }
            ],
            onsubmit: function(event: Event) {

            },
    });
}

export default plugin;
