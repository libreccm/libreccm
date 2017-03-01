ccm.sh is a helper script for building and running LibreCCM in a development 
environment. It provides shortcuts for several Maven goals. The available 
subcommands are:
    
build-site [PROFILE]                  : Builds the Maven project site.
build [PROFILE]                       : Builds LibreCCM using the provided 
                                        profile
build-module MODULE [PROFILE]         : Build a specific LibreCCM module.
test-all [[PROFILE] [start]]          : Run tests for all modules.
test-module MODULE [[PROFILE] [start]]: Run tests for a specific LibreCCM 
                                        module.
test MODULE TEST [[PROFILE] [start]]  : Run a specific testsuite or a single 
                                        test method.
install-runtime [RUNTIME]             : Download and install a runtime 
                                        (application server) into ./runtime.
run [-r RUNTIME] [BUNDLE]             : Run a runtime (application server)
help                                  : Show this help message.

build-site [PROFILE]
====================

Builds the project site. If a profile is provided it is passed to Maven. With a
profile this subcommand is equivalent to the Maven command: 

    mvn clean package site site:stage -Dmaven.test.failure.ignore=true
    -P$PROFILE
    
Otherwise it equivalent to
    
    mvn clean package site site:state -Dmaven.test.failure.ignore=true
    
build [PROFILE]
===============
    
Builds to complete project, optionally using a Maven profile. Without a
profile this is equivalent to 

    mvn clean package

With a profile it is equivalent to

    mvn clean package -P$PROFILE

build-module MODULE [PROFILE]
=============================

Build a specific LibreCCM module. Equivalent to 

    mvn clean package -pl MODULE -am 

without a profile and to

    mvn clean package -PPROFILE -pl MODULE -am

with a profile.

test-all [[PROFILE] [start]]
============================

Run all tests for all modules. The name of the (optional) profile is used to 
determine if a runtime (application server) is started and which application 
server is started. Some profiles use Arquillian container adapters which don't
require a running application server. Others requires a running a application
server. For those profiles the application server deduced from the profile
name is started if the last parameter is "start". Otherwise the application
server must be started manully before running the tests. Please note that the
use of the "start" parameter is not recommanded because is may causes
problems. It is recommanded to either start the application server manually or
to use a profile which uses an Arquillian container adapter which starts the 
application server itself.

This subcommand is equivalent to the Maven command 

    mvn clean test -PPROFILE

with a profile and to 

    mvn clean test 
    
without a profile.

test-module MODULE [[PROFILE] [start]]
======================================

Runs the tests for a specific module. The name of the module is a mandatory
parameter.

The name of the (optional) profile is
used to to determine if a runtime (application server) is started and
application server is started. Some profiles use Arquillian container adapters
which dont't require a running application server. Others require a running
application server. For those profiles the application is deduced from the
profile is started if the the last parameter is "start". Otherwise the
application server must be started manually before running the tests. Please
note that the use of the "start" parameter is not recommanded. It is
recommanded to either start the application server manually or to use a
profile which uses a container adapter which starts the application server
itself.
 
This subcommand is equivalent to the Maven command
    
    mvn clean test -pl MODULE -am -PPROFILE

with a profile and to

    mvn clean test -pl MODULE -am

without a profile.

test MODULE TEST [[PROFILE] [start]]
====================================

Runs the a specific testsuite or a single test. Module and test are mandatory
parameters. The testsuite or test to run is set using the following syntax:

fully.qualified.classname.of.testclass[#test-method]

The name of the (optional) profile is
used to to determine if a runtime (application server) is started and
application server is started. Some profiles use Arquillian container adapters
which dont't require a running application server. Others require a running
application server. For those profiles the application is deduced from the
profile is started if the the last parameter is "start". Otherwise the
application server must be started manually before running the tests. Please
note that the use of the "start" parameter is not recommanded. It is
recommanded to either start the application server manually or to use a
profile which uses a container adapter which starts the application server
itself.
 
This subcommand is equivalent to the Maven command

    mvn clean test -Dtest=TEST -DfailIfNoTests=false -pl MODULE -am -PPROFILE

with a profile and to 

    mvn clean test -Dtest=TEST -DfailIfNoTests=false -pl MODULE -am

without a profile.

Some examples:

Run all tests in the class org.libreccm.configuration.EqualsAndHashCodeTest in 
the ccm-core module:

ccm.sh test ccm-core org.libreccm.configuration.EqualsAndHashCodeTest

Run all tests in the class org.libreccm.core.CcmObjectRepositoryTest in the
ccm-core module. These tests is run using Arquillian and require a runtime and
is only run when an integration test profile is used. The application server
must be started before the test are invoked.

ccm.sh test ccm-core org.libreccm.core.CcmObjectRepositoryTest wildfly-remote-h2-mem

This example runs the same tests as the previous example but uses a managed
profile. The application server will started by Arquillian automatically:

ccm.sh test ccm-core org.libreccm.core.CcmObjectRepositoryTest
wildfly-managed-h2-mem

Or if you only want to run the saveChangedCcmObject test:

ccm.sh test ccm-core
org.libreccm.core.CcmObjectRepositoryTest#saveChangedCcmObject wildfly-managed-h2-mem


install-runtime [RUNTIME]
=========================

Install an runtime (application server) into ./runtime. At the moment only
Wildfly is supported. 

./ccm.sh install-runtime

or

./ccm.sh install-runtime wildfly

will 

a) Create the directory runtime in the current working directory (if it not
   exists already)

b) Download the current version of the Wildfly Application Server
   (10.1.0.Final) in ./runtime

c) Extract Wildfly 

d) Ask for the username and password for a management user for Wildfly (see
   the Wildfly documentation for details on that).

run [-r RUNTIME | --with-runtime RUNTIME] [BUNDLE]
==================================================

Starts an application server (at the moment only Wildfly is supported). If a
bundle is provided the bundle is build (skiping all JUnit tests) and deployed
to the application server. If no bundle is provided the application server is
only started. This is useful for configuring the application server. 

If no runtime is provided using -r or --with-runtime Wildfly is used as
default.

IMPORTANT: You must configure a datasource with the JNDI-URL required by the
bundle you want to run before running LibreCCM. For Wildfly this can be done
using Wildfly's administration UI available at 

http://localhost:9990


Supported iApplication server
=============================

Wildfly
-------

If the JBOSS_HOME environment variable is set the script will use the server
in the directory provided by JBOSS_HOME for starting Wildfly. 
You may provide a value for this variable when calling the script:

    JBOSS_HOME=/path/to/wildfly ./ccm.sh run [wildfly]

For profiles using the wildfly-arquillian-container-remote adapter Wildfly
must be started manually. The subcommands for running test have an optional
parameter for starting Wildfly automatically if a remote profile is used, but
this is not recommanded. If a profile is used which uses
wildfly-arquillian-container-managed Arquillian starts the container
automatically.

If JBOSS_HOME is not set the script will look for a Wildfly installation in
./runtime. The install-runtime subcommand can be used to install Wildfly
there. The script passes this parameter to Maven in the required form.

The datasource configuration is done using Wildfly's administration UI. To
create a datasource open a browser and go to

    localhost:9990

You will be asked for the username and password of a management user. Then go
to 

    Configuration/Subsystems/Datasources/Non-XA

and a datasource for the bundle you want to use.

Some JNDI-Datasource-URLs:

ccm-bundle-devel-wildfly    java:/comp/env/jdbc/libreccm/db



