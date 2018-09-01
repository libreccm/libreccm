const path = require("path");

module.exports = {

    devtool: "source-map",

    entry: {
        "ccm-cms-tinymce-insertmedia/plugin": "./src/main/typescript/tinymce/plugins/ccm-cms-tinymce-insertmedia/index.ts",
    },

    output: {
        path: path.resolve(__dirname, "target/generated-resources/tinymce/plugins"),
        filename: "[name].js"
    },

    resolve: {
        extensions: [".webpack.js", "web.js", ".ts", ".tsx", ".js"]
    },

    module: {
        rules: [
            { test: /\.tsx?$/, loader: "ts-loader"}
        ]
    }
};
