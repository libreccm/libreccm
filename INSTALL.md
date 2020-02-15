# LibreCCM/LibreCMS Installation

## WildFly 

### Preparation (optional)

These steps are optional and can be skipped if you have already a WildFly server 
up and running or if you already installed the PostgreSQL JDBC driver in your
WildFly instance.

#### WildFly installation

Download WildFly and extract the archive. For more informations about setting
wildfly please refer to the WildFly documentation

#### Install the PostgreSQL JDBC driver

1. Download the PostgreSQL JDBC driver from 
   https://jdbc.postgresql.org/download.html
2. Go to the home directory of your WildFly installation and to 
   `modules/system/layers/base`
3. Create a new directory `org/postgresql/main`
4. Create a `module.xml` file in the `org/postgresql/main` directory with the 
   following content:

    ```
    <?xml version="1.0" encoding="UTF-8"?>
        <module xmlns="urn:jboss:module:1.1" name="org.postgresql">
            <resources>
                <resource-root path="postgresql-42.2.10.jar"/>
            </resources>
        <dependencies>
            <module name="javax.api"/>
            <module name="javax.transaction.api"/>
        </dependencies>
    </module>
    ```

    Change the name of the JAR file to the correct name!

5. Start the JBOSS CLI tool: `bin/jboss-cli.sh` or `bin/jboss-cli.bat`. Enable
   the new module:

   ```
   [standalone@localhost:9990 /] /subsystem=datasources/jdbc-driver=postgresql:add(
    driver-name=postgresql,
    driver-module-name=org.postgresql,
    driver-class-name=org.postgresql.Driver
   )
   ```
   
   Note: The above command can be in one line.

### Installing LibreCCM/LibreCMS

#### Database 

1. Create a new database user 
2. Create a new database in your PostgreSQL server owned by the user created 
   in the previous step.

#### Create a datasource

1. Start the JBOSS CLI tool: `bin/jboss-cli.sh` or `bin/jboss-cli.bat`.
2. Add new datasource: 
   ```
   [standalone@localhost:9990 /] data-source add --name=librecms --driver-name=postgresql --jndi-name=java:/comp/env/jdbc/scientificcms/db --connection-url=jdbc:postgresql://localhost:5432/librecm --user-name=libreccm --password=libreccm
   ```

   Replace the name of the datasource, the connection URL,
   the user name and the password with the correct values for your environment.


#### Deploy LibreCCM/LibreCMS

Simpley copy the the WAR file from one of the bundle modules to to the
directory `standalone/deployments` of your WildFly installation.

## Thorntail

ToDo

## TomEE 

ToDo
