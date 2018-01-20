module.exports = function(grunt) {
    grunt.initConfig({
        ts: {
            default : {
                tsconfig: true,
                options: {
                    module: "amd",
                    moduleResolution: "classic"
                }
            }
        },
    });
    grunt.loadNpmTasks("grunt-ts");
    grunt.registerTask("default", ["ts"]);
};
