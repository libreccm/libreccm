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
        LAUNCH_JBOSS_IN_BACKGROUND=1 JBOSS_PIDFILE=$wildflypidfile ./bin/standalone.sh &
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
            echo "There is no Wildfly in ./runtime/ and JBOSS_HOME is not set."
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
    echo "Running all tests for all modules..."
    if [[ $1 =~ ^wildfly-managed.* ]]; then
        echo "...using a managed Wildfly container"
        wildflyhome=""
        if [ -n "$JBOSS_HOME" ]; then
            echo "...JBOSS_HOME environment variable is set. Using Wildfly installation at $JBOSS_HOME."
            wildflyhome=$JBOSS_HOME
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            echo "...using Wildfly installation in runtime directory"
            pushd $wildflyhome
            wildflyhome=`pwd`
            popd
        else 
            echo -e "\e[41mThere is no Wildfly-$wildflyversion in ./runtime nore is JBOSS_HOME set. Please install Wildfly-$wildflyversion into runtime by calling install-runtime or set the JBOSS_HOME environment variable to a valid location.\e[0m"
            exit 1
        fi
       
        echo "...using profile $1"
        echo ""
        if [ -n $STARTUP_TIMEOUT ]; then
            mvn clean test -Djboss.home=$wildflyhome -DstartuptimeoutInSeconds=$STARTUP_TIMEOUT -P$1
        else 
            mvn clean test -Djboss.home=$wildflyhome -P$1
        fi

    elif [[ $1 =~ ^wildfly-remote.* ]]; then
        echo "...using a remote Wildfly container"
        if [[ $2 == "start" ]]; then
            echo "...starting runtime"
            starttestruntime wildfly
        else 
            echo "...runtime is started manually"
        fi

        echo "...using profile $1"
        mvn clean test -P$1

        if [[ $2 == "start" ]]; then
            echo "...stopping runtime"
        fi
   else
        if [[ -n $1 ]]; then
            echo -n -e "\e[43m"
            echo "Warning:                                                 "
            echo "The provided profile starts with an unknown prefix. Tests" 
            echo "which require a running application server may fail.     "
            echo -n -e "\e[0m"
            
        fi

        mvn clean test
    fi
}

# Run tests for a module
testmodule() {

    if [[ -z $1 ]]; then
        echo -e "\e[41mUsage: ccm.sh test-module MODULE [PROFILE] [start]\e[0m"
        exit 1
    fi

    echo "Running tests for module $1..."
    if [[ $2 =~ ^wildfly-managed.* ]]; then
        echo "...using a managed Wildfly container."
        wildflyhome=""
        if [ -n "$JBOSS_HOME" ]; then
            echo "...JBOSS_HOME enviromentment variable is set Using Wildfly installation at $JBOSS_HOME."
            wildflyhome=$JBOSS_HOME
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            echo "...using Wildfly installation in runtime-directory"
            pushd $wildflyhome
            wildflyhome=`pwd`
            popd
        else
            echo -e "\e[41mThere is no Wildfly-$wildfly-version in ./runtime nore is JBOSS_HOME set. Please install Wildfly-$wildfly-version into ./runtime by calling install-runtime or set the JBOSS_HOME environment variable to valid location.\e[0m"
            exit 1
        fi

        echo "...using profile $2"
        echo ""
        if [ -n $STARTUP_TIMEOUT ]; then
            mvn clean test -Djboss.home=$wildflyhome -DstartupTimeoutInSeconds=$STARTUP_TIMEOUT -pl $1 -am -P$1
        else
            mvn clean test -Djboss.home=$wildflyhome -pl $1 -am -P$2
        fi
    elif [[ $2 =~ ^wildfly-remote.* ]]; then
        echo "Using a remote Wildfly container..."
        if [[ $3 == "start" ]]; then
            echo "...starting runtime"
            starttestruntime wildfly
        else
            echo "...runtime is started manually"
        fi

        echo "...using profile $2"
        mvn clean test -pl $1 -am -P$2

        if [[ $3 == "start" ]]; then
            echo "...stopping runtime"
        fi
    else
        if [[ -n $2 ]]; then
            echo -n -e "\e[43m"
            echo "Warning:                                                 "
            echo "The provided profile starts with an unknown prefix. Tests" 
            echo "which require a running application server may fail.     "
            echo -n -e "\e[0m"
        fi

        mvn clean test -pl $1 -am
    fi
}

# Run a single testsuite or test
runtest() {

    if [ -z "$1" -o -z "$2" ]; then
        echo -e "\e[41mUsage: ccm.sh test-module MODULE TEST [PROFILE] [start]\e[0m"
        exit 1
    fi

    echo "Running test $2 from module $1..."
    if [[ $3 =~ ^wildfly-managed.* ]]; then
        echo "...using a managed Wildfly container."
        wildflyhome=""
        if [ -n "$JBOSS_HOME" ]; then
            echo "...JBOSS_HOME environment variable is set. Using Wildfly installation at $JBOSS_HOME."
            wildflyhome=$JBOSS_HOME            
        elif [ -d "./runtime/wildfly-$wildflyversion" ]; then
            echo "...using Wildfly installation in runtime directory"
            pushd ./runtime/wildfly-$wildflyversion
            wildflyhome=`pwd`
            popd           
        else
            echo -e "\e[41mThere is no Wildfly-$wildfly-version in ./runtime nore is JBOSS_HOME set. Please install Wildfly-$wildfly-version into ./runtime by calling install-runtime or set the JBOSS_HOME environment variable to valid location.\e[0m"
            exit 1
        fi        

        echo "...using Wildfly in $wildflyhome"
        echo "...using profile $3"
        echo ""
        if [ -n $STARTUP_TIMEOUT ]; then
            mvn clean test -Djboss.home=$wildflyhome -DstartupTimeoutInSeconds=$STARTUP_TIMEOUT -Dtest=$2 -DfailIfNoTests=false -pl $1 -am -P$3
        else
            mvn clean test -Djboss.home=$wildflyhome -Dtest=$2 -DfailIfNoTests=false -pl $1 -am -P$3
        fi
    elif [[ $3 =~ ^wildfly-remote.* ]]; then
        echo "Using a remote Wildfly container..."
        if [[ $4 == "start" ]]; then
            echo "...starting runtime"
            starttestruntime wildfly
        else
            echo "...runtime is started manually"
        fi

        mvn clean test -Dtest=$2 -DfailIfNoTests=false -pl $1 -am -P$3

        if [[ $4 == "start" ]]; then
            echo "...stopping runtime"
            stoptestruntime wildfly
        fi
    else
        if [[ -n $3 ]]; then
            echo -n -e "\e[43m"
            echo "Warning:                                                 "
            echo "The provided profile starts with an unknown prefix. Tests" 
            echo "which require a running application server may fail.     "
            echo -n -e "\e[0m"            
        fi

        mvn clean test -Dtest=$2 -DfailIfNoTests=false -pl $1 -am
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
        bundle="$1"
    fi

    if [ -z $bundle ]; then
        echo "Running Wilfly $wildversion without a bundle (only starting Wilfly but not deploying LibreCCM)..."
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
development environment. It provides shortcuts for several Maven goals. The available subcommands are:
    
    build-site [PROFILE]                    : Builds the Maven project site. 
    build [PROFILE]                         : Build all LibreCCM modules. 
    build-module MODULE [PROFILE]           : Build a specific LibreCCM module.
    test-all [[PROFILE] [start]]            : Run all tests for all modules.
    test-module MODULE [[PROFILE] [start]]  : Run all tests for a specific 
                                              LibreCCM module.
    run-test MODULE TEST [[PROFILE] [start]]: Run a specific testsuite or a 
                                              single test method. 
    install-runtime [RUNTIME]               : Download and install a runtime 
                                              (application server) into 
                                              ./runtime
    run [-r RUNTIME] [BUNDLE]               : Run a runtime (application server)
    help                                    : Show this help message.

    A detailed description of the subcommands is provided in ccm-readme.txt"
    

    
    exit 0;
}

case $1 in
    build-site)      buildsite $2 ;;
    build)           build $2 ;;
    build-module)    buildmodule $2 ;;
    test-all)        testall $2 $3 $4 ;;
    test-module)     testmodule $2 $3 $4 $5 ;;
    test)            runtest $2 $3 $4 $5 ;;
    install-runtime) installruntime $2 ;;
    run)             run $2 $3 $4 ;;
    stop-runtime)    stopruntime $2 ;;
    help)            showhelp ;;
    *)               showhelp ;;

esac


