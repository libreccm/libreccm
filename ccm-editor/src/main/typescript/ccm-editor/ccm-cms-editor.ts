import { CCMEditor, CCMEditorCommand, CCMEditorCommandType } from "./ccm-editor";

export class InsertInternalLinkCommand extends CCMEditorCommand {

    private button: HTMLElement;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: HTMLElement = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-link";

        const text: HTMLElement = this.button
            .appendChild(document.createElement("span"));
        this.button
            .setAttribute("title", "Insert a link to a ContentItem");
        text.textContent = "Create internal link";
        text.className = "ccm-editor-accessibility";

        const command = this;

        this.button.addEventListener("click", function(event){

            event.preventDefault();

            const currentRange: Range = document.getSelection().getRangeAt(0);

            const dialogFragment: DocumentFragment = document
                .createDocumentFragment();
            const dialogElem: HTMLElement = dialogFragment
                .appendChild(document.createElement("div"));
            dialogElem.className = "ccm-editor-selectdialog";
            const dialogTitleElem: HTMLElement = dialogElem
                .appendChild(document.createElement("h1"));
            dialogTitleElem.textContent = "Insert link to a Content Item";

            const closeButton = dialogElem
                .appendChild(document.createElement("button"));
            closeButton.setAttribute("class", "ccm-editor-selectdialog-closebutton");
            closeButton.textContent = "\u2715";
            closeButton.addEventListener("click", function(event){
                event.preventDefault();
                const bodyElem = document.getElementsByTagName("body").item(0);
                bodyElem.removeChild(dialogElem);
                document.getSelection().removeAllRanges();
                document.getSelection().addRange(currentRange);
                return false;
            });

            const filterForm: HTMLElement = dialogElem
                .appendChild(document.createElement("div"));
            const contentSectionSelectLabel: HTMLElement = filterForm
                .appendChild(document.createElement("label"));
            contentSectionSelectLabel
                .setAttribute("for", "ccm-editor-contentsection-select");
            contentSectionSelectLabel.textContent = "Show items from Content Section";
            const contentSectionSelect: HTMLSelectElement = filterForm
                .appendChild(document.createElement("select"));
            contentSectionSelect
                .setAttribute("id", "ccm-editor-contentsection-select");
            const filterInputLabel: HTMLElement = filterForm
                .appendChild(document.createElement("label"));
            filterInputLabel.setAttribute("id", "ccm-editor-assetfilter");
            filterInputLabel.textContent = "Filter items";
            const filterInput: HTMLInputElement = filterForm
                .appendChild(document.createElement("input"));
            filterInput.setAttribute("id", "ccm-editor-assetfilter");
            filterInput.setAttribute("type", "text");
            const applyFiltersButton: HTMLElement = filterForm
                .appendChild(document.createElement("button"));
            applyFiltersButton.textContent = "Apply filters";
            const clearFiltersButton: HTMLElement = filterForm
                .appendChild(document.createElement("button"));
            clearFiltersButton.textContent = "Clear filters";

            const table: HTMLElement = dialogElem
                .appendChild(document.createElement("table"));
            const tableHead: HTMLElement = table
                .appendChild(document.createElement("thead"));
            const headerRow: HTMLElement = tableHead
                .appendChild(document.createElement("tr"));
            const titleColHeader: HTMLElement = headerRow
                .appendChild(document.createElement("th"));
            const typeColHeader: HTMLElement = headerRow
                .appendChild(document.createElement("th"));
            const placeColHeader: HTMLElement = headerRow
                .appendChild(document.createElement("th"));
            titleColHeader.textContent = "Title";
            typeColHeader.textContent = "Type";
            placeColHeader.textContent = "Place";
            const tableBody: HTMLElement = table
                .appendChild(document.createElement("tbody"));

            const contextPrefix = editor.getDataAttribute("context-prefix");
            // Get content sections
            const currentSection = editor
                .getDataAttribute("current-contentsection-primaryurl");
            const sectionsUrl = `${contextPrefix}/content-sections/`;
            fetch(sectionsUrl, { credentials: "include" }).then((response) => {
                if (response.ok) {
                    response.json().then((data) => {
                        for (const section of data) {
                            const option: HTMLOptionElement
                                = contentSectionSelect
                                    .appendChild(document
                                        .createElement("option"));
                            option.value = section.primaryUrl;
                            option.textContent = section.primaryUrl;
                            if (section.primaryUrl === currentSection) {
                                option.selected = true;
                            }
                        }
                    });
                } else {
                    const errorMsgElement: HTMLElement = dialogElem
                        .appendChild(document.createElement("div"));
                    const warningElem: HTMLElement = errorMsgElement
                        .appendChild(document.createElement("i"));
                    warningElem.classList.add("fa", "fa-warning");
                    errorMsgElement.classList.add("ccm-editor-error");
                    errorMsgElement.textContent = `Failed to fetch available
                    content sections from "${sectionsUrl}". Status: ${response.status}.
                    Status text: ${response.statusText}`;
                }
            },
            (failure) => {
                const errorMsgElement: HTMLElement = dialogElem
                    .appendChild(document.createElement("div"));
                const warningElem: HTMLElement = errorMsgElement
                    .appendChild(document.createElement("i"));
                warningElem.classList.add("fa", "fa-warning");
                errorMsgElement.classList.add("ccm-editor-error");
                errorMsgElement.textContent = `Failed to fetch available
                content sections from "${sectionsUrl}". Failure: ${failure}`;
            });

            contentSectionSelect.addEventListener("click", (event) => {
                command.fetchItems(dialogElem,
                                   contentSectionSelect.value,
                                   filterInput.value,
                                   tableBody,
                                   currentRange);
            });

            applyFiltersButton.addEventListener("click", (event) => {
                event.preventDefault();

                command.fetchItems(dialogElem,
                                   contentSectionSelect.value,
                                   filterInput.value,
                                   tableBody,
                                   currentRange);
            });
            clearFiltersButton.addEventListener("click", function(event){
                event.preventDefault();
                filterInput.value = "";
                command.fetchItems(dialogElem,
                                   contentSectionSelect.value,
                                   filterInput.value,
                                   tableBody,
                                   currentRange);
            });

            command.fetchItems(dialogElem,
                               currentSection,
                               filterInput.value,
                               tableBody,
                               currentRange);

            const bodyElem = document.getElementsByTagName("body").item(0);
            bodyElem.appendChild(dialogFragment);

            return false;
        });
    }

    private fetchItems(dialogElem: HTMLElement,
                       contentSection: string,
                       query: string,
                       itemsTableBodyElem: HTMLElement,
                       currentRange: Range) {

        const itemsUrl = this.generateItemsUrl(contentSection, query);

        fetch(itemsUrl, { credentials: "include" }).then((response) => {
            if (response.ok) {

                itemsTableBodyElem.innerHTML = "";

                response.json().then((data) => {
                    for (const item of data) {
                        const row: HTMLElement = itemsTableBodyElem
                            .appendChild(document.createElement("tr"));
                        const dataTitle = row
                            .appendChild(document.createElement("td"));
                        const dataType = row
                            .appendChild(document.createElement("td"));
                        const dataPlace = row
                            .appendChild(document.createElement("td"));

                        const selectItemButton: HTMLButtonElement = dataTitle
                            .appendChild(document.createElement("button"));
                        if (item.title === null || item.title.length <= 0) {
                            selectItemButton.textContent = item.name;
                        } else {
                            selectItemButton.textContent = item.title;
                        }
                        selectItemButton.addEventListener("click", (event) => {
                            event.preventDefault();

                            const bodyElem = document
                                .getElementsByTagName("body")
                                .item[0];
                            bodyElem.removeChild(dialogElem);
                            document.getSelection().removeAllRanges();
                            document.getSelection().addRange(currentRange);

                            const contextPrefix = this.editor
                                .getDataAttribute("context-prefix");
                            document.execCommand(
                                "createLink",
                                false,
                                `${contextPrefix}/redirect/?oid${item.itemId}`);
                        });

                        dataType.textContent = item.typeLabel;
                        dataPlace.textContent = item.place;
                    }
                });
            } else {
                const dialogElem: HTMLElement = itemsTableBodyElem
                    .parentNode.parentNode as HTMLElement;
                const errorMsgElement: HTMLElement = dialogElem
                    .insertBefore(document.createElement("div"),
                                  itemsTableBodyElem.parentNode);
                const warningElem: HTMLElement = errorMsgElement
                    .appendChild(document.createElement("i"));
                warningElem.classList.add("fa", "fa-warning");
                errorMsgElement.classList.add("ccm-editor-error");
                errorMsgElement.appendChild(document.createTextNode(
                    `Failed to fetch items for
                content section "${contentSection}" and query "${query}"
                from "${itemsUrl}".
                Status code: ${response.status}.
                Status text: ${response.statusText}`));
            }
        },
        (failure) => {
            const dialogElem: HTMLElement = itemsTableBodyElem
                .parentNode.parentNode as HTMLElement;
            const errorMsgElement: HTMLElement = dialogElem
                .insertBefore(document.createElement("div"),
                              itemsTableBodyElem.parentNode);
            const warningElem: HTMLElement = errorMsgElement
                .appendChild(document.createElement("i"));
            warningElem.classList.add("fa", "fa-warning");
            errorMsgElement.classList.add("ccm-editor-error");
            errorMsgElement.textContent = `Failed to fetch items for
            content section "${contentSection}" and query "${query}"
            from "${itemsUrl}".
            Failure message: ${failure}.`;
        });
    }

    private generateItemsUrl(contentSection: string, query?: string) {
        const contextPrefix = this.editor.getDataAttribute("context-prefix");

        let itemsUrl = `${contextPrefix}/content-sections`;
        if (!(new RegExp("^/.*").test(contentSection))) {
            itemsUrl = `${itemsUrl}/`;
        }

        itemsUrl = `${itemsUrl}${contentSection}`;
        if (!(new RegExp(".*/$").test(contentSection))) {
            itemsUrl = `${itemsUrl}/`;
        }

        itemsUrl = `${itemsUrl}items`;

        if (query) {
            itemsUrl = `${itemsUrl}?query=${query}`;
        }

        return itemsUrl;
    }

    getCommandType(): CCMEditorCommandType {
        return CCMEditorCommandType.INSERT_INLINE;
    }

    selectionChanged(selection: Selection) {

    }

    enableCommand(): void {
        this.button.removeAttribute("disabled");
    }

    disableCommand(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class InsertMediaAssetCommand extends CCMEditorCommand {

    private button: HTMLElement;

    private IMAGE_TYPE: string = "org.librecms.assets.Image";
    private VIDEO_TYPE: string = "org.librecms.assets.VideoAsset";
    private EXTERNAL_VIDEO_TYPE: string
        = "org.librecms.assets.ExternalVideoAsset";
    private AUDIO_TYPE: string = "org.librecms.assets.Audio";
    private EXTERNAL_AUDIO_TYPE: string
        = "org.librecms.assets.ExternalAudioAsset";

    constructor(editor: CCMEditor, settings: any) {

        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: HTMLElement = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-file-image-o";

        const text: HTMLElement = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title",
                                 "Insert media content (image, audio, video)");
        text.textContent = `Insert media content like an image, a audio file or
            a video`;
        text.className = "ccm-editor-accessibility";

        const command: InsertMediaAssetCommand = this;

        this.button.addEventListener("click", (event) => {

            event.preventDefault();

            const currentRange: Range = document.getSelection().getRangeAt(0);

            const dialogFragment: DocumentFragment = document
                .createDocumentFragment();
            const dialogElem: HTMLElement = dialogFragment
                .appendChild(document.createElement("div"));
            dialogElem.className = "ccm-editor-selectdialog";
            const dialogTitleElem: HTMLElement = dialogElem
                .appendChild(document.createElement("h1"));
            dialogTitleElem.textContent = "Insert media";

            const closeButton: HTMLButtonElement = dialogElem
                .appendChild(document.createElement("button"));
            closeButton.className = "ccm-editor-selectdialog-closebutton";
            closeButton.textContent = "\u2715";
            closeButton.addEventListener("click", function() {
                event.preventDefault();
                const bodyElem = document.getElementsByTagName("body").item(0);
                bodyElem.removeChild(dialogElem);
                document.getSelection().removeAllRanges();
                document.getSelection().addRange(currentRange);
            });

            const filterForm: HTMLElement = dialogElem
                .appendChild(document.createElement("div"));
            const contentSectionSelectLabel: HTMLElement = filterForm
                .appendChild(document.createElement("label"));
            contentSectionSelectLabel
                    .setAttribute("for", "ccm-editor-contentsection-select");
            contentSectionSelectLabel.textContent
                = "Show media assets from Content Section";
            const contentSectionSelect: HTMLSelectElement = filterForm
                    .appendChild(document.createElement("select"));
            contentSectionSelect
                    .setAttribute("id", "ccm-editor-contentsection-select");
            const typeSelectLabel: HTMLElement = filterForm
                .appendChild(document.createElement("label"));
            typeSelectLabel.setAttribute("for", "ccm-editor-assettype-select");
            typeSelectLabel.textContent = "Type";
            const typeSelect: HTMLSelectElement = filterForm
                .appendChild(document.createElement("select"));
            typeSelect.setAttribute("id", "ccm-editor-assettype-select");
            const typeSelectOptions = [
                {
                    type: command.IMAGE_TYPE,
                    label: "Image",
                },
                {
                    type: command.VIDEO_TYPE,
                    label: "Video",
                },
                {
                    type: command.EXTERNAL_VIDEO_TYPE,
                    label: "External Video",
                },
                {
                    type: command.AUDIO_TYPE,
                    label: "Audio file",
                },
                {
                    type: command.EXTERNAL_AUDIO_TYPE,
                    label: "External Audio file",
                },
            ];
            for (const typeOption of typeSelectOptions) {
                const option: HTMLElement = document.createElement("option");
                option.setAttribute("value", typeOption.type);
                option.textContent = typeOption.label
                typeSelect.appendChild(option);
            }
            const filterInputLabel: HTMLElement = filterForm
                .appendChild(document.createElement("label"));
            filterInputLabel.setAttribute("id", "ccm-editor-assetfilter");
            filterInputLabel.textContent = "Filter items";
            const filterInput: HTMLInputElement = filterForm
                .appendChild(document.createElement("input"));
            filterInput.setAttribute("id", "ccm-editor-assetfilter");
            filterInput.setAttribute("type", "text");
            const applyFiltersButton: HTMLElement = filterForm
                .appendChild(document.createElement("button"));
            applyFiltersButton.textContent = "Apply filters";

            const clearFiltersButton: HTMLElement = filterForm
                .appendChild(document.createElement("button"));
            clearFiltersButton.textContent = "Clear filters";

            const table: HTMLElement = dialogElem
                .appendChild(document.createElement("table"));
            const tableHead: HTMLElement = table
                .appendChild(document.createElement("thead"));
            const headerRow: HTMLElement = tableHead
                .appendChild(document.createElement("tr"));
            const titleColHeader: HTMLElement = headerRow
                .appendChild(document.createElement("th"));
            const typeColHeader: HTMLElement = headerRow
                    .appendChild(document.createElement("th"));
            const placeColHeader: HTMLElement = headerRow
                    .appendChild(document.createElement("th"));
            titleColHeader.textContent = "Title";
            typeColHeader.textContent = "Type";
            placeColHeader.textContent = "Place";
            const tableBody: HTMLElement = table
                .appendChild(document.createElement("tbody"));

            const contextPrefix = editor.getDataAttribute("context-prefix");
            // Get content sections
            const currentSection = editor
                .getDataAttribute("current-contentsection-primaryurl");
            const sectionsUrl = `${contextPrefix}/content-sections/`;
            fetch(sectionsUrl, { credentials: "include" }).then((response) => {

                if (response.ok) {
                    response.json().then((data) => {
                        for(const section of data) {
                            const option: HTMLOptionElement
                                = contentSectionSelect
                                    .appendChild(document
                                            .createElement("option"));
                            option.value = section.primaryUrl;
                            option.textContent = section.primaryUrl;
                            if (section.primaryUrl === currentSection) {
                                option.selected = true;
                            }
                        }
                    });
                } else {
                    const errorMsgElement: HTMLElement = dialogElem
                        .appendChild(document.createElement("div"));
                    errorMsgElement.classList.add("ccm-editor-error");
                    errorMsgElement.textContent = `Failed to fetch available
                    content sections from "${sectionsUrl}".
                    Status: ${response.status}.
                    Status text: ${response.statusText}`;
                }
            },
            (failure) => {
                const errorMsgElement: HTMLElement = dialogElem
                    .appendChild(document.createElement("div"));
                errorMsgElement.classList.add("ccm-editor-error");
                errorMsgElement.textContent = `Failed to fetch available
                content sections from "${sectionsUrl}". Failure: ${failure}`;
            });

            contentSectionSelect.addEventListener("change", (event) => {
                event.preventDefault();
                command.fetchAssets(dialogElem,
                                    contentSectionSelect.value,
                                    typeSelect.value,
                                    filterInput.value,
                                    tableBody,
                                    currentRange);
            });
            typeSelect.addEventListener("change", (event) => {
                event.preventDefault();
                command.fetchAssets(dialogElem,
                                    contentSectionSelect.value,
                                    typeSelect.value,
                                    filterInput.value,
                                    tableBody,
                                    currentRange);
            });
            applyFiltersButton.addEventListener("click", (event) => {
                event.preventDefault();
                command.fetchAssets(dialogElem,
                                    contentSectionSelect.value,
                                    typeSelect.value,
                                    filterInput.value,
                                    tableBody,
                                    currentRange);
            });

            clearFiltersButton.addEventListener("click", function(event){
                event.preventDefault();
                filterInput.value = "";
                command.fetchAssets(dialogElem,
                                    contentSectionSelect.value,
                                    typeSelect.value,
                                    filterInput.value,
                                    tableBody,
                                    currentRange);
            });

            command.fetchAssets(dialogElem,
                                currentSection,
                                command.IMAGE_TYPE,
                                null,
                                tableBody,
                                currentRange);

            const bodyElem = document.getElementsByTagName("body").item(0);
            bodyElem.appendChild(dialogFragment);
        });
    }

    private fetchAssets(dialogElem: HTMLElement,
                        contentSection: string,
                        type: string,
                        query: string,
                        resultsTableBody: HTMLElement,
                        currentRange: Range): void {

        console.log(`selected contentsection: ${contentSection}`);

        const assetsUrl = this.buildAssetsUrl(contentSection,
                                              type,
                                              query);

        fetch(assetsUrl, { credentials: "include" }).then((response) => {

            if (response.ok) {
                response.json().then((data) => {

                    resultsTableBody.innerHTML = "";

                    for(const asset of data) {
                        const row: HTMLElement = resultsTableBody
                            .appendChild(document.createElement("tr"));
                        const dataTitle: HTMLElement = row
                            .appendChild(document.createElement("td"));
                        const dataType: HTMLElement = row
                            .appendChild(document.createElement("td"));
                        const dataPlace = row
                            .appendChild(document.createElement("td"));

                        const selectAssetButton = dataTitle
                            .appendChild(document.createElement("button"));
                        selectAssetButton.textContent = asset.title;

                        selectAssetButton
                            .addEventListener("click", event => {
                                event.preventDefault();

                                this.insertMedia(
                                    dialogElem,
                                    this.editor
                                        .getDataAttribute("context-prefix"),
                                    contentSection,
                                    asset.uuid,
                                    asset.title,
                                    asset.type,
                                    asset.typeLabel,
                                    currentRange);
                            });

                        dataType.textContent = asset.typeLabel;
                        dataPlace.textContent = asset.place;
                    }
                },
                (failure) => {
                    const errorMsgElement = document.createElement("div");
                    errorMsgElement.classList.add("ccm-editor-error");
                    errorMsgElement.textContent = `Failed to fetch assets from
                    ${assetsUrl}: ${failure}`;
                    resultsTableBody.parentNode.parentNode
                        .insertBefore(errorMsgElement,
                                      resultsTableBody.parentNode);
                });
            } else {
                const errorMsgElement = document.createElement("div");
                errorMsgElement.classList.add("ccm-editor-error");
                errorMsgElement.textContent = `Failed to fetch assets from
                ${assetsUrl}. Status code: ${response.status}.
                Status text: ${response.statusText}`;
                resultsTableBody.parentNode.parentNode
                    .insertBefore(errorMsgElement, resultsTableBody.parentNode);
            }
        },
        (failure) => {
            const errorMsgElement = document.createElement("div");
            errorMsgElement.classList.add("ccm-editor-error");
            errorMsgElement.textContent = `Failed to fetch assets from
            ${assetsUrl}: ${failure}`;
            resultsTableBody.parentNode.parentNode
                .insertBefore(errorMsgElement, resultsTableBody.parentNode);
        });
    }

    private buildAssetsUrl(contentSection: string,
                           type: string,
                           query?: string): string {

        const contextPrefix = this.editor.getDataAttribute("context-prefix");

        console.log(`building assetsUrl for contentSection = ${contentSection}`);

        let url: string = `${contextPrefix}/content-sections`;
        if (!(new RegExp("^/.*").test(contentSection))) {
            url = `${url}/`;
        }
        url = `${url}${contentSection}`;
        if (!(new RegExp(".*/$").test(url))) {
            url = `${url}/`
        }

        url = `${url}assets?type=${type}`;

        if (query) {
            url = `${url}&query=${query}`;
        }

        return url;
    }

    private insertMedia(selectDialogElem: HTMLElement,
                        contextPrefix: string,
                        contentSection: string,
                        assetUuid: string,
                        assetTitle: string,
                        assetType: string,
                        assetTypeLabel: string,
                        currentRange: Range): void {

        selectDialogElem.setAttribute("style", "display: none");

        const dialogId: string = "ccm-editor-insertmedia";
        const captionFieldId: string = `${dialogId}-caption`;
        const altFieldId: string = `${dialogId}-alt`;
        const decorativeCheckboxId: string
            = `${dialogId}-isdecorative`;
        const widthFieldId: string = `${dialogId}-width`;
        const heightFieldId: string = `${dialogId}-height`;

        const fragment: DocumentFragment = document.createDocumentFragment();
        const insertDialogElem: HTMLElement = fragment
            .appendChild(document.createElement("div"));
        insertDialogElem.classList.add("ccm-editor-dialog");
        insertDialogElem.id = dialogId;

        const headingElem: HTMLElement = insertDialogElem
            .appendChild(document.createElement("h1"));
        headingElem.textContent = `Insert media asset "${assetTitle}" of
            type ${assetTypeLabel}`;

        const formElem: HTMLElement = insertDialogElem
            .appendChild(document.createElement("form"));

        const captionLabel: HTMLLabelElement = formElem
            .appendChild(document.createElement("label"));
        captionLabel.htmlFor = captionFieldId;
        captionLabel.textContent = "Caption";
        const captionField: HTMLInputElement = formElem
            .appendChild(document.createElement("input"));
        captionField.id = captionFieldId;
        captionField.type = "text";

        if (this.IMAGE_TYPE === assetType) {

            const altLabel: HTMLLabelElement = formElem
                .appendChild(document.createElement("label"));
            altLabel.htmlFor = altFieldId;
            altLabel.textContent = "Alternativ text for image";
            const altField: HTMLInputElement = formElem
                .appendChild(document.createElement("input"));
            altField.type = "text";
            altField.id = altFieldId;

            const decorativeLabel: HTMLLabelElement = formElem
                .insertBefore(document.createElement("label"), captionLabel);
            decorativeLabel.htmlFor = decorativeCheckboxId;
            decorativeLabel.textContent = "Is decorative image?";
            const decorativeCheckbox: HTMLInputElement = formElem
                .insertBefore(document.createElement("input"), captionLabel);
            decorativeCheckbox.id = decorativeCheckboxId;
            decorativeCheckbox.type = "checkbox";

            decorativeCheckbox.addEventListener("change", (event) => {
                if (decorativeCheckbox.checked) {
                    captionLabel.setAttribute("style", "display: none");
                    captionField.setAttribute("style", "display: none");
                    captionField.value = "";

                    altLabel.setAttribute("style", "display: none");
                    altField.setAttribute("style", "display: none");
                    altField.value = "";
                } else {
                    captionLabel.removeAttribute("style");
                    captionField.removeAttribute("style");
                    altLabel.removeAttribute("style");
                    altField.removeAttribute("style");
                }
            });

            if (this.IMAGE_TYPE === assetType
                || this.VIDEO_TYPE === assetType
                || this.EXTERNAL_VIDEO_TYPE === assetType) {

                const widthLabel: HTMLLabelElement = formElem
                    .appendChild(document.createElement("label"));
                widthLabel.htmlFor = widthFieldId;
                widthLabel.textContent = "Width";
                const widthField: HTMLInputElement = formElem
                    .appendChild(document.createElement("input"));
                widthField.id = widthFieldId;
                widthField.type = "number";

                const heightLabel: HTMLLabelElement = formElem
                    .appendChild(document.createElement("label"));
                heightLabel.htmlFor = heightFieldId;
                heightLabel.textContent = "height";
                const heightField: HTMLInputElement = formElem
                    .appendChild(document.createElement("input"));
                heightField.id = heightFieldId;
                heightField.type = "number";
            }

            const insertButton: HTMLButtonElement = formElem
                .appendChild(document.createElement("button"));
            insertButton.textContent = "insert";
            insertButton.addEventListener("click", (event) => {
                event.preventDefault();

                const captionField: HTMLInputElement = document
                    .getElementById(captionFieldId) as HTMLInputElement;
                let mediaHtml: string;
                switch(assetType) {
                    case this.AUDIO_TYPE: {
                        mediaHtml = this
                            .generateAudioHtml("0", captionField.value);
                    }
                    case this.EXTERNAL_AUDIO_TYPE: {
                        mediaHtml = this
                            .generateAudioHtml("0", captionField.value);
                    }
                    case this.EXTERNAL_VIDEO_TYPE: {
                        const widthField: HTMLInputElement = document
                            .getElementById(widthFieldId) as HTMLInputElement;
                        const heightField: HTMLInputElement = document
                                .getElementById(widthFieldId) as
                            HTMLInputElement;
                        mediaHtml = this
                            .generateVideoHtml("0",
                                               captionField.value,
                                               Number(widthField.value),
                                               Number(heightField.value));
                    }
                    case this.IMAGE_TYPE: {
                        const altField: HTMLInputElement = document
                            .getElementById(altFieldId) as HTMLInputElement;
                        const widthField: HTMLInputElement = document
                            .getElementById(widthFieldId) as HTMLInputElement;
                        const heightField: HTMLInputElement = document
                                .getElementById(widthFieldId) as
                                HTMLInputElement;
                        const imageUrl: string = this
                            .generateImageUrl(contextPrefix,
                                              contentSection,
                                              assetUuid);

                        if (decorativeCheckbox.checked) {
                            mediaHtml = this
                                .generateDecorativeImageHtml(
                                    imageUrl,
                                    Number(widthField.value),
                                    Number(heightField.value));
                        } else {
                            mediaHtml = this
                                .generateImageHtml(imageUrl,
                                                  captionField.value,
                                                  altField.value,
                                                  Number(widthField.value),
                                                  Number(heightField.value));
                        }
                    }
                    case this.VIDEO_TYPE: {
                        const widthField: HTMLInputElement = document
                            .getElementById(widthFieldId) as HTMLInputElement;
                        const heightField: HTMLInputElement = document
                                .getElementById(widthFieldId) as
                            HTMLInputElement;
                        mediaHtml = this
                            .generateVideoHtml("0",
                                               captionField.value,
                                               Number(widthField.value),
                                               Number(heightField.value));
                    }
                    default:
                        mediaHtml = "";
                }

                document.getSelection().removeAllRanges();
                document.getSelection().addRange(currentRange);
                document.execCommand("insertHTML", false, mediaHtml);

                const bodyElem = document.getElementsByTagName("body").item(0);
                bodyElem.removeChild(insertDialogElem);
                bodyElem.removeChild(selectDialogElem);
            });

            const cancelButton: HTMLButtonElement = formElem
                .appendChild(document.createElement("button"));
            cancelButton.textContent = "Cancel";
            cancelButton.addEventListener("click", (event) => {
                event.preventDefault();

                const bodyElem = document.getElementsByTagName("body").item(0);
                bodyElem.removeChild(insertDialogElem);
                selectDialogElem.removeAttribute("style");
            });
        }
    }

    private generateDecorativeImageHtml(imageUrl: string,
                            width: number = -1,
                            height: number = -1): string {

        let dimensions: string;
        if (width > 0) {
            dimensions = `${dimensions} width="${width}"`;
        }
        if (height > 0) {
            dimensions = `${dimensions} height="${height}"`;
        }

        return `<img src="${imageUrl}" ${dimensions} alt="" />`;
    }

    private generateImageHtml(imageUrl: string,
                              caption: string,
                              alt: string,
                              width: number = -1,
                              height: number = -1): string {

        let dimensions: string;
        if (width > 0) {
            dimensions = `${dimensions} width="${width}"`;
        }
        if (height > 0) {
            dimensions = `${dimensions} height="${height}"`;
        }

        return `<figure role="group">
            <img src="${imageUrl}" ${dimensions} alt="${alt}" />
            <figcaption>${caption}</figcaption>
        </figure>`
    }

    private generateImageUrl(contextPrefix: string,
                             contentSection: string,
                             assetUuid: string,
                             width: number = -1,
                             height: number = -1): string {

        let imageUrl: string = `${contextPrefix}/content-sections`;
        if (!(new RegExp("^/.*").test(contentSection))) {
            imageUrl = `${imageUrl}/`;
        }
        imageUrl = `${imageUrl}{$contentSection}`;
        if (!(new RegExp(".*/$").test(contentSection))) {
            imageUrl = `${imageUrl}/`;
        }
        return `${imageUrl}/images/uuid-${assetUuid}
        ?width=${width}&height=${height}`;
    }

    private generateVideoHtml(videoUrl: string,
                              caption: string,
                              width: number = -1,
                              height: number = -1): string {

        return `<figure role="group">
            <span>Not implemented yet</span>
            <figcaption>${caption}</figcaption>
        </figure>`;
    }

    private generateAudioHtml(audioUrl: string, caption: string): string {

        return `<figure role="group">
            <span>Not implemented yet</span>
            <figcaption>${caption}</figcaption>
        </figure>`;
    }

    getCommandType(): CCMEditorCommandType {
        return CCMEditorCommandType.INSERT_INLINE;
    }

    selectionChanged(selection: Selection) {

    }

    enableCommand(): void {
        this.button.removeAttribute("disabled");
    }

    disableCommand(): void {
        this.button.setAttribute("disabled", "disabled");
    }

}
