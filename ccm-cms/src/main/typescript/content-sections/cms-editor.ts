import { Editor } from "@tiptap/core";
import StarterKit from "@tiptap/starter-kit";

document.addEventListener("DOMContentLoaded", function (event) {
    console.log("Starting editor");
    new Editor({
        element: document.querySelector('#cms-editor'),
        extensions: [
            StarterKit
        ],
        content: '<h1>Hello World</h1>'
    })


})
