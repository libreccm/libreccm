LibreCCM
========

The documentation of project is provided as Maven project site. To
create the site run

    mvn site site:stage

and open the file ./target/staging/index.html in your browser.

To recreate the site run

    mvn clean site site:stage

To include integration tests into the reports

    mvn clean site site:stage -Pprofile-name

The available profiles are listed in the documentation.

To install the archetypes in your local Maven repository run

mvn [clean] install -pl $archetype-module

for example

mvn clean install -pl ccm-cms-archetypes-contenttype
