#!/bin/bash

# Provides shortcuts for working with LibreCCM

wildfly_version="10.1.0.Final"
wildfly_pid_file="./WILDFLY_PID"
wildfly_home=""

# Helper function for finding Wildfly. First the function checks if the 
# environment variable JBOSS_HOME is set. If JBOSS_HOME is set is make sure
# that JBOSS_HOME points to a valid Wildfly installation. If JBOSS_HOME is not
# set the function checks if Wildfly is installed in ./runtime. If there is no
# Wildfly in runtime the function exits the script.
find_wildfly_home() {
    #local wildfly_home=""
    if [[ -n $JBOSS_HOME ]]; then
        echo "JBOSS_HOME = $JBOSS_HOME"
        if [ -f "$JBOSS_HOME/bin/standalone.sh" ]; then           
            echo $JBOSS_HOME
        else 
            echo -e "\e[41mJBOSS_HOME is set but there is no $JBOSS_HOME/bin/standalone.sh. Please make sure that JBOSS_HOME points to a valid Wildfly installation.\e[0m"
            exit 1
        fi
    elif [[ -d "./runtime/wildfly-$wildfly_version" ]]; then
        wildfly_home="./runtime/wildfly-$wildfly_version"
        echo "wildfly_home is '$wildfly_home'"
        pushd $wildfly_home
        wildfly_home=$(pwd)
        echo "wildfly_home is '$wildfly_home'"
        popd
        #echo $wildfly_home        
    else
        echo -e "\e[41mThere is no Wildfly in ./runtime/ neither is JBOSS_HOME set. Please install Wildfly $wildfly_version into ./runtime/ using install-runtime or set the JBOSS_HOME enviroment variable.\e[0m"
        exit 1
    fi
}

# Build the project site.
# 
# @param $1 (optional): Maven profile to use.
#
build_site() {
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
build_module() {
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
start_test_runtime() {
    if [ "$1"="wildfly" ]; then
        echo "Starting Wildfly running tests..."
        #local wildfly_home=$(find_wildfly_home)

        find_wildfly_home
        #pushd $(find_wildfly_home)
        pushd $wildfly_home
        LAUNCH_JBOSS_IN_BACKGROUND=1 JBOSS_PIDFILE=$wildfly_pid_file ./bin/standalone.sh &
        popd
        echo "Waiting 120s for Wildfly to start up..."
        sleep 120

        if [ -n "$profile" ]; then
            mvn clean test -P$profile
        else
            mvn clean test
        fi

        echo "Stopping Wildfly..."
        pushd $wildfly_home
        kill $(<"$wildfly_pid_file")
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
stop_test_runtime() {
    if [ "$1" = "wildfly" ]; then
        echo "Stopping Wildfly..."
        find_wildfly_home
        #local wildfly_home=$(find_wildfly_home)

        pushd $wildfly_home
        kill $(<"$wildfly_pid_file")
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
test_all() {
    echo "Running all tests for all modules..."
    if [[ $1 =~ ^wildfly-managed.* ]]; then
        echo "...using a managed Wildfly container"
        find_wildfly_home
        #local wildfly_home=$(find_wildfly_home)
      
        echo "...using profile $1"
        echo ""
        if [ -n $STARTUP_TIMEOUT ]; then
            mvn clean test -Djboss.home=$wildfly_home -DstartuptimeoutInSeconds=$STARTUP_TIMEOUT -P$1
        else 
            mvn clean test -Djboss.home=$wildfly_home -P$1
        fi

    elif [[ $1 =~ ^wildfly-remote.* ]]; then
        echo "...using a remote Wildfly container"
        if [[ $2 == "start" ]]; then
            echo "...starting runtime"
            start_test_runtime wildfly
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
test_module() {

    if [[ -z $1 ]]; then
        echo -e "\e[41mUsage: ccm.sh test-module MODULE [PROFILE] [start]\e[0m"
        exit 1
    fi

    echo "Running tests for module $1..."
    if [[ $2 =~ ^wildfly-managed.* ]]; then
        echo "...using a managed Wildfly container."
        find_wildfly_home
        #local wildfly_home=$(find_wildfly_home)
        echo "...using profile $2"
        echo ""
        if [ -n $STARTUP_TIMEOUT ]; then
            mvn clean test -Djboss.home=$wildfly_home -DstartupTimeoutInSeconds=$STARTUP_TIMEOUT -pl $1 -am -P$1
        else
            mvn clean test -Djboss.home=$wildfly_home -pl $1 -am -P$2
        fi
    elif [[ $2 =~ ^wildfly-remote.* ]]; then
        echo "Using a remote Wildfly container..."
        if [[ $3 == "start" ]]; then
            echo "...starting runtime"
            start_test_runtime wildfly
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
run_test() {

    if [ -z "$1" -o -z "$2" ]; then
        echo -e "\e[41mUsage: ccm.sh test-module MODULE TEST [PROFILE] [start]\e[0m"
        exit 1
    fi

    echo "Running test $2 from module $1..."
    if [[ $3 =~ ^wildfly-managed.* ]]; then
        echo "...using a managed Wildfly container."
        find_wildfly_home
        #local wildfly_home=$(find_wildfly_home)

        echo "...using Wildfly in $wildfly_home"
        echo "...using profile $3"
        echo ""
        if [ -n $STARTUP_TIMEOUT ]; then
            mvn clean test -Djboss.home=$wildfly_home -DstartupTimeoutInSeconds=$STARTUP_TIMEOUT -Dtest=$2 -DfailIfNoTests=false -pl $1 -am -P$3
        else
            mvn clean test -Djboss.home=$wildfly_home -Dtest=$2 -DfailIfNoTests=false -pl $1 -am -P$3
        fi
    elif [[ $3 =~ ^wildfly-remote.* ]]; then
        echo "Using a remote Wildfly container..."
        if [[ $4 == "start" ]]; then
            echo "...starting runtime"
            start_test_runtime wildfly
        else
            echo "...runtime is started manually"
        fi

        mvn clean test -Dtest=$2 -DfailIfNoTests=false -pl $1 -am -P$3

        if [[ $4 == "start" ]]; then
            echo "...stopping runtime"
            stop_test_runtime wildfly
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



install_runtime() {
    local runtime=""
    if [ -z $1 ]; then
        runtime="wildfly"
    else 
        runtime=$1
    fi

    echo "Installing runtime $runtime..."
    if [ $runtime = wildfly ]; then

        if [ -d ./runtime/wildfly-$wildfly_version ]; then 
            echo "Wildfly $wildfly_version is already installed as runtime. Exiting"
            exit 1
        fi

        if [ ! -d ./runtime ]; then
            mkdir ./runtime
        fi

        pushd runtime 
        if [ -f wildfly-$wildfly_version.tar.gz ]; then
            echo "Wildfly $wildfly_version has already been downloaded, using existing archive."
        else 
            wget http://download.jboss.org/wildfly/$wildfly_version/wildfly-$wildfly_version.tar.gz
        fi

        if [ ! -f wildfly-$wildfly_version.tar.gz ]; then
            echo "Failed to download Wildfly."
            exit 1
        fi

        tar vxzf wildfly-$wildfly_version.tar.gz
        echo ""
        echo "Wildfly extracted successfully. Please provide a username and password for a Wildfly management user (admin):" 
        echo ""
        local username=""
        while [ -z $username ]; do
            echo -n "Username.......: "
            read username
            if [ -z $username ]; then
                echo "Username can't be empty!"
            fi
        done
        local password=""
        local passwordrepeat=""
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
        pushd wildfly-$wildfly_version
        sh ./bin/add-user.sh $username $password
        popd
        popd

        echo ""
        echo "Wildfly $wildfly_version successfully installed in ./runtime."
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
    local runtime=""
    local runtime_only=false
    local bundle=""

    if [ "$1" = "-r" ]; then
        runtime="$2"
        runtime_only=true
    else
        runtime_only=false
        bundle=$1
    fi

    if [ $runtime_only == true ]; then
        echo "Starting application server without deploying a bundle..."
        if [[ -z "$runtime" ]]; then
            echo "No application server set, using default (Wildfly)..."
            runtime="wildfly"
        fi

        if [ $runtime="wildfly" ]; then
            find_wildfly_home
            # local wildfly_home=$(find_wildfly_home)

            pushd $wildfly_home
            sh ./bin/standalone.sh
            popd
        elif [ $runtime="tomee" ]; then
            echo "Not implemented yet."
            exit 0
        else
            echo -e "\e[41mUnknown runtime $runtime. Please specify a supported runtime.\e[0m"
            exit 1
        fi
    else
        if [[ -z $bundle ]]; then
            echo "No bundle specificed. Please specifiy a bundle."
            echo "Usage: ccm.sh run BUNDLE"
            exit 1
        fi

        echo "Running bundle $bundle..."
        if [[ $bundle=~^.*-wildfly$ ]]; then
            find_wildfly_home
            # local wildfly_home=$(find_wildfly_home)
            echo "Using Wildfly in $wildfly_home..."
            mvn package wildfly:run -Djboss-as.home=${wildfly_home} -DskipTests -pl $bundle -am -Pgeneric
       elif [[ $bundle=~^.*-wildfly-swarm$ ]]; then
            echo "Not implemented yet"
            exit 0
        elif [[ $bundle=~^.*-tomee$ ]]; then
            echo "Not implemented yet"
            exit 0
       else
            echo -e "\e[41mThe bundle '$bundle' has an unknown suffix. Are you sure that you specified a valid bundle?\e[0m"
        fi
    fi
}

stop_runtime() {
    local runtime=""
    if [ -n "$1" ]; then
        runtime=$1
    else
        runtime="wildfly"
    fi

    if [ runtime = "wildfly"]; then
        echo "Nothing to do, the Wildfly Maven plugins automatically stops Wildfly."
    elif [ $runtime = "tomee"]; then
        echo "Not implemented yet."
        exit 0
    else
        echo "Unknown runtime."
        exit 1
    fi
}

show_help() {
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
    build-site)      build_site $2 ;;
    build)           build $2 ;;
    build-module)    build_module $2 ;;
    test-all)        test_all $2 $3 $4 ;;
    test-module)     test_module $2 $3 $4 $5 ;;
    run-test)        run_test $2 $3 $4 $5 ;;
    install-runtime) install_runtime $2 ;;
    run)             run $2 $3 $4 ;;
    stop-runtime)    stop_runtime $2 ;;
    help)            show_help ;;
    *)               show_help ;;

esac


