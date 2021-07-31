// import Sortable = require("sortablejs")
import Sortable, { SortableEvent } from "sortablejs";

interface RelatedInfoStepAttachmentOrder {
    attachmentListsOrder: string[];
    attachmentsOrder: {
        [key: string]: string[];
    };
    movedAttachments: MovedAttachment[];
}

interface MovedAttachment {
    attachmentUuid: string;
    fromListUuid: string;
    toListUuid: string;
}

const movedAttachments: MovedAttachment[] = [];

let attachmentsListSortable: Sortable;
let attachmentsSortables: {
    [key: string]: Sortable;
} = {};

document.addEventListener("DOMContentLoaded", function (event) {
    const attachmentLists = document.querySelector(".cms-attachment-lists");

    if (attachmentLists) {
        attachmentsListSortable = initAttachmentLists(
            attachmentLists as HTMLElement
        );
    }

    const attachments = document.querySelectorAll(".cms-attachments");

    for (let i = 0; i < attachments.length; i++) {
        initAttachments(attachments[i] as HTMLElement);
    }

    const saveOrderButtons = document.querySelectorAll(".save-order-button");
    for (let i = 0; i < saveOrderButtons.length; i++) {
        saveOrderButtons[i].addEventListener("click", saveOrder);
    }
});

function initAttachmentLists(attachmentList: HTMLElement): Sortable {
    return new Sortable(attachmentList, {
        animation: 150,
        group: "cms-attachment-lists",
        handle: ".cms-sort-handle",
        onEnd: enableSaveButton
    });
}

function initAttachments(attachments: HTMLElement): Sortable {
    const sortable = new Sortable(attachments, {
        animation: 150,
        group: "cms-attachments",
        handle: ".cms-sort-handle",
        onEnd: moveAttachment
    });

    const listUuid = attachments.getAttribute("data-list-uuid");
    if (listUuid === null) {
        showGeneralError();
        throw Error("attachments without data-list-uuid attribute found.");
    }

    attachmentsSortables[listUuid] = sortable;

    return sortable;
}

function enableSaveButton(event: SortableEvent) {
    const saveOrderButtons = document.querySelectorAll(".save-order-button");
    for (let i = 0; i < saveOrderButtons.length; i++) {
        const saveOrderButton: HTMLButtonElement = saveOrderButtons[
            i
        ] as HTMLButtonElement;
        saveOrderButton.disabled = false;
    }
}

function moveAttachment(event: SortableEvent) {
    // console.log("event.from:");
    // console.dir(event.from);
    // console.log("event.to:");
    // console.dir(event.to);
    // console.log("event.item:");
    // console.dir(event.item);

    const fromListUuid = event.from.getAttribute("data-list-uuid");
    if (!fromListUuid) {
        showGeneralError();
        throw Error(
            "An attachment was moved, but the list from which the attachment was removed has no data-id attribute."
        );
    }
    const toListUuid = event.to.getAttribute("data-list-uuid");
    if (!toListUuid) {
        showGeneralError();
        throw Error(
            "An attachment was moved, but the list to which the attachment was removed has no data-id attribute."
        );
    }

    if (fromListUuid !== toListUuid) {
        const attachmentUuid = event.item.getAttribute("data-id");
        if (!attachmentUuid) {
            showGeneralError();
            throw Error(
                "An attachment was moved, but the attachment was removed has no dat-id attribute."
            );
        }

        const movedAttachment: MovedAttachment = {
            fromListUuid,
            toListUuid,
            attachmentUuid
        };
        movedAttachments.push(movedAttachment);
    }

    enableSaveButton(event);
}

function saveOrder() {
    const attachmentOrder: RelatedInfoStepAttachmentOrder = {
        attachmentListsOrder: attachmentsListSortable.toArray(),
        attachmentsOrder: {},
        movedAttachments
    };

    for (let key in attachmentsSortables) {
        attachmentOrder.attachmentsOrder[key] =
            attachmentsSortables[key].toArray();
    }

    console.dir(attachmentOrder);
    const cmsAttachments = document.querySelector(".cms-attachment-lists");
    if (!cmsAttachments) {
        showGeneralError();
        throw Error("cms-attachment-lists container not found.");
    }
    const baseUrl = cmsAttachments.getAttribute("data-baseUrl");
    if (!baseUrl) {
        showGeneralError();
        throw Error(
            "data-baseUrl attribute on cms-attachment-lists container is missing or empty."
        );
    }

    const saveOrderButtons = document.querySelectorAll(".save-order-button");
    for (let i = 0; i < saveOrderButtons.length; i++) {
        const saveOrderButton: HTMLButtonElement = saveOrderButtons[i] as HTMLButtonElement;
        saveOrderButton.disabled = true;
        const saveIcon = saveOrderButton.querySelector(".save-icon");
        const spinner = saveOrderButton.querySelector(".save-spinner");
        saveIcon?.classList.toggle("d-none");
        spinner?.classList.toggle("d-none");
    }

    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    fetch(baseUrl, {
        credentials: "include",
        body: JSON.stringify(attachmentOrder),
        headers,
        method: "POST"
    })
        .then(response => {
            if (response.ok) {
                // const saveOrderButtons =
                //     document.querySelectorAll(".save-order-button");
                for (let i = 0; i < saveOrderButtons.length; i++) {
                    const saveOrderButton: HTMLButtonElement = saveOrderButtons[
                        i
                    ] as HTMLButtonElement;
                    // saveOrderButton.disabled = true;
                    const saveIcon = saveOrderButton.querySelector(".save-icon");
                    const spinner = saveOrderButton.querySelector(".save-spinner");
                    saveIcon?.classList.toggle("d-none");
                    spinner?.classList.toggle("d-none");
                }
            } else {
                showSaveError();
                for (let i = 0; i < saveOrderButtons.length; i++) {
                    const saveOrderButton: HTMLButtonElement = saveOrderButtons[
                        i
                    ] as HTMLButtonElement;
                    saveOrderButton.disabled = false;
                    const saveIcon = saveOrderButton.querySelector(".save-icon");
                    const spinner = saveOrderButton.querySelector(".save-spinner");
                    saveIcon?.classList.toggle("d-none");
                    spinner?.classList.toggle("d-none");
                }
                throw Error(
                    `Failed to save attachments order. Response status: ${response.status}, statusText: ${response.statusText}`
                );
            }
        })
        .catch(error => {
            showSaveError();
            for (let i = 0; i < saveOrderButtons.length; i++) {
                const saveOrderButton: HTMLButtonElement = saveOrderButtons[
                    i
                ] as HTMLButtonElement;
                saveOrderButton.disabled = false;
                const saveIcon = saveOrderButton.querySelector(".save-icon");
                const spinner = saveOrderButton.querySelector(".save-spinner");
                saveIcon?.classList.toggle("d-none");
                spinner?.classList.toggle("d-none");
            }
            throw new Error(`Failed to save attachments order: ${error}`);
        });
}

function showGeneralError(): void {
    const alertTemplate = document.querySelector(
        "#cms-sort-attachments-error-general"
    ) as HTMLTemplateElement;
    const alert = alertTemplate.content.cloneNode(true) as Element;

    const container = document.querySelector("#messages");
    if (container) {
        container.appendChild(alert);
    }
}

function showSaveError(): void {
    const alertTemplate = document.querySelector(
        "#cms-sort-attachments-error-save"
    ) as HTMLTemplateElement;
    const alert = alertTemplate.content.cloneNode(true) as Element;

    const container = document.querySelector("#messages");
    if (container) {
        container.appendChild(alert);
    }
}
