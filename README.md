# LibreCCM

The [https://libreccm.org](LibreCCM) framework and the 
[https://librecms.org](LibreCMS) web content management system. 

This repository contains the Jakarta EE based, new version of LibreCCM 
and LibreCMS. We are now using Maven as build tool. 

Some more documentation is provided as Maven project site. To create the site
run

    mvn clean package site site:stage

and open the ./target/staging/index.html file in your browser.

The recreate the site run

    mvn clean package site site:stage

again.

To include integration tests into the reports

    mvn clean package test site site:stage -P$profile-name

Note: If there are test failures the package goal fails and the site is not
build. The build the site anywhy use

    mvn clean package site site:stage -Dmaven.test.failure.ignore=true

or with a profile

    mvn clean package site site:stage -Dmaven.test.failure.ignore=true -Pwildfly-remote-h2-mem

The available profiles are listed in the documentation. All modules should 
provide a profile called wildfly-remote-h2-mem. This profile uses a remote
Wildfly application server and its integrated H2 in-memory database for
running the tests. Before you can run the integration tests you must download
Wildfly from http://www.wildfly.org. Unzip the downloaded archive and start
the server using the bin/standalone.sh file. Then go to another terminal,
navigate to the CCM NG directory and run

    mvn clean package test site site:stage -Pwildfly-remote-h2-mem

To run LibreCCM choose the bundle to run, for instance
ccm-bundle-devel-wildfly. Depending on the bundle and the selected profile 
you may have to configure a datasource in the Wildfly server and create a
database. Also you need to configure an environement variable which points to
your Wildfly installation. Then you can run LibreCCM using

    mvn package wildfly:run -DskipTests -pl ccm-bundle-devel-wildfly -am -Pgeneric

The above example skips all tests to speed up the start process. The generic
profile used in this example uses an existing Wildfly installation. The
JBOSS_HOME environment variable must either be configured or must be provided.
Alternativly you can provide the location of your Wildfly installation using
the -D switch: mvn -Djboss-as.home=/path/to/wildfly/

To install the archetypes in your local Maven repository run

    mvn [clean] install -pl $archetype-module -am

for example

mvn clean install -pl ccm-cms-archetypes-contenttype -am

Using the managed profiles (example is for testing):

mvn clean test -Djboss.home=/path/to/wildfly/ -DstartupTimeoutInSeconds=180 -Pwildfly-managed-h2-mem

You might need to increase the timeout.

For convience, we also provide a small Bash script which makes calling some Maven tasks easier. For more information please refer to the help of the ccm.sh script:

    ccm.sh --help


 
