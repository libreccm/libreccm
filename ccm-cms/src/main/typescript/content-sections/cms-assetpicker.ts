import * as $ from "jquery";

document.addEventListener("DOMContentLoaded", function (event) {
    const assetPickers = document.querySelectorAll(".ccm-cms-asset-picker");

    for (let i = 0; i < assetPickers.length; i++) {
        initAssetPicker(assetPickers[i]);
    }
});

async function initAssetPicker(assetPickerElem: Element) {
    const assetPickerId = assetPickerElem.getAttribute("id");
    const assetType = getAssetType(assetPickerElem);
    const baseUrl = assetPickerElem.getAttribute("data-baseUrl");
    const contentSection = assetPickerElem.getAttribute("data-contentsection");

    console.log(`assetPickerId = ${assetPickerId}`);

    const fetchUrl = `${baseUrl}/content-sections/${contentSection}/assets?type=${assetType}`;

    try {
        const response = await fetch(fetchUrl);

        if (response.ok) {
            const assets = (await response.json()) as [];

            const rowTemplate = assetPickerElem.querySelector(
                `#${assetPickerId}-row`
            ) as HTMLTemplateElement;
            console.log(`rowTemplate = ${rowTemplate}`);

            const tbody = assetPickerElem.querySelector("tbody");
            console.log(`tbody = ${tbody}`);

            for (const asset of assets) {
                const row = rowTemplate?.content.cloneNode(true) as Element;
                const colName = row.querySelector(".col-name");
                const colType = row.querySelector(".col-type");
                const selectButton = row.querySelector(".col-action button");

                console.log(`row = ${row}`);
                console.log(`colName = ${colName}`);
                console.log(`colType = ${colType}`);
                console.log(`selectButton = ${selectButton}`);

                if (colName) {
                    colName.textContent = asset["name"];
                }
                if (colType) {
                    colType.textContent = asset["type"];
                }
                selectButton?.setAttribute("data-assetuuid", asset["uuid"]);

                selectButton?.addEventListener("click", event =>
                    selectAsset(event, assetPickerElem)
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

function getAssetType(assetPickerElem: Element) {
    if (assetPickerElem.hasAttribute("data-assettype")) {
        return assetPickerElem.getAttribute("data-assettype");
    } else {
        return "org.librecms.assets.Asset";
    }
}

async function selectAsset(event: Event, assetPickerElem: Element) {
    const selectButton = event.currentTarget as Element;
    const assetUuid = selectButton.getAttribute("data-assetuuid");
    if (!assetUuid) {
        console.error("assetUuid is null");
        return;
    }

    console.log(`selectButton = ${selectButton}`);
    console.log(`assetUuid = ${assetUuid}`);

    const assetPickerParam = assetPickerElem.querySelector(
        ".assetpicker-param"
    ) as HTMLInputElement;
    if (!assetPickerParam) {
        console.error("assetPickerParam is null");
        return;
    }
    assetPickerParam.value = assetUuid;

    const form = assetPickerElem.querySelector("form") as HTMLFormElement;
    form.submit();
}
