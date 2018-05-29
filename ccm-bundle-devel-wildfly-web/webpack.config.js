const path = require('path');

module.exports = {

    devtool: "source-map",

    entry: {
        ccmcms: "./src/main/typescript/ccm-cms.ts"
    },

    mode: "production",

    output: {
        //path: path.resolve(__dirname, "src/main/resources/dist"),
        path: path.resolve(__dirname, "target/generated-resources/scripts/dist"),
        filename: "ccm-cms.js"
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
