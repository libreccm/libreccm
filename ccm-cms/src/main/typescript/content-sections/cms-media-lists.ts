import Sortable, { SortableEvent } from "sortablejs";

interface MediaStepMediaOrder {
    mediaListsOrder: string[];
    mediaOrder: {
        [key: string]: string[];
    };
    movedMedia: MovedMedia[];
}

interface MovedMedia {
    mediaUuid: string;
    fromListUuid: string;
    toListUuid: string;
}

const movedMedia: MovedMedia[] = [];

let mediaListSortable: Sortable;
let mediaSortables: {
    [key: string]: Sortable;
} = {};

document.addEventListener("DOMContentLoaded", function (event) {
    const mediaLists = document.querySelector(".cms.media-lists");

    if (mediaLists) {
        mediaListSortable = initMediaLists(mediaLists as HTMLElement);
    }

    const medias = document.querySelectorAll(".cms-medias");

    for (let i = 0; i < medias.length; i++) {
        initMedias(medias[i] as HTMLElement);
    }

    const saveOrderButtons = document.querySelectorAll(
        ".media-save-order-button"
    );
    for (let i = 0; i < saveOrderButtons.length; i++) {
        saveOrderButtons[i].addEventListener("click", saveOrder);
    }
});

function initMediaLists(mediaList: HTMLElement): Sortable {
    return new Sortable(mediaList, {
        animation: 150,
        group: "cms-media-lists",
        handle: ".cms-sort-handle",
        onEnd: enableSaveButton
    });
}

function initMedias(medias: HTMLElement): Sortable {
    const sortable = new Sortable(medias, {
        animation: 150,
        group: "cms-media",
        handle: ".cms-sort-handle",
        onEnd: moveMedia
    });

    const listUuid = medias.getAttribute("data-list-uuid");
    if (listUuid === null) {
        showGeneralError();
        throw Error("medias without data-list-uuid attribute found.");
    }

    mediaSortables[listUuid] = sortable;

    return sortable;
}

function enableSaveButton(event: SortableEvent) {
    const saveOrderButtons = document.querySelectorAll(
        ".media-save-order-button"
    );
    for (let i = 0; i < saveOrderButtons.length; i++) {
        const saveOrderButton: HTMLButtonElement = saveOrderButtons[
            i
        ] as HTMLButtonElement;
        saveOrderButton.disabled = false;
    }
}

function moveMedia(event: SortableEvent) {
    const fromListUuid = event.from.getAttribute("data-list-uuid");
    if (!fromListUuid) {
        showGeneralError();
        throw Error(
            "A media was moved, but the list from which the media was removed has no data-id attribute."
        );
    }

    const toListUuid = event.to.getAttribute("data-list-uuid");
    if (!toListUuid) {
        showGeneralError();
        throw Error(
            "An media was moved, but the list to which the media was removed has no data-id attribute."
        );
    }

    if (fromListUuid !== toListUuid) {
        const mediaUuid = event.item.getAttribute("data-id");
        if (!mediaUuid) {
            showGeneralError();
            throw Error(
                "An media was moved, but the media was removed has no dat-id attribute."
            );
        }

        const moved: MovedMedia = {
            fromListUuid,
            toListUuid,
            mediaUuid: mediaUuid
        };
        movedMedia.push(moved);
    }

    enableSaveButton(event);
}

function saveOrder() {
    const mediaOrder: MediaStepMediaOrder = {
        mediaListsOrder: mediaListSortable.toArray(),
        mediaOrder: {},
        movedMedia
    };

    for (let key in mediaSortables) {
        mediaOrder.mediaOrder[key] =
            mediaSortables[key].toArray();
    }

    // console.dir(mediaOrder);
    const cmsMedia = document.querySelector(".cms-media-lists");
    if (!cmsMedia) {
        showGeneralError();
        throw Error("cms-media-lists container not found.");
    }
    const baseUrl = cmsMedia.getAttribute("data-baseUrl");
    if (!baseUrl) {
        showGeneralError();
        throw Error(
            "data-baseUrl attribute on cms-media-lists container is missing or empty."
        );
    }

    const saveOrderButtons = document.querySelectorAll(".media-save-order-button");
    for (let i = 0; i < saveOrderButtons.length; i++) {
        const saveOrderButton: HTMLButtonElement = saveOrderButtons[
            i
        ] as HTMLButtonElement;
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
        body: JSON.stringify(mediaOrder),
        headers,
        method: "POST"
    })
    .then(response => {
        if (response.ok) {
            for(let i = 0; i < saveOrderButtons.length; i++) {
                const saveOrderButton: HTMLButtonElement = saveOrderButtons[
                    i
                ] as HTMLButtonElement;
                // saveOrderButton.disabled = true;
                const saveIcon =
                    saveOrderButton.querySelector(".save-icon");
                const spinner =
                    saveOrderButton.querySelector(".save-spinner");
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
                    const saveIcon =
                        saveOrderButton.querySelector(".save-icon");
                    const spinner =
                        saveOrderButton.querySelector(".save-spinner");
                    saveIcon?.classList.toggle("d-none");
                    spinner?.classList.toggle("d-none");
                }
                throw Error(
                    `Failed to save media order. Response status: ${response.status}, statusText: ${response.statusText}`
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
        throw new Error(`Failed to save media order: ${error}`);
    });
}

function showGeneralError(): void {
    const alertTemplate = document.querySelector(
        "#cms-sort-media-error-general"
    ) as HTMLTemplateElement;
    const alert = alertTemplate.content.cloneNode(true) as Element;

    const container = document.querySelector("#messages");
    if (container) {
        container.appendChild(alert);
    }
}

function showSaveError(): void {
    const alertTemplate = document.querySelector(
        "#cms-sort-media-error-save"
    ) as HTMLTemplateElement;
    const alert = alertTemplate.content.cloneNode(true) as Element;

    const container = document.querySelector("#messages");
    if (container) {
        container.appendChild(alert);
    }
}
