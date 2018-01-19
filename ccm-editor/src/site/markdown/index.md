The CCM Editor modul
--------------------

# Overview

The CCM Editor modules provides a simple HTML editor written in TypeScript 
for use in the backend of LibreCCM. TypeScript is super set of JavaScript. 
Along with features from the newest versions ECMA Script standard which are not
yet implemented by the browsers it also is a strongly typed programming 
language. This helps to avoid some types of mistakes which can easily occur 
For more information about TypeScript please
refer to the the [TypeScript homepage](http://www.typescriptlang.org). 
The TypeScript compiler (or better transpiler) creates JavaScript from 
TypeScript.

In addition to TypeScript this module also uses NPM as package manager for 
JavaScript libraries and Grunt as a TaskRunner. To build this module
you don't need to install any of these tools. 
They are integrated
into the build process of Maven by the 
[frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin).
This plugin for Maven downloads and installs an local copy of NPM, invokes
NPM to download the dependencies using NPM and calls Grunt to run the 
TypeScript compiler.

The editor uses the `contenteditable` attribute from HTML 5 also with several 
other features. Modern browsers provide almost necessary basic funtions for
an editor, this module only provides a UI for them. 

Supported browsers are:

* Mozilla Firefox (52 and later)
* Google Chrome (58 and later)
* Microsoft Edge (all versions)
* Apple Safari (10 and later)
* other based WebKit Browsers should also work (with recent versions of WebKit)

# Integration into a bundle

To integrate the editor into a bundle first declare a dependency to the 
`ccm-editor` module in the `pom.xml` file of the bundle:

    <dependency>
        <groupId>org.libreccm</groupId>
        <artifactId>ccm-editor</artifactId>
        <version>${project.parent.version}</version>
    </dependency>

In the Maven module of bundle which generates the WAR part in add the following
the to configuration of the `maven-war-plugin`:

    ...
    <build>
        ...
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <configuration>
                    <overlays>
                        <overlay>
                            <groupId>org.libreccm</groupId>
                            <artifactId>ccm-editor</artifactId>
                            <type>jar</type>
                        </overlay>
                        ...
                    </overlays>
                </configuration>
                ...
            </plugin>
        </plugins>
        ...
    </build>
    ...

The editor needs three external libraries: RequireJS, RequireJS DOMready and 
Font Awesome. The easiest way to include them into the bundle is to use the 
JARs provided by the [WebJars project](https://www.webjars.org) by adding them
to the dependencies section of the WAR module:

    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>font-awesome</artifactId>
        <version>4.7.0</version>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
         <artifactId>requirejs</artifactId>
         <version>2.3.5</version>
    </dependency>
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>requirejs-domready</artifactId>
         <version>2.0.1-2</version>
    </dependency>


In the WAR module add a folder `ccm-editor` to the `src/main/webapp` folder 
and create a file similar to the following one:

    requirejs(["./ccm-editor",
            "../node_modules/requirejs-domready/domReady!"],
          function(editor, doc) {

        editor.addEditor(".editor", {
            "commandGroups": [
                {
                        "name": "blocks",
                        "title": "Format blocks",
                        "commands": [
                            editor.FormatBlockCommand
                        ]
                },
                {
                    "name": "format-text",
                    "title": "Format text",
                    "commands": [
                        editor.MakeBoldCommand,
                        editor.MakeItalicCommand,
                        editor.MakeUnderlineCommand,
                        editor.StrikeThroughCommand,
                        editor.SubscriptCommand,
                        editor.SuperscriptCommand,
                        editor.RemoveFormatCommand,
                        editor.InsertExternalLinkCommand
                    ]
                },
                {
                        "name": "insert-list",
                        "title": "Insert list",
                        "commands": [
                            editor.InsertUnorderedListCommand,
                            editor.InsertOrderedListCommand
                        ]
                },
                {
                    "name": "html",
                    "title": "HTML",
                    "commands": [editor.ToggleHtmlCommand]
                }
            ],
            "settings": {
                "formatBlock.blocks": [
                    {
                        "element": "h3",
                        "title": "Heading 3"
                    },
                    {
                        "element": "h4",
                        "title": "Heading 4"
                    },
                    {
                        "element": "h5",
                        "title": "Heading 5"
                    },
                    {
                        "element": "h6",
                        "title": "Heading 6"
                    },
                    {
                        "element": "p",
                        "title": "Paragraph"
                    }
                ]
            }
        });
    });

This will load the editor using `require.js` (the editor is provided as a 
AMD JavaScript module) and call the wrapped function when the DOM tree is ready.
The function calls the `addEditor` function from the editor module and applies
the editor to all `textarea` elements with the class `.editor`. The parameter
is a CSS selector which you can modify according to your needs.

The editor is initialised with a configuration. The configuration is described
at the [configuration](configuration.html) page. The editor itself has no 
hardcoded functions. Instead all controls in its toolbar are provided as 
(TypeScript) classes which can be added by the configuration. The editor 
module provides several standard commands like formated block elements. Please
refer to the [Standard Commands](commands.html) for more information about these
commands.