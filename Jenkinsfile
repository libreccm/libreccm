pipeline {
    agent any
    tools {
        maven 'apache-maven-3.6.0'
    }
    stages {
        stage('Build and Test')  {
            steps {
                dir('') {
                    sh 'mvn clean package test -Pwildfly-remote-h2-mem' 
                }
            }
        }
        stage("Analyse") {
            steps {
                dir('') {
                    sh 'mvn package pmd:pmd pmd:cpd spotbugs:spotbugs'
                }
            }           
        }
    }
    post {
        success {
            mail to: 'developers@scientificcms.org',
                 subject: "${currentBuild.fullDisplayName} was successful"
                 body: "Build ${env.BUILD_URL} was successful."
        }
        failure {
            mail to: 'developers@scientificcms.org',
                 subject: "${currentBuild.fullDisplayName} FAILED!!!"
                 body: "Build ${env.BUILD_URL} failed."
        }
        always {
            junit testResults: '**/target/surefire-reports/*.xml'

            recordIssues enabledForFailure: true, tools: [java(), javaDoc()]
            recordIssues enabledForFailure: false, tool: spotBugs()
            recordIssues enabledForFailure: false, tool: cpd(pattern: '**/target/cpd.xml')
            recordIssues enabledForFailure: false, tool: pmdParser(pattern: '**/target/pmd.xml')
        }
    }
}