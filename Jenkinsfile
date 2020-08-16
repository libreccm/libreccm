pipeline {
    agent any
    tools {
        maven 'apache-maven-3.6.0'
    }
    stages {
        stage('Build and Test')  {
            steps {
                dir('') {
                    sh 'mvn clean verify -Prun-its-with-wildfly-h2mem'
                }
            }
        }
        stage("Analyse") {
            steps {
                dir('') {
                    sh 'mvn pmd:pmd pmd:cpd spotbugs:spotbugs'
                }
            }
        }
        stage("Deploy") {
            steps {
                dir('') {
                    configFileProvider([configFile(fileId: 'libreccm-packages-deploy', variable: 'MAVEN_SETTINGS')]) {
                        sh 'mvn -U -s "$MAVEN_SETTINGS" -e deploy'
                    }
                }
            }
        }
    }
    post {
        success {
            mail to: 'developers@scientificcms.org',
                 subject: "${currentBuild.fullDisplayName} was successful",
                 body: "Build ${env.BUILD_URL} was successful."
        }
        failure {
            mail to: 'developers@scientificcms.org',
                 subject: "${currentBuild.fullDisplayName} FAILED!!!",
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
