module.exports = {
    mode: "production",
    devtool: "source-map",
    entry: {
        "cms-admin": "./src/main/typescript/content-sections/cms-admin.ts",
        "article-text-step": "./src/main/typescript/content-sections/article-text-step.ts"
    },
    output: {
        filename: "[name].js",
        path: __dirname + "/target/generated-resources/assets/@content-sections"
    },
    resolve: {
        extensions: [".tsx", ".ts", ".js", ".json"]
    },
    module: {
        rules: [
            // all files with a '.ts' or '.tsx' extension will be handled by 'ts-loader'
            { test: /\.tsx?$/, use: ["ts-loader"], exclude: /node_modules/ }
        ]
    }
};
