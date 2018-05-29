const path = require('path');

module.exports = {

    devtool: "inline-source-map",

    entry: {
        ccmcms: "./src/main/typescript/ccm-cms/ccm-cms-pagemodelseditor.tsx"
    },

    output: {
        //path: path.resolve(__dirname, "src/main/resources/dist"),
        path: path.resolve(__dirname, "target/generated-resources/dist"),
        filename: "ccm-cms-pagemodelseditor.js"
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
