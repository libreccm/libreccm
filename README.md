# LibreCCM

The [https://libreccm.org](LibreCCM) framework and the
[https://librecms.org](LibreCMS) web content management system.

## Overview

This repository contains the Jakarta EE based, new version of
[https://libreccm.org](LibreCCM) and [https://librecms.org](LibreCMS). We are
now using Maven as build tool.

Some more documentation is provided as Maven project site. To create the site
run

    mvn clean package site site:stage

and open the ./target/staging/index.html file in your browser.

The recreate the site run

    mvn package site site:stage

again.

Note: The ./target/staging folder is left intact when cleaning. If you
want to remove the folder you have delete it manually.

To include integration tests into the reports run

    mvn clean verifiy site site:stage -P${profileName}

Note: If there are test failures the build fails and the site is not
build. The build the site anywhy use

    mvn clean package site site:stage -Dmaven.test.failure.ignore=true

or with the integration tests included

    mvn clean verify site site:stage -Dmaven.test.failure.ignore=true -P${profileName}

## Running integration tests

Some of the modules provide integration tests which use
[Arquillian](http://arquillian.org/) to run tests inside an application server.
If a module provides integration tests it should have at least four profiles
for running them (at the time of writing):

* `run-its-with-wildfly-h2mem`: This profile uses the
  [wildfly-maven-plugin](https://docs.jboss.org/wildfly/plugins/maven/latest/)
  to start a Wildfly and run the integration tests. The H2 database which is
  integrated with Wildfly is used for run the tests. No configuration
  is necessary.
* `run-its-with-wildfly-pgsql`: This profile uses the
  [wildfly-maven-plugin](https://docs.jboss.org/wildfly/plugins/maven/latest/)
  to start a Wildfly and run the integration tests. A PostgreSQL database is
  used to run the tests. The connection parameters are configured using
  the `it-pgsql-datasources.properties` file in the project root. Make sure
  to create a database for all modules and configure them before using
  this profile.
* `run-its-in-remote-wildfly-h2mem`: This profile uses a remote Wildfly and
   the H2 database for
   running the tests. The user it responsible for starting the Wildfly container
   and for creating the required databases.
* `run-its-in-remote-wildfly-pgsql`: This profile uses a remote Wildfly and
   PostgreSQL databases for
   running the tests. The user it responsible for starting the Wildfly container
   and for creating the required databases.

To run the integration tests select a profile - we recommend to use the profiles
which use the wildfly-maven-plugin` to manage to Wildfly application server -
and run

    mvn verify site site:stage -P${profileName}

## Running LibreCCM (development environment)

Before running a bundle the WAR file has the be generated using

    mvn package

To run LibreCCM choose the bundle to run, for instance
`ccm-bundle-devel-wildfly`. You can run the bundle

    mvn wildfly:run -pl ${bundleName} -am

This will start a Wildfly server running the selected bundle. Before running
the bundle the Wildfly container has the be configured. For these purpose, run

    mvn wildfly:run -pl ${bundleName} -am -Psetup-runtime

This will start the Wildfly server, deploy the bundle WAR, and create a
datasource. At the moment only PostgreSQL is supported as database. The
datasource configuration is provided by the `datasource.properties` file in the
bundle directory. To create one copy the example file and adjust the settings.

You also have to create a wildfly.properties file which contains some settings
for the Wildfly server, for isntance the port the Wildfly server binds to. An 
example file is provided.

## Archetypes

To install the archetypes in your local Maven repository run

    mvn [clean] install -pl $archetype-module -am

for example

mvn clean install -pl ccm-cms-archetypes-contenttype -am




