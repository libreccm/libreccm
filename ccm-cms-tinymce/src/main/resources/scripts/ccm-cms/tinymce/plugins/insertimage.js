tinymce.PluginManager.add("ccm-cms-insertimage", function(editor, url) {

    console.log("Adding plugin ccm-cms-insertimage...");

    editor.addButton(
        "insertimage",
        {
            icon: "image",
            onclick: function() {
                openDialog(editor);
            },
            tooltip: "Insert image",
        },
    );

    editor.addMenuItem(
        "insert-image",
        {
            text: "Insert image",
            context: "tools",
            onclick: function() {
                openDialog(editor);
            },
        },
    );

});

function openDialog(editor) {

    console.log("Opening dialog");

    // const selectedImage = {
    //
    //     selectedUuid: undefined,
    //
    //     get imageUuid() {
    //         this.selectedUuid;
    //     },
    //     set imageUuid(value) {
    //         this.selectedUuid = value;
    //         console.log(`Set imageUuid to ${this.selectedUuid}`);
    //         editor.windowManager
    //     },
    // };

    let imageUuid = undefined;

    editor.windowManager.open({
        body: [
            {
                items: [
                    {
                        direction: "row",
                        items: [
                            {
                                // disabled: true,
                                // label: "Image",
                                // name: "imageUuid",
                                // type: "textbox",
                                // value: "foobar",
                                border: "1 1 1 1",
                                minWidth: 150,
                                minHeight: 150,
                                name: "imageViewer",
                                type: "container",
                            },
                            {
                                name: "browse",
                                onclick: (event) => {
                                    openBrowseDialog(editor, (uuid) => {
                                        console.log(`Selected image ${uuid}`);
                                        // editor.windowManager.getWindows()[0].find("#imageUuid").value(uuid);
                                        imageUuid = uuid;
                                        const contextPrefix = editor.getParam("contextprefix");
                                        const contentSection = editor.getParam("contentsection");
                                        const imageViewer = editor
                                                            .windowManager
                                                            .getWindows()[0]
                                                            .find("#imageViewer")
                                                            .toArray()[0];
                                        imageViewer
                                            .innerHtml(`<img src="${contextPrefix}/content-sections${contentSection}images/uuid-${uuid}?width=150" />`);
                                    });
                                },
                                text: "Browse",
                                type: "button",
                            },
                        ],
                        label: "Selected image",
                        layout: "flex",
                        type: "container",
                    },
                    {
                        label: "Caption",
                        name: "caption",
                        type: "textbox",
                    },
                    {
                        label: "Width",
                        name: "width",
                        size: 4,
                        type: "textbox",
                    },
                    {
                        label: "Height",
                        name: "height",
                        size: 4,
                        type: "textbox",
                    },
                    {
                        name: "lightbox",
                        text: "Lightbox?",
                        type: "Checkbox",
                    },
                    {
                        border: "1 1 1 1",
                        name: "imagePanel",
                        type: "container",
                    },
                ],
                minHeight: 200,
                minWidth: 400,
                name: "insertimage_dialog_container",
                onPostRender: function() {
                    // console.log(`imageUuid = ${this.find("#imageUuid").value()}`);
                    const imageUuid = this.find("#imageUuid").value();
                     console.log(`imageUuid = ${imageUuid}`);
                    // this.find("#imagePanel").innerHTML = `<pre>${imageUuid}</pre>`;
                    this.find("#imagePanel").append([
                        {
                            html: `<pre>${imageUuid}</pre>`,
                            // text: `${imageUuid}`,
                            // type: "label",
                            type: "container",
                        }
                    ]);
                    console.log(this.find("#imagePanel"));
                },
                type: "form",
            },
        ],
        onsubmit: function(event) {
            // console.log(selectedImage.imageUuid);
        },
        title: "Insert image",
    });

    console.log("Dialog opened.");
}

function openBrowseDialog(editor, selectImage) {

    editor.windowManager.open({
        body: [
            {
                // items: [
                //     {
                //         html: '<ul></ul>',
                //         name: "imageList",
                //         onPostRender: function() {
                //             console.log(`contentSection: ${editor.getParam("contentsection")}`);
                //             console.log(`contextPrefix: ${editor.getParam("contextprefix")}`);
                //
                //             const contextPrefix = editor.getParam("contextprefix");
                //             const contentSection = editor.getParam("contentsection");
                //
                //
                //
                //         },
                //         type: "container",
                //     }
                // ],
                layout: "flow",
                minHeight: 400,
                minWidth: 600,
                onClick: function(event) {

                    console.log("Click in Image Container...");
                    console.log(`...on image ${event.target.getAttribute("data-uuid")}`);
                    // selectedImage.imageUuid = event.target.getAttribute("data-uuid");
                    selectImage(event.target.getAttribute("data-uuid"));
                    editor.windowManager.close();
                },
                onPostRender: function() {

                    console.log(`contentSection: ${editor.getParam("contentsection")}`);
                    console.log(`contextPrefix: ${editor.getParam("contextprefix")}`);

                    const contextPrefix = editor.getParam("contextprefix");
                    const contentSection = editor.getParam("contentsection");

                    // console.log(`this.find(#imageList) = ${JSON.stringify(this.find("#imageList"))}`);
                    // this.find("#imageList").innerHtml("<ul><li>bar</li><li>barbar</li></ul>");

                    // const Throbber = tinymce.ui.Factory.get("Throbber");
                    // this.append(new tinymce.ui.Throbber(this.getContainerElm()));

                    console.log(`containerElm = ${this.getEl()}`);
                    const throbber = new tinymce.ui.Throbber(this.getEl());
                    throbber.show();
                    // const throbber = tinymce.ui.Factory.create({
                    //     elm: this,
                    //     type: "Throbber"});
                    // this.append(throbber);

                    const url = `${contextPrefix}/content-sections`
                        + `${contentSection}assets/`
                        + `?type=org.librecms.assets.Image`;
                    const requestInit = {
                        credentials: "same-origin",
                        method: "GET",
                    };
                    fetch(url, requestInit)
                    .then((response) => {
                        if (response.ok) {
                            response
                            .json()
                            .then((data) => {

                                // console.log(`imageList = ${this.find("#imageList")}`);
                                // console.log(JSON.stringify(this.find("#imageList")));

                                // let imageList = `Found ${data.length} images</pre>`;
                                // imageList = imageList + "<ul>";
                                const images = [];
                                for(const image of data) {
                                    images.push(
                                        `<div style="display: flex; margin-top: 10px; margin-bottom: 10px;">\
                                            <dt style="margin-right: 15px">\
                                                <img data-uuid="${image.uuid}"
                                                     src="${contextPrefix}/content-sections${contentSection}images/uuid-${image.uuid}?width=150"\
                                                     alt="${image.title}"
                                                     width="150"
                                                     style="width: 150px; cursor: pointer">\
                                            </dt>\
                                            <dd style="width: 430px; overflow: hidden">\
                                                <dl style="display: block">\
                                                    <div style="display: flex">\
                                                        <dt style="width: 5em">Title</dt>\
                                                        <dd style="hypens:auto; max-width: 15em; overflow: hidden">${image.title}</dd>\
                                                    </div>\
                                                    <div style="display: flex">\
                                                        <dt style="width: 5em">Filename</dt>\
                                                        <dd style="hypens:auto max-width: 15em; overflow: hidden">${image.properties.filename}</dd>\
                                                    </div>\
                                                    <div style="display: flex">\
                                                        <dt style="width: 5em">Width</dt>\
                                                        <dd style="">${image.properties.width}</dd>\
                                                    </div>\
                                                    <div style="display: flex">\
                                                        <dt style="width: 5em">Height</dt>\
                                                        <dd style="">${image.properties.height}</dd>\
                                                    </div>\
                                            </dl>\
                                        </dd>\
                                    </div>`);
                                }
                                // imageList = imageList + "</ul>";

                                const html = `<pre style="margin-bottom: 20px">Found ${data.length} images</pre>\
                                <dl>\
                                ${images.join("\n")}\
                                </dl>`;

                                this.innerHtml(html);
                                throbber.hide();
                                // for(const property of dialog.find("#imageList").getOwnPropertyNames()) {
                                //     console.log(`imageList.${propery}`);
                                // }
                                // console.log(`imageList.getEl = ${dialog.find("#imageList").getEl}`);
                                // const imageList = dialog.find("#imageList").getEl();
                                //
                                // for(const image of data) {
                                //     console.log(image.name);
                                //     imageList.append({
                                //         html: `<li>${image.name}</li>`,
                                //     });
                                // }
                            })
                            .catch((error) => {
                                throbber.hide();
                                editor.notificationManager.open({
                                    text: `Failed to retrieve available images: `
                                        + ` ${error}`,
                                    type: "error",
                                });
                            });
                        } else {
                            throbber.hide();
                            editor.notificationManager.open({
                                text: `Failed to retrieve images: `
                                    + `Status: ${response.status} `
                                    + `${response.statusText}`
                            });
                        }
                    })
                    .catch((error) => {
                        throbber.hide();
                        editor.notificationManager.open({
                            text: `Failed to retrieve available images: `
                                + ` ${error}`,
                            type: "error",
                        });
                    });
                },
                style: "overflow: auto",
                type: "container",
            }
        ],
        minWidth: 600,
        minHeight: 400,
        // onSubmit: function() {
        //     selectedImage.imageUuid = "0000-0000-0000-0000";
        // },
        title: "Select image",
    });
}
