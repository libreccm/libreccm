#!/bin/bash

wildflyversion="10.1.0.Final"

buildsite() {
    if [ -d "$1" ]; then 
        mvn clean package site site:stage -Dmaven.test.failure.ignore=true -P$2
    else 
        mvn clean package site site:stage -Dmaven.test.failure.ignore=true
    fi
}

build() {
    if [ -d "$1" ]; then
        mvn clean package -P$1
    else
        mvn clean package
    fi
}

buildmodule() {
    if [ -n "$1" ]; then
        if [ -n "$2" ]; then
            mvn clean package -P$2 -pl $1 -am
        else 
            mvn clean package -pl $1 -am
        fi
    else 
        echo "Usage: ccm.sh build-module PROFILE [MODULE]"
        exit 1
    fi
}

testccm() {
    if [ -n "$1" ]; then
       mvn clean test -P$1 
    else
       mvn clean test
    fi
}

testmodule() {
    if [ -n "$1" ]; then
        if [ -n "$2" ]; then
           mvn clean test -P$2 -pl $1 -am
        else
           mvn clean test -pl $1 -am
        fi
    else
        echo "Usage: ccm.sh test-module MODULE [PROFILE]"
        exit 1
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
            echo 1
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
    
    build-site [PROFILE]                   : Builds the Maven project site. 
    build [PROFILE]                        : Build all LibreCCM modules.        
    build-module MODULE [PROFILE]          : Build a specific LibreCCM module.
    testccm [PROFILE] [RUNTIME]            : Run tests for all modules.
    test-module MODULE [PROFILE] [RUNTIME] : Run tests for a specific LibreCCM 
                                             module.
    install-runtime [RUNTIME]              : Download and install a runtime 
                                             (application server) into ./runtime
    run [-r RUNTIME] [BUNDLE]              : Run a runtime (application server)
    help                                   : Show this help message.

    A detailed description of the subcommands is provided in ccm-readme.txt"
    

    
    exit 0;
}

case $1 in
    build-site)      buildsite $2 ;;
    build)           build $2 ;;
    build-module)    buildmodule $2 ;;
    testccm)         testccm $2 $3 ;;
    test-module)     testmodule $2 $3 $4 ;;
    install-runtime) installruntime $2 ;;
    run)             run $2 $3 $4 ;;
    stop-runtime)    stopruntime $2 ;;
    help)            showhelp ;;
    *)               showhelp ;;

esac


