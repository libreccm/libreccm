import "bootstrap";
import * as $ from "jquery";
import { Editor } from "@tiptap/core";
import StarterKit from "@tiptap/starter-kit";

document.addEventListener("DOMContentLoaded", function (event) {
    const viewButtons = document.querySelectorAll(
        ".cms-editor .cms-editor-variants .cms-editor-view-button"
    );
    for (let i = 0; i < viewButtons.length; i++) {
        viewButtons[i].addEventListener("click", event =>
            showViewDialog(event)
        );
    }

    const editButtons = document.querySelectorAll(
        ".cms-editor .cms-editor-variants .cms-editor-edit-button"
    );
    for (let i = 0; i < editButtons.length; i++) {
        editButtons[i].addEventListener("click", event =>
            showEditDialog(event)
        );
    }

    // document
    //     .querySelector(
    //         ".cms-editor .cms-editor-variants .cms-editor-view-button"
    //     )
    //     .addEventListener("click", function (event) {
    //         event.preventDefault();

    //         const target = event.currentTarget as Element;
    //         const variantUrl = target.getAttribute("data-variant-url");
    //         const viewDialogId = target.getAttribute("data-view-dialog");

    //         fetch(variantUrl, {
    //             method: "GET",
    //             credentials: "include"
    //         })
    //             .then(response => {
    //                 if (response.ok) {
    //                     response
    //                         .text()
    //                         .then(text => {
    //                             const viewDialog = document.querySelector(
    //                                 `#${viewDialogId}`
    //                             );
    //                             const viewDialogBody =
    //                                 viewDialog.querySelector(".modal-body");

    //                             viewDialogBody.textContent = text;

    //                             $(`#${viewDialogId}`).modal("toggle");
    //                         })
    //                         .catch(err => {
    //                             showMessage(
    //                                 "#cms-editor-msg-variant-load-failed"
    //                             );
    //                         });
    //                 } else {
    //                     showMessage("#cms-editor-msg-variant-load-failed");
    //                 }
    //             })
    //             .catch(err => {
    //                 showMessage("#cms-editor-msg-variant-load-failed");
    //             });
    //     });

    // document
    //     .querySelector(
    //         ".cms-editor .cms-editor-variants .cms-editor-edit-button"
    //     )
    //     .addEventListener("click", function (event) {
    //         event.preventDefault();

    //         const target = event.currentTarget as Element;
    //         const locale = target.getAttribute("data-locale");
    //         const variantUrl = target.getAttribute("data-variant-url");
    //         const editDialogId = target.getAttribute("data-edit-dialog");
    //         const saveUrl = target.getAttribute("data-save-url");

    //         fetch(variantUrl, {
    //             method: "GET",
    //             credentials: "include"
    //         })
    //             .then(response => {
    //                 if (response.ok) {
    //                     response
    //                         .text()
    //                         .then(text => {
    //                             const editDialog = document.querySelector(
    //                                 `#${editDialogId}`
    //                             );
    //                             const tiptapDiv = editDialog.querySelector(
    //                                 ".modal-body .cms-tiptap-editor"
    //                             );
    //                             if (!tiptapDiv) {
    //                                 console.warn("tiptapDiv is null");
    //                             }

    //                             const editor = new Editor({
    //                                 element: tiptapDiv,
    //                                 extensions: [StarterKit],
    //                                 content: text
    //                             });

    //                             const buttonsDiv = editDialog.querySelector(
    //                                 ".cms-tiptap-editor-buttons"
    //                             );
    //                             if (!buttonsDiv) {
    //                                 console.warn("buttonsDiv is null.");
    //                             }
    //                             const emphButton =
    //                                 buttonsDiv.querySelector(".tiptap-emph");
    //                             if (!emphButton) {
    //                                 console.warn("emphButton not found.");
    //                             }
    //                             emphButton.addEventListener("click", event => {
    //                                 event.preventDefault();
    //                                 editor.chain().focus().toggleItalic().run();
    //                             });

    //                             const strongEmphButton =
    //                                 buttonsDiv.querySelector(
    //                                     ".tiptap-strong-emph"
    //                                 );
    //                             if (!strongEmphButton) {
    //                                 console.warn("strongEmphButton not found.");
    //                             }
    //                             strongEmphButton.addEventListener(
    //                                 "click",
    //                                 event => {
    //                                     event.preventDefault();
    //                                     editor
    //                                         .chain()
    //                                         .focus()
    //                                         .toggleBold()
    //                                         .run();
    //                                 }
    //                             );

    //                             const closeButton = editDialog.querySelector(
    //                                 ".modal-header .close"
    //                             );
    //                             const cancelButton = editDialog.querySelector(
    //                                 ".modal-footer .cms-editor-cancel-button"
    //                             );
    //                             const saveButton = editDialog.querySelector(
    //                                 ".modal-footer .cms-editor-save-button"
    //                             );

    //                             closeButton.addEventListener("click", event => {
    //                                 editor.chain().clearContent();
    //                                 editor.destroy();
    //                                 $(`#${editDialogId}`).modal("toggle");
    //                             });

    //                             cancelButton.addEventListener(
    //                                 "click",
    //                                 event => {
    //                                     editor.chain().clearContent();
    //                                     editor.destroy();
    //                                     $(`#${editDialogId}`).modal("toggle");
    //                                 }
    //                             );

    //                             saveButton.addEventListener("click", event => {
    //                                 const html = editor.getHTML();
    //                                 const params = new URLSearchParams();
    //                                 params.append("value", html);
    //                                 fetch(saveUrl, {
    //                                     method: "POST",
    //                                     credentials: "include",
    //                                     headers: {
    //                                         "Content-Type":
    //                                             "application/x-www-form-urlencoded"
    //                                     },
    //                                     body: params
    //                                 })
    //                                     .then(saveResponse => {
    //                                         if (saveResponse.ok) {
    //                                             showMessage(
    //                                                 "#cms-editor-msg-save-successful"
    //                                             );
    //                                             window.location.reload();
    //                                         } else {
    //                                             showMessage(
    //                                                 "#cms-editor-msg-save-failed"
    //                                             );
    //                                         }
    //                                         $(`#${editDialogId}`).modal(
    //                                             "toggle"
    //                                         );
    //                                     })
    //                                     .catch(err => {
    //                                         showMessage(
    //                                             "#cms-editor-msg-save-failed"
    //                                         );
    //                                         console.error(err);
    //                                         $(`#${editDialogId}`).modal(
    //                                             "toggle"
    //                                         );
    //                                     });
    //                             });

    //                             $(`#${editDialogId}`).modal("toggle");
    //                         })
    //                         .catch(err => {
    //                             showMessage(
    //                                 "#cms-editor-msg-variant-load-failed"
    //                             );
    //                             console.error(err);
    //                         });
    //                 } else {
    //                     showMessage("#cms-editor-msg-variant-load-failed");
    //                 }
    //             })
    //             .catch(err => {
    //                 showMessage("#cms-editor-msg-variant-load-failed");
    //                 console.error(err);
    //             });
    //     });

    // console.log("Starting editor");
    // new Editor({
    //     element: document.querySelector('#cms-editor'),
    //     extensions: [
    //         StarterKit
    //     ],
    //     content: '<h1>Hello World</h1>'
    // })
});

function closeEditor(event: Event, editor: Editor, editDialogId: string) {
    event.preventDefault();

    editor.chain().clearContent();
    editor.destroy();
    const editDialog = $(`#${editDialogId}`) as any;
    editDialog.modal("toggle");
}

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

function initEditorButtons(editor: Editor, buttonsElem: Element) {
    // const emphButton: HTMLButtonElement | null = buttonsElem.querySelector(
    //     ".tiptap-emph"
    // );
    // if(emphButton) {
    //     emphButton.addEventListener("click", event => {
    //         event.preventDefault();
    //         editor.chain().focus().toggleItalic().run();
    //     });
    // }
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
        extensions: [StarterKit],
        content: variant
    });

    const editorButtons = document.querySelector(".cms-tiptap-editor-buttons");
    if (!editorButtons) {
        console.error("editorButtons are null");
        return;
    }
    initEditorButtons(editor, editorButtons);

    const editDialogHeader = editDialog.querySelector(".modal-header .close");
    if (editDialogHeader) {
        editDialogHeader.addEventListener("click", event =>
            closeEditor(event, editor, editDialogId)
        );
    }
    const cancelButton = editDialog.querySelector(
        ".modal-footer .cms-editor-cancel-button"
    );
    if (!cancelButton) {
        console.error("cancelButton is null");
        return;
    }
    cancelButton.addEventListener("click", event =>
        closeEditor(event, editor, editDialogId)
    );
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
