#!/bin/bash

# Provides shortcuts for working with LibreCCM

wildflyversion="10.1.0.Final"
wildflypidfile="./WILDFLY_PID"

# Build the project site.
# 
# @param $1 (optional): Maven profile to use.
#
buildsite() {
    if [ -d "$1" ]; then 
        mvn clean package site site:stage -Dmaven.test.failure.ignore=true -P$1
    else 
        mvn clean package site site:stage -Dmaven.test.failure.ignore=true
    fi
}

# Build all modules
#
# @param $1 (optional): Maven profile to use.
#
build() {
    if [ -d "$1" ]; then
        mvn clean package -P$1
    else
        mvn clean package
    fi
}

# Build a module
#
# @param $1 (mandantory): The artifact ID of the module to build.
# @param $2 (optional)  : The profile to use.
#
buildmodule() {
    if [ -n "$1" ]; then
        if [ -n "$2" ]; then
            mvn clean package -P$2 -pl $1 -am
        else 
            mvn clean package -pl $1 -am
        fi
    else 
        echo "Usage: ccm.sh build-module MODULE [PROFILE]"
        exit 1
    fi
}

# Helper method for starting runtime for integration tests
# @param $1 (mandantory): Runtime to start
#
starttestruntime() {
    if [ "$1"="wildfly" ]; then
        echo "Starting Wildfly $wildversion for running tests..."
        wildflyhome=""
        if [ -n "$JBOSS_HOME" ]; then
            echo "Setting wildflyhome to JBOSS_HOME"
            wildflyhome=$JBOSS_HOME
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            echo "Setting wildflyhome..."
            wildflyhome="./runtime/wildfly-$wildflyversion"
        else 
            echo "There is not Wildfly in ./runtime/ and JBOSS_HOME is not set."
            echo "Please call install-runtime or set JBOSS_HOME"
            exit 1
        fi

        pushd $wildflyhome
        sh "LAUNCH_JBOSS_IN_BACKGROUND=1 JBOSS_PID_FILE=$wildflypidfile ./bin/standalone.sh"
        popd
        echo "Waiting 120s for Wildfly to start up..."
        sleep 120

        if [ -n "$profile" ]; then
            mvn clean test -P$profile
        else
            mvn clean test
        fi

        echo "Stopping Wildfly..."
        pushd $wildflyhome
        kill $(<"$wildflypidfile")
        popd

    elif [ "$1"="tomee" ]; then
        echo "Not implemented yet."
        exit 1
    else 
        echo "Unsupported runtime $1. Supported runtimes are wildfly and tomee."
        exit 1
    fi
    
}

# Helper method for stopping the test runtime
# 
# @param $1 (mandantory): Runtime to stop
#
stoptestruntime() {
    if [ "$1" = "wildfly" ]; then
        echo "Stopping Wildfly..."

        wildflyhome=""
        if [ -n "$JBOSS_HOME" ]; then
            echo "Setting wildflyhome to JBOSS_HOME"
            wildflyhome=$JBOSS_HOME
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            echo "Setting wildflyhome..."
            wildflyhome="./runtime/wildfly-$wildflyversion"
        else 
            echo "There is not Wildfly in ./runtime/ and JBOSS_HOME is not set."
            echo "Please call install-runtime or set JBOSS_HOME"
            exit 1
        fi

        pushd $wildflyhome
        kill $(<"$wildflypidfile")
        popd
    elif [ "$1" = "tomee" ]; then
        echo "Not implemented yet."
        exit 1
    else
        echo "Unsupported runtime $1. Supported runtime are wildfly and tomee."
        exit 1
    fi

}

# Run all tests
testall() {
    
    startruntime=false
    runtime="wildfly"
    profile=""

    if [ "$1" = "-s" ]; then
        startruntime=true
        if [ "$2" = "-r" ]; then
            runtime=$3
            profile=$4
        else
            profile=$2
        fi
    fi

    if [ $startruntime ]; then
        starttestruntime $runtime
    fi

    if [ -n "$profile" ]; then
        echo "Running tests for all modules with profile $profile..."
        mvn clean test -P$profile
    else
        echo "Running tests for all modules..."
        mvn clean test
    fi

    if [ $startruntime ]; then
        stoptestruntime $runtime
    fi
}

# Run tests for a module
testmodule() {

    startruntime=false
    runtime="wildfly"
    module=""
    profile=""

    if [ "$1" = "-r" ]; then
        startruntime=true
        if [ "$2" = "-r" ]; then
            runtime=$3
            module=$4
            profile=$5
        else
            module=$2
            profile=$3
        fi
    fi

    if [ -z "$module" ]; then
        echo "Error: No module to test. Exiting."
        exit 1
    fi

    if [ $startruntime ]; then
        starttestruntime $runtime
    fi

    if [ -n "$profile" ]; then
        echo "Running tests for module $module with profile $profile..."
        mvn clean test -P$profile -pl $module -am
    else
        echo "Running tests for module $module..."
        mvn clean test -pl $module $am
    fi

    if [ $startruntime ]; then
        stoptestruntime $runtime
    fi
}

# Run a single testsuite or test
runtest() {
    
    startruntime=false
    runtime="wildfly"
    module=""
    testtorun=""
    profile=""

    if [ "$1" = "-r" ]; then
        startruntime=true
        if [ "$2" = "-r" ]; then
            runtime=$3
            module=$4
            profile=$5
        else
            module=$2
            profile=$3
        fi
    fi

    if [ -z module ]; then
        echo "No module provided. Please provide the module which contains the test to run. Exiting."
        exit 1
    fi

    if [ -z testtorun ]; then
        echo "No test to run provided. Exiting."
        exit 1
    fi

    if [ $startruntime ]; then
        starttestruntime $runtime
    fi

    if [ -n "$profile" ]; then
        echo "Running tests for module $module with profile $profile..."
        mvn clean test -Dtest=$testtorun -DfailIfNoTests=false -P$profile -pl $module -am
    else
        echo "Runnign tests for module $module..."
        mvn clean test -D$testtorun -pl $module $am
    fi

    if [ $startruntime ]; then
        stoptestruntime $runtime
    fi
}



installruntime() {
    runtime=""
    if [ -z $1 ]; then
        runtime="wildfly"
    else 
        runtime=$1
    fi

    echo "Installing runtime $runtime..."
    if [ $runtime = wildfly ]; then

        if [ -d ./runtime/wildfly-$wildflyversion ]; then 
            echo "Wildfly $wildflyversion is already installed as runtime. Exiting"
            exit 1
        fi

        if [ ! -d ./runtime ]; then
            mkdir ./runtime
        fi

        pushd runtime 
        if [ -f wildfly-$wildflyversion.tar.gz ]; then
            echo "Wildfly $wildflyversion has already been downloaded, using existing archive."
        else 
            wget http://download.jboss.org/wildfly/$wildflyversion/wildfly-$wildflyversion.tar.gz
        fi

        if [ ! -f wildfly-$wildflyversion.tar.gz ]; then
            echo "Failed to download Wildfly."
            exit 1
        fi

        tar vxzf wildfly-$wildflyversion.tar.gz
        echo ""
        echo "Wildfly extracted successfully. Please provide a username and password for a Wildfly management user (admin):" 
        echo ""
        username=""
        while [ -z $username ]; do
            echo -n "Username.......: "
            read username
            if [ -z $username ]; then
                echo "Username can't be empty!"
            fi
        done
        password=""
        passwordrepeat=""
        while [ -z $password -o $password != $passwordrepeat ]; do
            echo -n "Password.......: "
            read -s password
            if [ -z $password ]; then
                echo ""
                echo "Password can't be empty!"
                continue
            fi
            echo ""
            echo -n "Repeat password: "
            read -s passwordrepeat
            if [ $password != $passwordrepeat ]; then
                echo ""
                echo "Passwords do not match."
                continue
            fi
        done
        echo ""
        echo "Creating Wildfly management user $username..."
        pushd wildfly-$wildflyversion
        sh ./bin/add-user.sh $username $password
        popd
        popd

        echo ""
        echo "Wildfly $wildflyversion successfully installed in ./runtime."
        echo "Before running LibreCCM you have to configure a datasource."
        echo "To do that run"
        echo ""
        echo "ccm.sh run --with-runtime wildfly "
        echo ""
        echo "Then open a browser and go to "
        echo ""
        echo "localhost:9990"
        echo ""
        echo "and configure a datasource. Refer to the Wildfly documentation for more details."

    elif [ $runtime = tomee ]; then
        echo "Not implememented yet."
    else 
        echo "Unsupported runtime. Supported runtimes are wildfly (default) and tomee."
    fi

}

run() {
    runtime=""
    bundle=""

    if [ "$1" = "-r" ]; then
        runtime="$2"
        bundle="$3"
    elif [ "$1" = "--with-runtime" ]; then
        runtime="$2"
        bundle="$3"
    else 
        runtime="wildfly"
        bundle="$2"
    fi

    if [ -z $bundle ]; then
        echo "Running Wilfly $wildversion without a bundle (only starting Wilflybut not deploying LibreCCM)..."
    else 
        echo "Running bundle $bundle with Wildfly $wildflyversion..."
    fi


    if [ $runtime = "wildfly" ]; then
        wildflyhome=""
        if [ -n "$JBOSS_HOME" ]; then
            echo "Setting wildflyhome to JBOSS_HOME"
            wildflyhome=$JBOSS_HOME
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            echo "Setting wildflyhome..."
            wildflyhome="./runtime/wildfly-$wildflyversion"
        else 
            echo "There is not Wildfly in ./runtime/ and JBOSS_HOME is not set."
            echo "Please call install-runtime or set JBOSS_HOME"
            exit 1
        fi

        echo "Starting Wildfly in $wildflyhome..."
        if [ -z $bundle ]; then
            pushd $wildflyhome
            sh "./bin/standalone.sh"
            popd
        else 
            mvn -Djboss-as.home=${wildflyhome} package wildfly:run -DskipTests -pl $bundle -am -Pgeneric
        fi

    elif [ $runtime = "tomee" ]; then
        echo "Not implemented yet"
        exit 0
    else 
        echo "Unknown runtime $runtime. Supported runtimes are: wildfly tomee"
        exit 1
    fi
}

stopruntime() {
    runtime=""
    if [ -n "$1" ]; then
        runtime=$1
    else
        runtime="wildfly"
    fi

    if [ runtime = "wildfly"]; then
        wildflyhome=""
        if [ -n $JBOSS_HOME ]; then
            wildflyhome="$JBOSS_HOME"
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            wildflyhome="./runtime/wildfly-$wildflyversion"
        else
            echo "There is no Wildfly in ./runtime/ and JBOSS_HOME is not set."
            echo "Exiting."
            exit 1
        fi

        bin/standalone.sh
    elif [ $runtime = "tomee"]; then
        echo "Not implemented yet."
        exit 0
    else
        echo "Unknown runtime."
        exit 1
    fi
}

showhelp() {
    echo "ccm.sh is a helper script for building and running LibreCCM in a 
    development environment. It provides shortcuts for several Maven goals. The
    available subcommands are:
    
    build-site [PROFILE]                            : Builds the Maven project 
                                                      site. 
    build [PROFILE]                                 : Build all LibreCCM 
                                                      modules. 
    build-module MODULE [PROFILE]                   : Build a specific LibreCCM 
                                                      module.
    test-all [-s [-r RUNTIME]] [PROFILE]            : Run tests for all modules.
    test-module [-s [-r RUNTIME]] MODULE [PROFILE]  : Run tests for a specific 
                                                      LibreCCM module.
    run-test [-s [-r RUNTIME]] MODULE TEST [PROFILE]: Run a specific testsuite 
                                                      or a single test. 
    install-runtime [RUNTIME]                       : Download and install a 
                                                      runtime  (application 
                                                      server) into ./runtime
    run [-r RUNTIME] [BUNDLE]                       : Run a runtime 
                                                      (application server)
    help                                            : Show this help message.

    A detailed description of the subcommands is provided in ccm-readme.txt"
    

    
    exit 0;
}

case $1 in
    build-site)      buildsite $2 ;;
    build)           build $2 ;;
    build-module)    buildmodule $2 ;;
    test-all)        testall $2 $3 $4 ;;
    test-module)     testmodule $2 $3 $4 $5 ;;
    run-test)        runtest $2 $3 $4 $5 $6 ;;
    install-runtime) installruntime $2 ;;
    run)             run $2 $3 $4 ;;
    stop-runtime)    stopruntime $2 ;;
    help)            showhelp ;;
    *)               showhelp ;;

esac


