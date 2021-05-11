import "bootstrap";
import * as $ from "jquery";
import { Editor } from "@tiptap/core";
import StarterKit from "@tiptap/starter-kit";

function showMessage(messageId: string) {
    const template = document.querySelector(messageId) as HTMLTemplateElement;
    const msg = template.content.cloneNode(true);
    document.querySelector(".cms-editor-messages").append(msg);
}

document.addEventListener("DOMContentLoaded", function (event) {
    document
        .querySelector(
            ".cms-editor .cms-editor-variants .cms-editor-view-button"
        )
        .addEventListener("click", function (event) {
            event.preventDefault();

            const target = event.currentTarget as Element;
            const variantUrl = target.getAttribute("data-variant-url");
            const viewDialogId = target.getAttribute("data-view-dialog");

            fetch(variantUrl, {
                method: "GET",
                credentials: "include"
            })
                .then(response => {
                    if (response.ok) {
                        response
                            .text()
                            .then(text => {
                                const viewDialog = document.querySelector(
                                    `#${viewDialogId}`
                                );
                                const viewDialogBody = viewDialog.querySelector(
                                    ".modal-body"
                                );

                                viewDialogBody.textContent = text;

                                $(`#${viewDialogId}`).modal("toggle");
                            })
                            .catch(err => {
                                showMessage(
                                    "#cms-editor-msg-variant-load-failed"
                                );
                            });
                    } else {
                        showMessage("#cms-editor-msg-variant-load-failed");
                    }
                })
                .catch(err => {
                    showMessage("#cms-editor-msg-variant-load-failed");
                });
        });

    document
        .querySelector(
            ".cms-editor .cms-editor-variants .cms-editor-edit-button"
        )
        .addEventListener("click", function (event) {
            event.preventDefault();

            const target = event.currentTarget as Element;
            const variantUrl = target.getAttribute("data-variant-url");
            const editDialogId = target.getAttribute("data-edit-dialog");
            const saveUrl = target.getAttribute("data-save-url");

            fetch(variantUrl, {
                method: "GET",
                credentials: "include"
            })
                .then(response => {
                    if (response.ok) {
                        response
                            .text()
                            .then(text => {
                                const editDialog = document.querySelector(
                                    `#${editDialogId}`
                                );
                                const tiptapDiv = editDialog.querySelector(
                                    ".modal-body .cms-tiptap-editor"
                                );
                                if (!tiptapDiv) {
                                    console.warn("tiptapDiv is null");
                                }

                                const editor = new Editor({
                                    element: tiptapDiv,
                                    extensions: [StarterKit],
                                    content: text
                                });

                                const buttonsDiv = editDialog.querySelector(
                                    ".cms-tiptap-editor-buttons"
                                );
                                if (!buttonsDiv) {
                                    console.warn("buttonsDiv is null.");
                                }
                                const emphButton = buttonsDiv.querySelector(
                                    ".tiptap-emph"
                                );
                                if (!emphButton) {
                                    console.warn("emphButton not found.");
                                }
                                emphButton.addEventListener("click", event => {
                                    event.preventDefault();
                                    editor.chain().focus().toggleItalic().run();
                                });

                                const strongEmphButton = buttonsDiv.querySelector(
                                    ".tiptap-strong-emph"
                                );
                                if (!strongEmphButton) {
                                    console.warn("strongEmphButton not found.");
                                }
                                strongEmphButton.addEventListener(
                                    "click",
                                    event => {
                                        event.preventDefault();
                                        editor
                                            .chain()
                                            .focus()
                                            .toggleBold()
                                            .run();
                                    }
                                );

                                const closeButton = editDialog.querySelector(
                                    ".modal-header .close"
                                );
                                const cancelButton = editDialog.querySelector(
                                    ".modal-footer .cms-editor-cancel-button"
                                );
                                const saveButton = editDialog.querySelector(
                                    ".modal-footer .cms-editor-save-button"
                                );

                                closeButton.addEventListener("click", event => {
                                    editor.chain().clearContent();
                                    editor.destroy();
                                    $(`#${editDialogId}`).modal("toggle");
                                });

                                cancelButton.addEventListener(
                                    "click",
                                    event => {
                                        editor.chain().clearContent();
                                        editor.destroy();
                                        $(`#${editDialogId}`).modal("toggle");
                                    }
                                );

                                saveButton.addEventListener("click", event => {
                                    const html = editor.getHTML();
                                    const formData = new FormData();
                                    formData.append("value", html);
                                    fetch(saveUrl, {
                                        method: "POST",
                                        credentials: "include",
                                        headers: {
                                            "Content-Type": "application/x-www-form-urlencoded"
                                        },
                                        body: formData
                                    })
                                        .then(saveResponse => {
                                            if (saveResponse.ok) {
                                                showMessage(
                                                    "#cms-editor-msg-save-successful"
                                                );
                                            } else {
                                                showMessage(
                                                    "#cms-editor-msg-save-failed"
                                                );
                                            }
                                            $(`#${editDialogId}`).modal(
                                                "toggle"
                                            );
                                        })
                                        .catch(err => {
                                            showMessage(
                                                "#cms-editor-msg-save-failed"
                                            );
                                            console.error(err);
                                            $(`#${editDialogId}`).modal(
                                                "toggle"
                                            );
                                        });
                                });

                                $(`#${editDialogId}`).modal("toggle");
                            })
                            .catch(err => {
                                showMessage(
                                    "#cms-editor-msg-variant-load-failed"
                                );
                                console.error(err);
                            });
                    } else {
                        showMessage("#cms-editor-msg-variant-load-failed");
                    }
                })
                .catch(err => {
                    showMessage("#cms-editor-msg-variant-load-failed");
                    console.error(err);
                });
        });

    // console.log("Starting editor");
    // new Editor({
    //     element: document.querySelector('#cms-editor'),
    //     extensions: [
    //         StarterKit
    //     ],
    //     content: '<h1>Hello World</h1>'
    // })
});
