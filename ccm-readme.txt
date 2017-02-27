ccm.sh is a helper script for building and running LibreCCM in a development 
environment. It provides shortcuts for several Maven goals. The available 
subcommands are:
    
build-site [PROFILE]                            : Builds the Maven project site.
build [PROFILE]                                 : Builds LibreCCM using the 
                                                  provided profile
build-module MODULE [PROFILE]                   : Build a specific LibreCCM 
                                                  module.
test-all [-s [-r RUNTIME]] [PROFILE]            : Run tests for all modules.
test-module [-s [-r RUNTIME]] MODULE [PROFILE]  : Run tests for a specific 
                                                  LibreCCM module.
run-test [-s [-r RUNTIME]] MODULE TEST [PROFILE]: Run a specific testsuite or
                                                  a single test.
install-runtime [RUNTIME]                       : Download and install a 
                                                  runtime (application server) 
                                                  into ./runtime.
run [-r RUNTIME] [BUNDLE]                       : Run a runtime 
                                                  (application server)
help                                            : Show this help message.

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

    mvn clean package -pl $MODULE -am 

without a profile and to

    mvn clean package -P$PROFILE -pl $MODULE -am

with a profile.

test-all [-s [-r RUNTIME]] [PROFILE]
====================================

Run all tests for all modules. If the -s flag is set a application server is
started in the background before the tests are run and stopped afterwards.
If no runtime is provided using -r RUNTIME Wildfly is used as runtime. See
the section "run" for Wildfly specific details.

If a profile is provided the profile is used to run the tests.

test-module [-s [-r RUNTIME]] MODULE [PROFILE]
==============================================

Runs the tests for a specific module. If the -s flag is set a application 
server is started in the background before the tests are run and stopped 
afterwards. If no runtime is provided using -r RUNTIME Wildfly is used as 
runtime. See the section "run" for Wildfly specific details.

This subcommand has one mandatory parameter: The module which tests are run.
Optionally a profile can be provided.

run-test [-s [-r RUNTIME]] MODULE TEST [PROFILE]
================================================

Runs the a specific testsuite or a single test. 
If the -s flag is set a application 
server is started in the background before the tests are run and stopped 
afterwards. If no runtime is provided using -r RUNTIME Wildfly is used as 
runtime. See the section "run" for Wildfly specific details.

This subcommand has two mandatory parameter: 
The module which contains the test(suite) to run and the test to run. The test
to run must be provided in the following syntax:

fully.qualified.classname.of.testclass[#test-method]

Optionally are profile can be provided. 

Some examples:

Run all tests in the class org.libreccm.configuration.EqualsAndHashCodeTest in 
the ccm-core module:

ccm.sh runtest ccm-core org.libreccm.configuration.EqualsAndHashCodeTest

Run all tests in the class org.libreccm.core.CcmObjectRepositoryTest in the
ccm-core module. This tests is run using Arquillian and requires a runtime and
is only run when an integration test profile is used:

ccm.sh runtest -s ccm-core org.libreccm.core.CcmObjectRepositoryTest
wildfly-remote-h2-mem

Or if you only want to run the saveChangedCcmObject test:

ccm.sh runtest -s ccm-core
org.libreccm.core.CcmObjectRepositoryTest#saveChangedCcmObject wildfly-remote-h2-mem

The last two examples used the default runtime (Wildfly). Invoking without a
specific runtime is equivalent to 

ccm.sh runtest -s -r wildfly ccm-core org.libreccm.core.CcmObjectRepositoryTest wildfly-remote-h2-mem

ccm.sh runtest -s -r wildfly ccm-core org.libreccm.core.CcmObjectRepositoryTest#saveChangedCcmObject wildfly-remote-h2-mem






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


Wildfly specific details
------------------------

If the JBOSS_HOME environment variable is set the script will use the server
in the directory provided by JBOSS_HOME. You may provide a value for this
variable when calling the script:

    JBOSS_HOME=/path/to/wildfly ./ccm.sh run [wildfly]

If JBOSS_HOME is not set the script will look for a Wildfly installation in
./runtime. The install-runtime subcommand can be used to install Wildfly
there. 

The datasource configuration is done using Wildfly's administration UI. To
create a datasource open a browser and go to

    localhost:9990

You will be asked for the username and password of a management user. Then go
to 

    Configuration/Subsystems/Datasources/Non-XA

and a datasource for the bundle you want to use.

Some JNDI-Datasource-URLs:

ccm-bundle-devel-wildfly    java:/comp/env/jdbc/libreccm/db



