import * as $ from "jquery";

document.addEventListener("DOMContentLoaded", function (event) {
    const itemPickers = document.querySelectorAll(
        ".ccm-cms-contentitem-picker"
    );

    for (let i = 0; i < itemPickers.length; i++) {
        initContentItemPicker(itemPickers[i]);
    }
});

async function initContentItemPicker(itemPickerElem: Element) {
    const itemPickerId = itemPickerElem.getAttribute("id");
    const itemType = getContentType(itemPickerElem);
    const baseUrl = itemPickerElem.getAttribute("data-baseUrl");
    const contentSection = itemPickerElem.getAttribute("data-contentsection");

    console.log(`itemPickerId = ${itemPickerId}`);

    if (!baseUrl) {
        console.error("No baseUrl provided.");
        return;
    }
    if (!contentSection) {
        console.error("No content section provided");
        return;
    }
    
    const fetchUrl = buildFetchUrl(baseUrl, contentSection, itemType);

    try {
        const response = await fetch(fetchUrl);

        if (response.ok) {
            const items = (await response.json()) as [];

            const rowTemplate = itemPickerElem.querySelector(
                `#${itemPickerId}-row`
            ) as HTMLTemplateElement;

            const tbody = itemPickerElem.querySelector("tbody");

            for (const item of items) {
                const row = rowTemplate?.content.cloneNode(true) as Element;
                const colName = row.querySelector(".col-name");
                const colType = row.querySelector(".col-type");
                const selectButton = row.querySelector(".col-action button");

                if (colName) {
                    colName.textContent = item["name"];
                }
                if (colType) {
                    colType.textContent = item["type"];
                }
                selectButton?.setAttribute("data-itemuuid", item["uuid"]);

                selectButton?.addEventListener("click", event =>
                    selectItem(event, itemPickerElem)
                );

                tbody?.appendChild(row);
            }
        } else {
            console.error(
                `Error. Status: ${response.status}. Status Text: ${response.statusText}`
            );
        }
    } catch (error) {
        console.error(error);
    }
}

function buildFetchUrl(
    baseUrl: string, 
    contentSection: string, 
    itemType: string
    ) {
    if (itemType && itemType !== "org.librecms.contentsection.ContentItem") {
        return `${baseUrl}/content-sections/${contentSection}/items?type=${itemType}&version=draft`;
    } else {
        return `${baseUrl}/content-sections/${contentSection}/items?version=draft`;
    }
}

function getContentType(itemPickerElem: Element):string  {
    if (itemPickerElem.hasAttribute("data-contentitem-type")) {
        const result = itemPickerElem.getAttribute("data-contentitem-type");
        if (result) {
            return result;
        } else {
            return "org.librecms.contentsection.ContentItem";
        }
    } else {
        return "org.librecms.contentsection.ContentItem";
    }
}

async function selectItem(event: Event, itemPickerElem: Element) {
    const selectButton = event.currentTarget as Element;
    const itemUuid = selectButton.getAttribute("data-itemuuid");
    if (!itemUuid) {
        console.error("itemUuid is null");
        return;
    }

    const itemPickerParam = itemPickerElem.querySelector(
        ".contentitempicker-param"
    ) as HTMLInputElement;
    if (!itemPickerParam) {
        console.error("contentItemPickerParam is null");
        return;
    }
    itemPickerParam.value = `UUID-${itemUuid}`;

    const form = itemPickerElem.querySelector("form") as HTMLFormElement;
    form.submit();
}
