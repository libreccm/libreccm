requirejs(["./ccm-editor",
            "../node_modules/requirejs-domready/domReady!"],
          function(editor, doc) {

    editor.addEditor(".editor", {
        "actionGroups": [
            {
                    "name": "blocks",
                    "title": "Format blocks",
                    "actions": [
                        editor.FormatBlockAction
                    ]
            },
            {
                "name": "format-text",
                "title": "Format text",
                "actions": [
                    editor.MakeBoldAction,
                    editor.MakeItalicAction,
                    editor.MakeUnderlineAction,
                    editor.StrikeThroughAction,
                    editor.SubscriptAction,
                    editor.SuperscriptAction,
                    editor.RemoveFormatAction,
                    editor.InsertExternalLinkAction
                ]
            },
            {
                    "name": "insert-list",
                    "title": "Insert list",
                    "actions": [
                        editor.InsertUnorderedListAction,
                        editor.InsertOrderedListAction
                    ]
            },
            {
                "name": "html",
                "title": "HTML",
                "actions": [editor.ToggleHtmlAction]
            }
        ],
        "settings": {
            "formatBlock.blocks": [
                {
                    "element": "h3",
                    "title": "Heading 3"
                },
                {
                    "element": "h4",
                    "title": "Heading 4"
                },
                {
                    "element": "h5",
                    "title": "Heading 5"
                },
                {
                    "element": "h6",
                    "title": "Heading 6"
                },
                {
                    "element": "p",
                    "title": "Paragraph"
                }
            ]
        }
    });
});
