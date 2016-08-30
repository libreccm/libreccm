LibreCCM
========

The documentation of project is provided as Maven project site. To
create the site run

    mvn package site site:stage

and open the file ./target/staging/index.html in your browser.

To recreate the site run

    mvn clean package site site:stage

To include integration tests into the reports

    mvn clean package site site:stage -P$profile-name

The available profiles are listed in the documentation. All modules should 
provide a profile called wildfly-remote-h2-mem. This profile uses a remote
Wildfly application server and its integrated H2 in-memory database for
running the tests. Before you can run the integration tests you must download
Wildfly from http://www.wildfly.org. Unzip the downloaded archive and start
the server using the bin/standalone.sh file. Then go to another terminal,
navigate to the CCM NG directory and run

    mvn clean package site site:stage -Pwildfly-remote-h2-mem

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

    mvn [clean] install -pl $archetype-module

for example

mvn clean install -pl ccm-cms-archetypes-contenttype
