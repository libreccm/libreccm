requirejs(["./ccm-editor",
            "../webjars/requirejs-domready/2.0.1/domReady!"],
          function(editor, doc) {

    editor.addEditor(".editor-textarea", {
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
            "ccm-editor-css.path": "/libreccm/ccm-editor/ccm-editor.css",
            "font-awesome.path": "/libreccm/webjars/font-awesome/4.7.0/css/font-awesome.min.css",
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
