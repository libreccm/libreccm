import "bootstrap";
import * as $ from "jquery";
import { ChainedCommands, Editor } from "@tiptap/core";
import Gapcursor from "@tiptap/extension-gapcursor";
import StarterKit from "@tiptap/starter-kit";
import Subscript from "@tiptap/extension-subscript";
import Superscript from "@tiptap/extension-superscript";
import Table from "@tiptap/extension-table";
import TableRow from "@tiptap/extension-table-row";
import TableCell from "@tiptap/extension-table-cell";
import TableHeader from "@tiptap/extension-table-header";

const BUTTONS: CmsEditorButton[] = [
    {
        selector: ".tiptap-emph",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().toggleItalic().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleItalic()
                .run();
        }
    },
    {
        selector: ".tiptap-strong-emph",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().toggleBold().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleBold()
                .run();
        }
    },
    {
        selector: ".tiptap-code",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().toggleCode().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleCode()
                .run();
        }
    },
    {
        selector: ".tiptap-strikethrough",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().toggleStrike().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleStrike()
                .run();
        }
    },
    {
        selector: ".tiptap-subscript",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleSubscript()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleSubscript()
                .run();
        }
    },
    {
        selector: ".tiptap-superscript",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleSuperscript()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleSuperscript()
                .run();
        }
    },
    {
        selector: ".tiptap-h1",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeading({ level: 1 })
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeading({ level: 1 })
                .run();
        }
    },
    {
        selector: ".tiptap-h2",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeading({ level: 2 })
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeading({ level: 2 })
                .run();
        }
    },
    {
        selector: ".tiptap-h3",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeading({ level: 3 })
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeading({ level: 3 })
                .run();
        }
    },
    {
        selector: ".tiptap-h5",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeading({ level: 5 })
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeading({ level: 5 })
                .run();
        }
    },
    {
        selector: ".tiptap-h6",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeading({ level: 6 })
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeading({ level: 6 })
                .run();
        }
    },
    {
        selector: ".tiptap-paragraph",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().clearNodes().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .clearNodes()
                .run();
        }
    },
    {
        selector: ".tiptap-blockquote",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleBlockquote()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleBlockquote()
                .run();
        }
    },
    {
        selector: ".tiptap-codeblock",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleCodeBlock()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleCodeBlock()
                .run();
        }
    },
    {
        selector: ".tiptap-ul",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleBulletList()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleBulletList()
                .run();
        }
    },
    {
        selector: ".tiptap-ol",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleOrderedList()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleOrderedList()
                .run();
        }
    },
    {
        selector: ".cms-editor-insert-table-dialog",
        command: cmsEditor => {
            return true;
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .insertTable()
                .run();
        }
    },
    {
        selector: ".cms-editor-insert-table-dialog .btn-success",
        command: cmsEditor => {
            const dialog = cmsEditor
                .getEditorElem()
                .querySelector(".cms-editor-insert-table-dialog");
            if (!dialog) {
                return false;
            }
            const rowsInput = dialog.querySelector(
                "input#rows"
            ) as HTMLInputElement;
            const colsInput = dialog.querySelector(
                "input#cols"
            ) as HTMLInputElement;
            const headerRowInput = dialog.querySelector(
                "input#headerRow"
            ) as HTMLInputElement;
            console.log(`rowsInput = ${rowsInput}`);
            console.log(`colsInput = ${colsInput}`);
            console.log(`headerRowInput = ${headerRowInput}`);
            const rows = parseInt(rowsInput.value, 10);
            const cols = parseInt(colsInput.value, 10);
            const headerRow = JSON.parse(headerRowInput.value) as Boolean;
            const insertTableDialog = $("#insert-table-dialog") as any;
            insertTableDialog.modal("hide");
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .insertTable({
                    // allowTableNodeSelection: true,
                    // cellMinWidth: 150,
                    cols: cols,
                    // headerRow: headerRow,
                    // resizable: true,
                    rows: rows
                })
                .run();
        },
        can: cmsEditor => {
            return true;
        }
    },
    {
        selector: ".tiptap-insert-table-row-before",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().addRowBefore().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .addRowBefore()
                .run();
        }
    },
    {
        selector: ".tiptap-insert-table-row-after",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().addRowAfter().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .addRowAfter()
                .run();
        }
    },
    {
        selector: ".tiptap-insert-table-column-before",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .addColumnBefore()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .addColumnBefore()
                .run();
        }
    },
    {
        selector: ".tiptap-insert-table-column-after",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().addColumnAfter().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .addColumnAfter()
                .run();
        }
    },
    {
        selector: ".tiptap-remove-table-row",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().deleteRow().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .deleteRow()
                .run();
        }
    },
    {
        selector: ".tiptap-remove-table-column",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().deleteColumn().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .deleteColumn()
                .run();
        }
    },
    {
        selector: ".tiptap-remove-table",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().deleteTable().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .deleteTable()
                .run();
        }
    },
    {
        selector: ".tiptap-toggle-table-header-row",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeaderRow()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeaderRow()
                .run();
        }
    },
    {
        selector: ".tiptap-toggle-table-header-column",
        command: cmsEditor => {
            return cmsEditor
                .getEditor()
                .chain()
                .focus()
                .toggleHeaderColumn()
                .run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .toggleHeaderColumn()
                .run();
        }
    },
    {
        selector: ".tiptap-merge-table-cells",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().mergeCells().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .mergeCells()
                .run();
        }
    },
    {
        selector: ".tiptap-split-table-cell",
        command: cmsEditor => {
            return cmsEditor.getEditor().chain().focus().splitCell().run();
        },
        can: cmsEditor => {
            return cmsEditor
                .getEditor()
                .can()
                .chain()
                .focus()
                .splitCell()
                .run();
        }
    }
    // {
    //     selector: "",
    //     command: cmsEditor => {},
    //     can: cmsEditor => {}
    // },
    // {
    //     selector: "",
    //     command: cmsEditor => {},
    //     can: cmsEditor => {}
    // },
    // {
    //     selector: "",
    //     command: cmsEditor => {},
    //     can: cmsEditor => {}
    // },
    // {
    //     selector: "",
    //     command: cmsEditor => {},
    //     can: cmsEditor => {}
    // }
];

class CmsEditor {
    private editor: Editor;
    private editorElem: HTMLElement;
    private saveUrl: string;

    public constructor(
        editor: Editor,
        editorElem: HTMLElement,
        saveUrl: string
    ) {
        this.editor = editor;
        this.editorElem = editorElem;
        this.saveUrl = saveUrl;

        console.log("initializing editor buttons");
        const buttonsElem = editorElem.querySelector(
            ".cms-tiptap-editor-buttons"
        );
        if (buttonsElem) {
            for (const button of BUTTONS) {
                const buttonElem = buttonsElem.querySelector(button.selector);
                if (buttonElem) {
                    buttonElem.addEventListener("click", event => {
                        event.preventDefault();
                        button.command(this);
                    });
                } else {
                    continue;
                }
            }
        } else {
            console.error("editorButtonsElem not found.");
            return;
        }

        editor.on("selectionUpdate", ({ editor }: { editor: Editor }) => {
            console.log(`checkButton - this.editorElem = ${this.editorElem}`);
            const buttonsElem = editorElem.querySelector(
                ".cms-tiptap-editor-buttons"
            );
            if (!buttonsElem) {
                return;
            }
            for (const button of BUTTONS) {
                const elem = buttonsElem.querySelector(button.selector);
                if (elem) {
                    const buttonElem = elem as HTMLButtonElement;
                    if (button.can(this)) {
                        buttonElem.removeAttribute("disabled");
                    } else {
                        buttonElem.setAttribute("disabled", "disabled");
                    }
                } else {
                    continue;
                }
            }
        });

        console.log(`editorElem = ${editorElem}`);

        const saveButton = editorElem.querySelector(".cms-editor-save-button");
        saveButton?.addEventListener("click", event => this.save(event));
    }

    protected async save(event: Event) {
        event.preventDefault();

        const params = new URLSearchParams();
        params.append("value", this.editor.getHTML());

        try {
            const response = await fetch(this.saveUrl, {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: params
            });
            if (response.ok) {
            } else {
                this.showSaveFailedMessage(
                    response.status,
                    response.statusText
                );
            }
        } catch (error) {
            this.showSaveFailedErrMessage(error as string);
        }
    }

    protected showSaveFailedMessage(status: number, statusText: string) {
        this.showMessage("#cms-editor-msg-save-failed");
        console.error(
            `Failed to save text. Status: ${statusText}. Status Text: ${statusText}`
        );
    }

    protected showSaveFailedErrMessage(error: string) {
        this.showMessage("#cms-editor-msg-save-failed");
        console.error(error);
    }

    protected showMessage(messageId: string) {
        const template = this.editorElem.querySelector(
            messageId
        ) as HTMLTemplateElement;
        const message = template.content.cloneNode(true);
        this.editorElem.querySelector(".cms-editor-messages")?.append(message);
    }

    public getEditor(): Editor {
        return this.editor;
    }

    public getEditorElem(): HTMLElement {
        return this.editorElem;
    }
}

class CmsEditorBuilder {
    private editorElem: HTMLElement;
    private saveUrl: string;
    private variantUrl: string;

    constructor(editorElem: HTMLElement, saveUrl: string, variantUrl: string) {
        this.editorElem = editorElem;
        this.saveUrl = saveUrl;
        this.variantUrl = variantUrl;
    }

    public async buildEditor(): Promise<CmsEditor> {
        console.log("Build CMS Editor.");
        const canvasElement = this.editorElem.querySelector(
            ".cms-tiptap-editor-canvas"
        );
        if (!canvasElement) {
            this.showMessage("#cms-editor-msg-canvas-element-not-found");
            console.error("canvasElem not found.");
            throw "canvasElem not found.";
        }

        const variant = await this.fetchVariant(this.variantUrl);

        const editor: Editor = new Editor({
            element: canvasElement,
            extensions: [
                Gapcursor,
                StarterKit,
                Subscript,
                Superscript,
                Table.configure({
                    allowTableNodeSelection: true,
                    cellMinWidth: 100,
                    handleWidth: 25,
                    resizable: true
                }),
                TableRow,
                TableHeader,
                TableCell
            ],
            content: variant
        });

        return new CmsEditor(editor, this.editorElem, this.saveUrl);
    }

    protected async fetchVariant(variantUrl: string): Promise<string> {
        try {
            const response = await fetch(variantUrl, {
                method: "GET",
                credentials: "include"
            });

            if (response.ok) {
                return await response.text();
            } else {
                this.showLoadVariantFailedMessage(
                    response.status,
                    response.statusText
                );
                throw `Failed to load variant. Status: ${response.status}, Status Text: ${response.statusText}`;
            }
        } catch (error) {
            this.showLoadVariantFailedErrorMessage(error as string);
            throw error;
        }
    }

    protected showLoadVariantFailedMessage(status: number, statusText: string) {
        this.showMessage("#cms-editor-msg-variant-load-failed");
        console.error(
            `Failed to load variant: HTTP Status: ${status}, statusText: ${statusText}`
        );
    }

    protected showLoadVariantFailedErrorMessage(error: string) {
        this.showMessage("#cms-editor-msg-variant-load-failed");
        console.error(`Failed to load variant: ${error}`);
    }

    protected showMessage(messageId: string) {
        const template = this.editorElem.querySelector(
            messageId
        ) as HTMLTemplateElement;
        const message = template.content.cloneNode(true);
        this.editorElem.querySelector(".cms-editor-messages")?.append(message);
    }
}

interface CmsEditorParameters {
    editorElem: HTMLElement;
    variantUrl: string;
}

interface CmsEditorButton {
    selector: string;
    command: (cmsEditor: CmsEditor) => boolean;
    can: (cmsEditor: CmsEditor) => boolean;
}

interface EditorParam {
    editor: Editor;
}

export { CmsEditor, CmsEditorBuilder, CmsEditorParameters };
