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
    const contextPath = assetPickerElem.getAttribute("data-contextpath");
    const contentSection = assetPickerElem.getAttribute("data-contentsection");

    const fetchUrl = contextPath
        ? `./${contextPath}/content-sections/${contentSection}/assets/?type=${assetType}`
        : `./content-sections/${contentSection}/assets/?type=${assetType}`;

    try {
        const response = await fetch(fetchUrl);

        if (response.ok) {
            const assets = (await response.json()) as [];

            const rowTemplate = assetPickerElem.querySelector(
                `#${assetPickerId}-row`
            );

            const tbody = assetPickerElem.querySelector("tbody");

            for (const asset of assets) {
                const row = rowTemplate.cloneNode(true) as Element;
                const colName = row.querySelector(".col-name");
                const colType = row.querySelector(".col-type");
                const selectButton = row.querySelector(".col-action button");

                colName.textContent = asset["name"];
                colType.textContent = asset["type"];
                selectButton.setAttribute("data-assetuuid", asset["uuid"]);

                selectButton.addEventListener("click", event =>
                    selectAsset(event, assetPickerElem)
                );

                tbody.appendChild(row);
            }
        } else {
        }
    } catch (error) {}
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

    const assetPickerParam = assetPickerElem.querySelector(
        ".assetpicker-param"
    ) as HTMLInputElement;
    assetPickerParam.value = assetUuid;

    const form = assetPickerElem.querySelector("form") as HTMLFormElement;
    form.submit();
}
