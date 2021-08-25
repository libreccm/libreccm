import "bootstrap";
import * as $ from "jquery";
import { Editor } from "@tiptap/core";
import Gapcursor from "@tiptap/extension-gapcursor";
import StarterKit from "@tiptap/starter-kit";
import Subscript from "@tiptap/extension-subscript";
import Superscript from "@tiptap/extension-superscript";
import Table from "@tiptap/extension-table";
import TableRow from "@tiptap/extension-table-row";
import TableCell from "@tiptap/extension-table-cell";
import TableHeader from "@tiptap/extension-table-header";

document.addEventListener("DOMContentLoaded", function (event) {
    // const viewButtons = document.querySelectorAll(
    //     ".cms-editor .cms-editor-variants .cms-editor-view-button"
    // );
    // for (let i = 0; i < viewButtons.length; i++) {
    //     viewButtons[i].addEventListener("click", event =>
    //         showViewDialog(event)
    //     );
    // }

    // const editButtons = document.querySelectorAll(
    //     ".cms-editor .cms-editor-variants .cms-editor-edit-button"
    // );
    // for (let i = 0; i < editButtons.length; i++) {
    //     editButtons[i].addEventListener("click", event =>
    //         showEditDialog(event)
    //     );
    // }

    console.log("Trying to init editor...");
    const editor = document.querySelector(".cms-tiptap-editor");
    if (editor) {
        initEditor(editor as HTMLElement)
            .then(() => console.log("editor initalized."))
            .catch(error => console.log(`Failed to init editor ${error}`));
    } else {
        console.log("No editor found.");
    }
});

// function closeEditor(event: Event, editor: Editor, editDialogId: string) {
//     event.preventDefault();

//     editor.chain().clearContent();
//     editor.destroy();
//     const editDialog = $(`#${editDialogId}`) as any;
//     editDialog.modal("toggle");
// }

async function fetchVariant(fromUrl: string) {
    try {
        const response = await fetch(fromUrl, {
            method: "GET",
            credentials: "include"
        });

        if (response.ok) {
            return await response.text();
        } else {
            showLoadVariantFailedMessage(response.status, response.statusText);
        }
    } catch (err) {
        showLoadVariantFailedErrMessage(err);
    }
}

async function fetchWordCount(fromUrl: string) {
    try {
        const response = await fetch(fromUrl, {
            method: "GET",
            credentials: "include"
        });

        if (response.ok) {
            return await response.text();
        } else {
            return "?";
        }
    } catch (err) {
        return "?";
    }
}

async function initEditor(editorElem: HTMLElement) {
    console.log("init editor");
    const variantUrl = editorElem.getAttribute("data-variant-url");
    if (variantUrl == null) {
        console.error("variantUrl is null");
        return;
    }
    console.log(`variantUrl = ${variantUrl}`);
    const variant = await fetchVariant(variantUrl);
    console.log("Got variant");

    const canvasElem = editorElem.querySelector(".cms-tiptap-editor-canvas");
    if (!canvasElem) {
        console.error("canvasElem not found.");
        return;
    }

    const editor = new Editor({
        element: canvasElem,
        extensions: [
            Gapcursor,
            StarterKit,
            Subscript,
            Superscript,
            Table.configure({ resizable: true }),
            TableRow,
            TableHeader,
            TableCell
        ],
        content: variant
    });

    console.log("initializing editor buttons");
    const buttonsElem = editorElem.querySelector(".cms-tiptap-editor-buttons");
    if (buttonsElem) {
        initEditorButtons(editor, buttonsElem);
    } else {
        console.error("editorButtons are null");
        return;
    }
}

function initEditorButtons(editor: Editor, buttonsElem: Element) {
    buttonsElem
        .querySelector(".tiptap-emph")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleItalic().run();
        });

    buttonsElem
        .querySelector(".tiptap-strong-emph")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleBold().run();
        });
    buttonsElem
        .querySelector(".tiptap-code")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleCode().run();
        });
    buttonsElem
        .querySelector(".tiptap-strikethrough")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleStrike().run();
        });
    buttonsElem
        .querySelector(".tiptap-subscript")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleSubscript().run();
        });
    buttonsElem
        .querySelector(".tiptap-superscript")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleSuperscript().run();
        });

    for (let i = 1; i <= 6; i++) {
        buttonsElem
            .querySelector(`.tiptap-h${i}`)
            ?.addEventListener("click", event => {
                event.preventDefault();
                editor.chain().focus().toggleHeading({ level: i }).run();
            });
    }

    buttonsElem
        .querySelector(".tiptap-paragraph")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().clearNodes().run();
        });

    buttonsElem
        .querySelector(".tiptap-blockquote")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleBlockquote().run();
        });

    buttonsElem
        .querySelector(".tiptap-codeblock")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleCodeBlock().run();
        });

    buttonsElem
        .querySelector(".tiptap-ul")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleBulletList().run();
        });

    buttonsElem
        .querySelector(".tiptap-ol")
        ?.addEventListener("click", event => {
            event.preventDefault();
            editor.chain().focus().toggleOrderedList().run();
        });

    editor.on("selectionUpdate", ({ editor }) => {
        console.log("Selection updated");
        console.log(
            `can insertRowBefore: ${editor
                .can()
                .chain()
                .focus()
                .addRowBefore()
                .run()}`
        );
    });

    buttonsElem
        .querySelector(".cms-editor-insert-table-dialog .btn-success")
        ?.addEventListener("click", event => {
            event.preventDefault();
            const dialog = buttonsElem.querySelector(
                ".cms-editor-insert-table-dialog"
            );
            if (!dialog) {
                return;
            }
            const rowsInput = dialog.querySelector(
                "input#rows"
            ) as HTMLInputElement;
            const colsInput = dialog.querySelector(
                "input#cols"
            ) as HTMLInputElement;
            const headerRowInput = dialog.querySelector(
                "input#headerRow"
            ) as HTMLInputElement;
            console.log(`rowsInput = ${rowsInput}`);
            console.log(`colsInput = ${colsInput}`);
            console.log(`headerRowInput = ${headerRowInput}`);
            const rows = rowsInput.value;
            const cols = colsInput.value;
            const headerRow = JSON.parse(headerRowInput.value) as Boolean;
            editor
                .chain()
                .focus()
                .insertTable({ cols: cols, rows: rows, headerRow: headerRow })
                .run();
            $("#insert-table-dialog").modal("hide");
        });
}

async function showEditDialog(event: Event) {
    event.preventDefault();

    const target = event.currentTarget as Element;
    const locale = target.getAttribute("data-locale");
    if (!locale) {
        console.error("locale is null");
        return;
    }
    const variantUrl = target.getAttribute("data-variant-url");
    if (variantUrl == null) {
        console.error("variantUrl is null");
        return;
    }
    const editDialogId = target.getAttribute("data-edit-dialog");
    if (!editDialogId) {
        console.error("editDialogId is null");
        return;
    }
    const saveUrl = target.getAttribute("data-save-url");
    if (!saveUrl) {
        console.error("saveUrl is null");
        return;
    }
    const wordCountUrl = target.getAttribute("data-wordcount-url");
    if (!wordCountUrl) {
        console.error("wordCountUrl is null");
        return;
    }

    const variant = await fetchVariant(variantUrl);

    const editDialog = document.querySelector(`#${editDialogId}`);
    if (!editDialog) {
        console.error("editDialog is null");
        return;
    }
    const tiptapDiv = editDialog.querySelector(
        ".modal-body .cms-tiptap-editor"
    );
    if (!tiptapDiv) {
        console.warn("tiptapDiv is null");
        return;
    }

    const editor = new Editor({
        element: tiptapDiv,
        extensions: [
            Gapcursor,
            StarterKit,
            Subscript,
            Superscript,
            Table.configure({ resizable: true }),
            TableRow,
            TableHeader,
            TableCell
        ],
        content: variant
    });

    const editorButtons = document.querySelector(".cms-tiptap-editor-buttons");
    if (!editorButtons) {
        console.error("editorButtons are null");
        return;
    }
    initEditorButtons(editor, editorButtons);

    // const editDialogHeader = editDialog.querySelector(".modal-header .close");
    // if (editDialogHeader) {
    //     editDialogHeader.addEventListener("click", event =>
    //         closeEditor(event, editor, editDialogId)
    //     );
    // }
    const cancelButton = editDialog.querySelector(
        ".modal-footer .cms-editor-cancel-button"
    );
    if (!cancelButton) {
        console.error("cancelButton is null");
        return;
    }
    // cancelButton.addEventListener("click", event =>
    //     closeEditor(event, editor, editDialogId)
    // );
    const editButton = editDialog.querySelector(
        ".modal-footer .cms-editor-save-button"
    );
    if (!editButton) {
        console.error("editButton is null");
        return;
    }
    editButton.addEventListener("click", event =>
        save(event, editor, editDialogId, saveUrl, locale, wordCountUrl)
    );

    const editDialogJquery = $(`#${editDialogId}`) as any;
    editDialogJquery.modal("toggle");
}

async function save(
    event: Event,
    editor: Editor,
    editDialogId: string,
    saveUrl: string,
    locale: string,
    wordCountUrl: string
) {
    event.preventDefault();

    const params = new URLSearchParams();
    params.append("value", editor.getHTML());

    try {
        const response = await fetch(saveUrl, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params
        });
        if (response.ok) {
            showSaveSuccessfulMessage();
        } else {
            showSaveFailedMessage(response.status, response.statusText);
        }
    } catch (err) {
        showSaveFailedErrMessage(err);
    }

    const editDialogJquery = $(`#${editDialogId}`) as any;
    editDialogJquery.modal("toggle");

    const wordCount = await fetchWordCount(wordCountUrl);
    const wordCountSpan = document.querySelector(
        `tr#variant-${locale} .wordcount`
    );
    if (wordCountSpan) {
        wordCountSpan.textContent = wordCount;
    }
}

function showLoadVariantFailedErrMessage(err: string) {
    showMessage("#cms-editor-msg-variant-load-failed");
    console.error(err);
}

function showLoadVariantFailedMessage(status: number, statusText: string) {
    showMessage("#cms-editor-msg-variant-load-failed");
    console.error(`HTTP Status: ${status}, statusText: ${statusText}`);
}

function showMessage(messageId: string) {
    const template = document.querySelector(messageId) as HTMLTemplateElement;
    const msg = template.content.cloneNode(true);
    document.querySelector(".cms-editor-messages")?.append(msg);
}

function showSaveFailedErrMessage(err: string) {
    showMessage("#cms-editor-msg-save-failed");
    console.error(err);
}

function showSaveFailedMessage(status: number, statusText: string) {
    showMessage("#cms-editor-msg-save-failed");
    console.error(`HTTP Status: ${status}, statusText: ${statusText}`);
}

function showSaveSuccessfulMessage() {
    showMessage("#cms-editor-msg-save-successful");
}

async function showViewDialog(event: Event) {
    event.preventDefault();

    const target = event.currentTarget as Element;
    const variantUrl = target.getAttribute("data-variant-url");
    if (!variantUrl) {
        console.error("variantUrl is null");
        return;
    }
    const viewDialogId = target.getAttribute("data-view-dialog");

    const variant = await fetchVariant(variantUrl);
    if (!variant) {
        console.error("variant is null");
        return;
    }

    const viewDialog = document.querySelector(`#${viewDialogId}`);
    if (!viewDialog) {
        console.error("viewDialog is null");
        return;
    }

    const viewDialogBody = viewDialog.querySelector(".modal-body");
    if (!viewDialogBody) {
        console.error("viewDialogBody is null");
        return;
    }

    viewDialogBody.innerHTML = variant;

    const viewDialogJquery = $(`#${viewDialogId}`) as any;
    viewDialogJquery.modal("toggle");
}
