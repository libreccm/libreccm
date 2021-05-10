import "bootstrap";
import * as $ from "jquery";
import { Editor } from "@tiptap/core";
import StarterKit from "@tiptap/starter-kit";

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

                                $(`#${viewDialogId}`).modal('toggle');
                            })
                            .catch(err => {
                                const template = document.querySelector(
                                    "#cms-editor-msg-variant-load-failed"
                                ) as HTMLTemplateElement;
                                const msg = template.content.cloneNode(true);
                                document
                                    .querySelector(".cms-editor-messages")
                                    .append(msg);
                            });
                    }
                })
                .catch(err => {
                    const template = document.querySelector(
                        "#cms-editor-msg-variant-load-failed"
                    ) as HTMLTemplateElement;
                    const msg = template.content.cloneNode(true);
                    document.querySelector(".cms-editor-messages").append(msg);
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
