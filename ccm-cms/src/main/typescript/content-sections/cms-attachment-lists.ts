// import Sortable = require("sortablejs")
import Sortable from "sortablejs";

document.addEventListener("DOMContentLoaded", function (event) {
    const attachmentLists = document.querySelectorAll(".cms-attachment-lists");

    for (let i = 0; i < attachmentLists.length; i++) {
        initAttachmentList(attachmentLists[i] as HTMLElement);
    }

    const attachments = document.querySelectorAll(".cms-attachments");

    for (let i = 0; i < attachments.length; i++) {
        initAttachments(attachments[i] as HTMLElement);
    }
});

function initAttachmentList(attachmentList: HTMLElement) {
    new Sortable(attachmentList, {
        animation: 150,
        group: "cms-attachment-lists",
        handle: ".cms-sort-handle"
    });
}

function initAttachments(attachments: HTMLElement) {
    new Sortable(attachments,
        {
            animation: 150,
            group: "cms-attachments",
            handle: ".cms-sort-handle"
        });
}
