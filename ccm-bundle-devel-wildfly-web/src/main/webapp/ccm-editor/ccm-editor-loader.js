requirejs(["./ccm-editor",
            "../webjars/requirejs-domready/2.0.1/domReady!"],
          function(editor, doc) {

    editor.addEditor(".editor-textarea", {
        "commandGroups": [
            {
                    "name": "blocks",
                    "title": "Format blocks",
                    "commands": [
                        editor.FormatBlockCommand
                    ]
            },
            {
                "name": "format-text",
                "title": "Format text",
                "commands": [
                    editor.MakeBoldCommand,
                    editor.MakeItalicCommand,
                    editor.MakeUnderlineCommand,
                    editor.StrikeThroughCommand,
                    editor.SubscriptCommand,
                    editor.SuperscriptCommand,
                    editor.RemoveFormatCommand,
                    editor.InsertExternalLinkCommand
                ]
            },
            {
                    "name": "insert-list",
                    "title": "Insert list",
                    "commands": [
                        editor.InsertUnorderedListCommand,
                        editor.InsertOrderedListCommand
                    ]
            },
            {
                "name": "html",
                "title": "HTML",
                "commands": [editor.ToggleHtmlCommand]
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
