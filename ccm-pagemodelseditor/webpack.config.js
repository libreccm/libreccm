const path = require('path');

module.exports = {

    devtool: "inline-source-map",

    entry: {
        pagemodeleditor: "./src/main/typescript/ccm-pagemodelseditor/index.tsx"
    },

    output: {
        path: path.resolve(__dirname, "src/main/resources/dist"),
        filename: "ccm-pagemodelseditor.js"
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
