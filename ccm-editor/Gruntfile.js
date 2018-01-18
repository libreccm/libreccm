module.exports = function(grunt) {
    grunt.initConfig({
        ts: {
            default : {
                src: ["src/main/typescript/**/*.ts"],
                options: {
                    module: "amd"
                }
            }
        },
        clean: ['scripts/*.js', 'scripts/*.js.map', 'scripts/.tscache']
    });
    grunt.loadNpmTasks("grunt-ts");
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.registerTask("default", ["ts"]);
};

