const path = require('path');

module.exports = {

    devtool: "source-map",

    entry: {
        "ccm-cms-pagemodelseditor": "./src/main/typescript/ccm-cms-pagemodelseditor.ts",
        "ccm-cms-tinymce": "./src/main/typescript/ccm-cms-tinymce.ts",
    },

    mode: "development",

    output: {
        //path: path.resolve(__dirname, "src/main/resources/dist"),
        path: path.resolve(__dirname, "target/generated-resources/scripts/dist"),
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
