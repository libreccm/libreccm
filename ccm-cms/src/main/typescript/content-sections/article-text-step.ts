import { CmsEditorBuilder, CmsEditor } from "./cms-editor";

document.addEventListener("DOMContentLoaded", event => {
    const editorElem = document.querySelector("#cms-article-text-editor");

    if (editorElem) {
        const saveUrl = editorElem.getAttribute("data-save-url");
        const variantUrl = editorElem.getAttribute("data-variant-url");

        if (!saveUrl) {
            console.error("saveUrl is null");
            return;
        }

        if (!variantUrl) {
            console.error("variantUrl is null");
            return;
        }

        const builder = new CmsEditorBuilder(
            editorElem as HTMLElement,
            saveUrl,
            variantUrl
        );

        builder.buildEditor();
    }
});
