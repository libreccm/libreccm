module.exports = function(grunt) {
    grunt.initConfig({
        ts: {
            default : {
                options: {
                    module: "amd",
                    tsconfig: true,
                    moduleResolution: "classic"
                }
            }
        },
        clean: ['scripts/*.js', 'scripts/*.js.map', 'scripts/.tscache']
    });
    grunt.loadNpmTasks("grunt-ts");
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.registerTask("default", ["ts"]);
};
