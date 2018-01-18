/**
 * Them main class for the editor.
 */
export class CCMEditor {

    private configuration: CCMEditorConfiguration;
    private textarea: HTMLTextAreaElement;
    private editorDiv: Element;
    private editorToolbar: Element;
    private htmlSourceToolbar: Element;
    private actions: CCMEditorAction[] = [];

    constructor(textarea: HTMLTextAreaElement,
                configuration?: CCMEditorConfiguration) {

        if (textarea == undefined || textarea == null) {
            throw new Error("No TextArea provided.");
        }

        if (configuration == undefined) {

            console.log("No configuration provided, using default configuration.");

            this.configuration = {
                "actionGroups": [
                    {
                        "name": "mark-text",
                        "title": "Mark text",
                        "actions": [MakeBoldAction, MakeItalicAction]
                    }
                ],
                "settings":
                    {
                        "formatBlock.blocks": [
                            {
                                "element": "h3",
                                "title": "Heading 3"
                            },
                            {
                                "element": "h4",
                                "title": "Heading 4"
                            },
                            {
                                "element": "h5",
                                "title": "Heading 5"
                            },
                            {
                                "element": "h6",
                                "title": "Heading 6"
                            },
                            {
                                "element": "p",
                                "title": "Paragraph"
                            },
                            {
                                "element": "blockquote",
                                "title": "Blockquote"
                            }
                        ]
                    }
            };
        } else {
            this.configuration = configuration;
        }

        if (textarea.tagName.toLowerCase() != "textarea") {
            throw new Error("Provided element is not a textarea");
        }

        this.textarea = textarea;

        console.log("Adding actions...")
        for(const actionGroupKey in this.configuration.actionGroups) {

            const actionGroup: CCMEditorActionGroup = this
                .configuration
                .actionGroups[actionGroupKey];
            for(const actionKey in actionGroup.actions) {

                console.log("Adding action " + actionKey);
                const actionClass = actionGroup.actions[actionKey];
                const actionObj: CCMEditorAction
                    = new actionClass(this, configuration.settings);
                this.actions[actionGroup.actions[actionKey]] = actionObj;
            }
        }

        const editor: Element = document.createElement("div");
        editor.classList.add("ccm-editor");

        this.editorToolbar = editor.appendChild(document.createElement("div"));
        this.editorToolbar.classList.add("ccm-editor-toolbar");

        for(const actionGroupKey in this.configuration.actionGroups) {

            const actionGroup: CCMEditorActionGroup = this
                .configuration
                .actionGroups[actionGroupKey];
            const actionGroupElem: Element = this.editorToolbar
                .appendChild(document.createElement("div"));
            actionGroupElem.classList.add("ccm-editor-toolbar-actiongroup");

            for(const actionKey in actionGroup.actions) {

                const actionObj: CCMEditorAction = this
                    .actions[actionGroup.actions[actionKey]];
                actionGroupElem.appendChild(actionObj.getDocumentFragment());
            }
        }

        this.editorDiv = editor.appendChild(document.createElement("div"));
        this.editorDiv.classList.add("ccm-editor-editable");
        this.editorDiv.setAttribute("contenteditable", "true");
        this.editorDiv.innerHTML = this.textarea.value.trim();
        this.editorDiv.addEventListener("input", event => this.syncTextArea());
        document.addEventListener("selectionchange",
                                  event => this.selectionChanged());

        this.htmlSourceToolbar = editor
            .appendChild(document.createElement("div"));
        this.htmlSourceToolbar.classList.add("ccm-editor-toolbar");
        this.htmlSourceToolbar.classList.add("ccm-editor-hidden");
        const showEditorButton: Element = this.htmlSourceToolbar
            .appendChild(document.createElement("button"));
        const showEditorIcon: Element = showEditorButton
            .appendChild(document.createElement("i"));
        showEditorIcon.className = "fa fa-edit";
        const showEditorText: Element = showEditorButton
            .appendChild(document.createElement("span"));
        showEditorButton.setAttribute("title", "Show editor");
        showEditorText.textContent = "Show editor";
        showEditorText.className = "ccm-editor-accessibility";
        const ccmeditor: CCMEditor = this;
        showEditorButton.addEventListener("click", function() {
            ccmeditor.toggleHtml();
        });

        this.textarea.addEventListener("input", event => this.syncEditor());

        const textareaParent: Node = this.textarea.parentNode;
        textareaParent.insertBefore(editor, textarea);
        this.textarea.classList.add("ccm-editor-textarea");
        this.textarea.classList.add("ccm-editor-hidden");

        const headElem: Element = document.getElementsByTagName("head").item(0);
        const styleElem: Element = document.createElement("link");
        styleElem.setAttribute("rel", "stylesheet");
        if ("ccm-editor-css.path" in this.configuration.settings) {
            styleElem
                .setAttribute("href",
                              this.configuration.settings["ccm-editor-css.path"]);
        } else {
            styleElem.setAttribute("href", "ccm-editor.css");
        }
        headElem.appendChild(styleElem);

        //Check if Fontawesome is already loaded. If not add it
        if (!this.isFontAwesomeLoaded()) {

            const fontawesomeElem: Element = document.createElement("link");
            fontawesomeElem.setAttribute("rel", "stylesheet");
            if ("font-awesome.path" in this.configuration.settings) {
                fontawesomeElem
                    .setAttribute("href",
                                  this.configuration.settings["font-awesome.path"]);
            } else {
                fontawesomeElem
                    .setAttribute("href",
                                  "node_modules/font-awesome/css/font-awesome.min.css");
            }
            headElem.appendChild(fontawesomeElem);
        }

        //Avoid <br>
        document.execCommand("insertBrOnReturn", false, false);
    }

    public toggleHtml(): void {
        console.log("Toggle HTML view...");
        if (this.textarea.classList.contains("ccm-editor-hidden")) {
            console.log("HTML not visible, making visible...");
            this.textarea.classList.remove("ccm-editor-hidden");
            this.editorDiv.classList.add("ccm-editor-hidden");
        } else {
            console.log("HTML is visible, make invisible...");
            this.textarea.classList.add("ccm-editor-hidden");
            this.editorDiv.classList.remove("ccm-editor-hidden");
        }
    }

    public syncTextArea() {
        this.textarea.value = this.editorDiv.innerHTML;
    }

    public syncEditor() {
        this.editorDiv.innerHTML = this.textarea.value.trim();
    }

    private selectionChanged() {
        console.log("Selection changed.");
        const selection: Selection = document.getSelection();
        for(const key in this.actions) {

            this.actions[key].selectionChanged(selection);
        }
    }

    private isFontAwesomeLoaded(): boolean {

        const linkElems: NodeList = document.getElementsByTagName("link");

        for(let i = 0; i < linkElems.length; i++) {

            const linkElem: Element = linkElems.item(i) as Element;

            if (linkElem.hasAttribute("rel")
                && linkElem.getAttribute("rel") == "stylesheet"
                && linkElem.getAttribute("href").indexOf("fontawesome") != -1) {

                return true;
            }
        }

        return false;
    }
}

export function addEditor(selector: string,
                          configuration?: CCMEditorConfiguration) {

    const selectedElements: NodeList = document.querySelectorAll(selector);

    for(let i = 0; i < selectedElements.length; i++) {

        const selectedElement: HTMLTextAreaElement
            = selectedElements.item(i) as HTMLTextAreaElement;
        new CCMEditor(selectedElement, configuration);
    }
}

interface CCMEditorConfiguration {

    actionGroups: CCMEditorActionGroup[]

    settings: {}
}

interface CCMEditorActionGroup {

    name: string;
    title: string;

    actions: any[];
}

/**
 * Possible types for actions.
 */
export enum CCMEditorActionType {

    INSERT_BLOCK,
    INSERT_INLINE,
    OTHER
}

/**
 * Base class for all editor actions.
 */
export abstract class CCMEditorAction {

    protected editor: CCMEditor;
    protected settings: string[];

    protected fragment: DocumentFragment;

    constructor(editor: CCMEditor, settings: any) {
        this.editor = editor;
        this.settings = settings;

        this.fragment = document.createDocumentFragment();
    }

    /**
     * Generate the HTML for the control(s) for the action. The Element
     * returned by this method is added to the toolbar of the editor if
     * the plugin is added to the editor.
     */
    getDocumentFragment(): DocumentFragment {
        return this.fragment;
    }

    /**
     * Return the type of the action.
     */
    abstract getActionType(): CCMEditorActionType;

    /**
     * Verify is the action is applicable for the current element.
     * This will be a future extension.
     *
     * @param element The element.
     */
    public isApplicableFor(element: Element): Boolean {
        return true;
    }

    public abstract selectionChanged(selection: Selection): void;

    /**
     * Enables the controls for the action.
     */
    abstract enableAction(): void;

    /**
     * Disables the controls for the action.
     */
    abstract disableAction(): void;
}

export class FormatBlockAction extends CCMEditorAction {

    private blockSelect: HTMLSelectElement;
    private values: string[];

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        const blockSelectLabel = this.fragment
            .appendChild(document.createElement("label"));
        const blockSelectLabelSpan = blockSelectLabel
            .appendChild(document.createElement("span"));
        blockSelectLabelSpan.textContent = "Block element";
        blockSelectLabelSpan.className = "ccm-editor-accessibility";
        this.blockSelect = blockSelectLabel
            .appendChild(document.createElement("select"));

        const emptyOption: Element = document.createElement("option");
        emptyOption.setAttribute("value", "");
        emptyOption.textContent = "";
        this.blockSelect.appendChild(emptyOption);

        this.values = [];

        for(const block of settings["formatBlock.blocks"]) {
            const option: Element = document.createElement("option");
            option.setAttribute("value", block.element.toLowerCase());
            this.values.push(block.element);
            option.textContent = block.title;
            this.blockSelect.appendChild(option);
        }

        const blockSelect: HTMLSelectElement = this.blockSelect;
        this.blockSelect.addEventListener("change", function(event) {

            const blockElem: string = blockSelect.value;
            if (blockElem !== null && blockElem.length > 0) {
                document.execCommand("formatBlock", false, blockElem);
                editor.syncTextArea();
            }
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_BLOCK;
    }

    selectionChanged(selection: Selection) {

        console.log("selection.anchorNode = " + selection.anchorNode);
        const blockElem: Element = this.findBlockElement(selection.anchorNode);
        if (blockElem === null) {
            console.log("Selection is not in a known block element.");
            this.blockSelect.value = "";
        } else {
            console.log("Selection is in block element \"" + blockElem.tagName.toLowerCase + "\"");
            this.blockSelect.value = blockElem.tagName.toLowerCase();
        }
    }

    private findBlockElement(node: Node): Element {

        if (node instanceof Element) {
            console.log("Current node is an element.");
            const elem: Element = node as Element;
            console.log("elem.tagName = " + elem.tagName.toLowerCase());
            if(this.values.indexOf(elem.tagName.toLowerCase()) === -1) {
                console.log("elem.tagName is not in the values array.");
                if (elem.parentNode === null) {
                    console.log("elem has no parent node. Returning null.");
                    return null;
                } else {
                    console.log("Continuing with elem.parentNode");
                    return this.findBlockElement(elem.parentNode);
                }
            } else {
                return elem;
            }
        } else {
            console.log("Current node is not an element node.");
            if (node.parentNode === null) {
                console.log("Current node has no parent, returning null.");
                return null;
            } else {
                console.log("Continuing with parent node...");
                return this.findBlockElement(node.parentNode);
            }
        }
    }

    enableAction(): void {
        this.blockSelect.removeAttribute("disabled");
    }

    disableAction(): void {
        this.blockSelect.setAttribute("disabled", "true");
    }
}

/**
 * Action for making the selected text bold (strongly emphasised). Wraps the
 * selected text in a <code>b</code> element.
 */
export class MakeBoldAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-bold";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button
            .setAttribute("title", "Make selected text bold (mark as strongly emphasised).");
        text.textContent = "Bold";
        //text.textContent = "Make selected text bold (mark as strongly emphasised).";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event) {

            event.preventDefault();
            document.execCommand("bold");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }

}

/**
 * Action for making the selected text italic (emphasised). Wraps the
 * selected text in a <code>i</code> element.
 */
export class MakeItalicAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-italic";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Make selected italic (mark as emphasised)");
        //text.textContent = "Make selected italic (mark as emphasised).";
        text.textContent = "Italic";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event) {

            event.preventDefault();
            document.execCommand("italic");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

/**
 * Action for making the selected text underlined. Wraps the selected text
 * in an <code>u</code> element
 */
export class MakeUnderlineAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {

        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-underline";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title",
                                 "Make selected text underlined. "
                                    + "Use with caution because many people "
                                    + "think that underlined text is a link.")
        text.textContent = "Underline";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event) {

            event.preventDefault();
            document.execCommand("underline");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class StrikeThroughAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {

        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-strikethrough";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Strike through selected text.");
        text.textContent = "Strike out";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event){

            event.preventDefault();
            document.execCommand("strikeThrough");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class ToggleHtmlAction extends CCMEditorAction {

    private showHtmlButton: HTMLButtonElement;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.showHtmlButton = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.showHtmlButton
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-code";

        const text: Element = this.showHtmlButton
            .appendChild(document.createElement("span"));
        this.showHtmlButton.setAttribute("title", "Show HTML source code.");
        text.textContent = "Show HTML";
        text.className = "ccm-editor-accessibility";

        this.showHtmlButton.addEventListener("click", function(event) {

            event.preventDefault();
            editor.toggleHtml();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.OTHER;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.showHtmlButton.removeAttribute("disabled");
    }

    disableAction(): void {
        this.showHtmlButton.setAttribute("disabled", "true");
    }
}

export class SubscriptAction extends CCMEditorAction {

    private button: HTMLButtonElement;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-subscript";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Make subscript.");
        text.textContent = "Subscript";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event) {

            event.preventDefault();
            document.execCommand("subscript");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class SuperscriptAction extends CCMEditorAction {

    private button: HTMLButtonElement;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-superscript";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Make superscript.");
        text.textContent = "Superscript";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event) {

            event.preventDefault();
            document.execCommand("superscript");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class RemoveFormatAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {

        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-remove";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Remove all formatting from selected text.");
        text.textContent = "Remove format";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event){

            event.preventDefault();
            document.execCommand("removeFormat");
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.OTHER;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class InsertOrderedListAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {

        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-list-ol";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Insert an ordered list.");
        text.textContent = "Ordered list";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event){

            event.preventDefault();
            document.execCommand("insertOrderedList", false);
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_BLOCK;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class InsertUnorderedListAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {

        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-list-ul";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button.setAttribute("title", "Insert an unordered list.");
        text.textContent = "Unordered list";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event){

            event.preventDefault();
            document.execCommand("insertUnorderedList", false);
            editor.syncTextArea();
            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_BLOCK;
    }

    isApplicableFor(element: Element): Boolean {
        return true;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}

export class InsertExternalLinkAction extends CCMEditorAction {

    private button: Element;

    constructor(editor: CCMEditor, settings: any) {
        super(editor, settings);

        this.button = this.fragment
            .appendChild(document.createElement("button"));

        const icon: Element = this.button
            .appendChild(document.createElement("i"));
        icon.className = "fa fa-external-link";

        const text: Element = this.button
            .appendChild(document.createElement("span"));
        this.button
            .setAttribute("title", "Insert an external link.");
        text.textContent = "External link";
        text.className = "ccm-editor-accessibility";

        this.button.addEventListener("click", function(event) {

            event.preventDefault();

            const currentRange: Range = document.getSelection().getRangeAt(0);

            const dialogFragment: DocumentFragment = document
                .createDocumentFragment();
            const dialogElem: Element = dialogFragment
                .appendChild(document.createElement("div"));
            dialogElem.className = "ccm-editor-dialog";
            const dialogTitleElem: Element = dialogElem
                .appendChild(document.createElement("h1"));
            dialogTitleElem.textContent = "Create external link";
            const dialogForm: HTMLFormElement = dialogElem
                .appendChild(document.createElement("form"));
            const urlFieldLabel: Element = dialogForm
                .appendChild(document.createElement("label"));
            urlFieldLabel.setAttribute("for", "ccm-editor-external-link-url");
            urlFieldLabel.textContent = "Target URL";
            const urlField: HTMLInputElement = dialogForm
                .appendChild(document.createElement("input"));
            urlField.setAttribute("id", "ccm-editor-external-link-url");
            urlField.setAttribute("type", "text");
            const newWindowLabel: Element = dialogForm
                .appendChild(document.createElement("label"));
            newWindowLabel.setAttribute("for",
                                        "ccm-editor-external-link-new-window");
            newWindowLabel.textContent = "Open in new Window?";
            const newWindowCheckbox: HTMLInputElement = dialogForm
                .appendChild(document.createElement("input"));
            newWindowCheckbox.setAttribute("id",
                                           "ccm-editor-external-link-new-window");
            newWindowCheckbox.setAttribute("type", "checkbox");
            const okButton: HTMLButtonElement = dialogForm
                .appendChild(document.createElement("button"));
            const cancelButton: HTMLButtonElement = dialogForm
                .appendChild(document.createElement("button"));

            okButton.textContent = "OK";
            cancelButton.textContent = "Cancel";

            const bodyElem = document.getElementsByTagName("body").item(0);

            okButton.addEventListener("click", function(event){
                event.preventDefault();

                bodyElem.removeChild(dialogElem);
                document.getSelection().removeAllRanges();
                document.getSelection().addRange(currentRange);

                document.execCommand("createLink", false, urlField.value);

                return false;
            });

            cancelButton.addEventListener("click", function(){
                event.preventDefault();

                bodyElem.removeChild(dialogElem);
                document.getSelection().removeAllRanges();
                document.getSelection().addRange(currentRange);

                return false;
            });

            bodyElem.appendChild(dialogFragment);

            return false;
        });
    }

    getActionType(): CCMEditorActionType {
        return CCMEditorActionType.INSERT_INLINE;
    }

    selectionChanged(selection: Selection) {

    }

    enableAction(): void {
        this.button.removeAttribute("disabled");
    }

    disableAction(): void {
        this.button.setAttribute("disabled", "true");
    }
}
