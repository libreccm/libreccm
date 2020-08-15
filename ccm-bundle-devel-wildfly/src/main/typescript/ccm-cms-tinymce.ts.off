import * as tinymce from "tinymce";

import "tinymce/themes/modern/theme";

import "tinymce/plugins/code";
import "tinymce/plugins/lists";
import "tinymce/plugins/nonbreaking";
import "tinymce/plugins/noneditable";
import "tinymce/plugins/paste";
import "tinymce/plugins/searchreplace";
import "tinymce/plugins/table";
import "tinymce/plugins/template";
import "tinymce/plugins/visualblocks";
import "tinymce/plugins/wordcount";

import "ccm-cms-tinymce";

// Atom IDE reports an error here, but thats not correct. The Typescript
// compiler uses the definitions from @types/webext-env automatically.
require.context(
    "file-loader?name=[path][name].[ext]"
        + "&context=node_modules/tinymce!tinymce/skins",
    true,
    /.*/,
);

tinymce.init({
    //menubar: "tools",
    plugins: "code insert-media lists nonbreaking noneditable paste searchreplace table template visualblocks wordcount",
    selector: ".tinymce",
    //toolbar: "code",
});
