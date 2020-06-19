# ccm-bundle-devel-wildfly README

This module creates a WAR which can be deployed to a Wildfly Application 
Server. For testing, the module also provides an runtime environment
and the setup. The runtime is managed using the 
[wildfly-maven-plugin](https://docs.jboss.org/wildfly/plugins/maven/latest/).

At the moment the runtime only supports PostgreSQL as database.

To use these runtime some prepration steps are necessary.

1. Create the configuration files. The runtime is configured using two files:
    * wildfly.properties allows it to configure the ports used by the Wildfly server
    * datasource.properties provides the configuration data for datasource used by
      LibreCCM. 

      For both files examples are provided (`datasource.example.properties`, 
      `wildfly.example.properties`). Copy the example files and customize
      the settings is necessary.

2. Run a build using the `setup-runtime` profile. It is important to add the 
   `package` goal, otherwise the WAR file which is deployed in this step is
   not build correctly:

   ```
   mvn package -Psetup-runtime -pl ccm-bundle-devel-wildfly -am 
   ```

   During the `package` phase of the this module Wildfly will be downloaded,
   unpackaged into the `target` directory, the PostgreSQL driver will be 
   deployed, a datasource will be generated and the WAR file will be deployed.

To run LibreCCM in this environment, use the `enable-runtime` profile and the `wildfly:start`
goal:

    mvn -pl ccm-bundle-devel-wildfly wildfly:start

To shutdown the server:

    mvn -pl ccm-bundle-devel-wildfly wildfly:shutdown


