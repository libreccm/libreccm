import { CCMEditor, CCMEditorCommand, CCMEditorCommandType } from "./ccm-editor";

export class InsertInternalLinkCommand extends CCMEditorCommand {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-link";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button
            .setAttribute("title", "Insert a link to a ContentItem");
        text.textContent = "Create internal link";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event){

            event.preventDefault();

            const currentRange: Range = document.getSelection().getRangeAt(0);

            const dialogFragment: DocumentFragment = document
                .createDocumentFragment();
            const dialogElem: Element = dialogFragment
                .appendChild(document.createElement("div"));
            dialogElem.className = "ccm-editor-selectdialog";
            const dialogTitleElem: Element = dialogElem
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

            const filterForm: Element = dialogElem
                .appendChild(document.createElement("div"));
            const contentSectionSelectLabel: Element = filterForm
                .appendChild(document.createElement("label"));
            contentSectionSelectLabel
                .setAttribute("for", "ccm-editor-contentsection-select");
            contentSectionSelectLabel.textContent = "Show items from Content Section";
            const contentSectionSelect: HTMLSelectElement = filterForm
                .appendChild(document.createElement("select"));
            contentSectionSelect
                .setAttribute("id", "ccm-editor-contentsection-select");
            const filterInputLabel: Element = filterForm
                .appendChild(document.createElement("label"));
            filterInputLabel.setAttribute("id", "ccm-editor-itemfilter");
            filterInputLabel.textContent = "Filter items";
            const filterInput: HTMLInputElement = filterForm
                .appendChild(document.createElement("input"));
            filterInput.setAttribute("id", "ccm-editor-itemfilter");
            filterInput.setAttribute("type", "text");
            const applyFiltersButton: Element = filterForm
                .appendChild(document.createElement("button"));
            applyFiltersButton.textContent = "Clear filters";
            applyFiltersButton.addEventListener("click", function(event){
                event.preventDefault();
                return false;
            });
            const clearFiltersButton: Element = filterForm
                .appendChild(document.createElement("button"));
            clearFiltersButton.textContent = "Clear filters";
            clearFiltersButton.addEventListener("click", function(event){
                event.preventDefault();
                filterInput.value = "";
                return false;
            });

            const table: Element = dialogElem
                .appendChild(document.createElement("table"));
            const tableHead: Element = table
                .appendChild(document.createElement("thead"));
            const headerRow: Element = tableHead
                .appendChild(document.createElement("tr"));
            const titleColHeader: Element = headerRow
                .appendChild(document.createElement("th"));
            const typeColHeader: Element = headerRow
                .appendChild(document.createElement("th"));
            const placeColHeader: Element = headerRow
                .appendChild(document.createElement("th"));
            titleColHeader.textContent = "Title";
            typeColHeader.textContent = "Type";
            placeColHeader.textContent = "Place";
            const tableBody: Element = table
                .appendChild(document.createElement("tbody"));

            const contextPrefix = editor.getDataAttribute("context-prefix");
            // Get content sections
            const currentSection = editor
                .getDataAttribute("current-contentsection-primaryurl");
            const sectionsUrl = contextPrefix + "/content-sections/";
            const sectionsRequest = new XMLHttpRequest();
            sectionsRequest.open("GET", sectionsUrl);
            sectionsRequest.withCredentials = true;
            sectionsRequest.addEventListener("load", function(event){
                if (sectionsRequest.status >= 200
                    && sectionsRequest.status <= 300) {

                    const sections = JSON.parse(sectionsRequest.responseText);
                    for(let i = 0; i < sections.length; ++i) {
                        const section = sections[i];
                        const option: Element = contentSectionSelect
                            .appendChild(document.createElement("option"));
                        option.setAttribute("value", section["primaryUrl"]);
                        option.textContent = section["primaryUrl"];
                        if (section["primaryUrl"] === currentSection) {
                            option.setAttribute("selected", "selected");
                        }
                    }
                }
            });
            sectionsRequest.send();

            console.log("Current sections is \"" + currentSection + "\"");
            // Get items
            let itemsUrl = contextPrefix
                + "/content-sections";
            if (!(new RegExp("^/.*").test(currentSection))) {
                console.log("Current sections does not start with an \"/\", adding one...");
                itemsUrl += "/";
            }
            itemsUrl += currentSection;
            if (!(new RegExp(".*/$").test(currentSection))) {
                console.log("Current sections does not end with an \"/\", adding one...");
                itemsUrl += "/";
            }
            itemsUrl += "items";
            if (filterInput.value !== null && filterInput.value.length > 0) {
                itemsUrl + "?query=" + filterInput.value;
            }
            const itemsRequest = new XMLHttpRequest();
            itemsRequest.open("GET", itemsUrl);
            itemsRequest.withCredentials = true;
            itemsRequest.addEventListener("load", function(event){
                if (itemsRequest.status >= 200 && itemsRequest.status <= 300) {

                    const items = JSON.parse(itemsRequest.responseText);
                    for(let i = 0; i < items.length; ++i) {
                        const item = items[i];
                        const row: Element = tableBody
                            .appendChild(document.createElement("tr"));
                        const dataTitle = row
                            .appendChild(document.createElement("td"));
                        const dataType = row
                            .appendChild(document.createElement("td"));
                        const dataPlace =
                            row.appendChild(document.createElement("td"));

                        const selectItemButton = dataTitle
                            .appendChild(document.createElement("button"));
                        if (item["title"] === null
                            || item["title"].length <= 0) {
                            selectItemButton.textContent = item["name"];
                        } else {
                            selectItemButton.textContent = item["title"];
                        }
                        selectItemButton
                            .addEventListener("click", function(event) {

                                event.preventDefault();

                                const bodyElem = document
                                    .getElementsByTagName("body")
                                    .item(0);
                                bodyElem.removeChild(dialogElem);
                                document.getSelection().removeAllRanges();
                                document.getSelection().addRange(currentRange);

                                document.execCommand("createLink",
                                                     false,
                                                     contextPrefix
                                                     + "/redirect/?oid="
                                                     + item["itemId"]);

                                return false;
                        });

                        dataType.textContent = item["typeLabel"];
                        dataType.textContent = item["place"];
                    }
                }
            });
            itemsRequest.send();

            const bodyElem = document.getElementsByTagName("body").item(0);
            bodyElem.appendChild(dialogFragment);

            return false;
        });
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
