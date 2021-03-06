void setBuildStatus(String message, String state) {
        step([
            $class: "GitHubCommitStatusSetter",
            reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/AquaMorph/FRC-Manager"],
            contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
            errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
            statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
        ]);
}

pipeline {
    agent any
    stages {
        stage('Setup') {
            steps {
                deleteDir()
                git branch: '**', credentialsId: 'a14847e5-f1a5-48d1-8833-9709f75f8471', url: 'git@github.com:AquaMorph/FRC-Manager.git'
                withCredentials([string(credentialsId: 'TBA_KEY', variable: 'TBA_KEY')]) {
                    sh "echo 'TBA_KEY=\"${TBA_KEY}\"' > secrets.properties"
                }
            }
        }
        stage('Build') {
            steps {
                sh './gradlew --no-daemon clean assemble build'
            }
        }
    }
    post {
        success {
            setBuildStatus("Build succeeded", "SUCCESS");
        }
        failure {
            setBuildStatus("Build failed", "FAILURE");
        }
    }
}