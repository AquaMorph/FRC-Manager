node {
    stage('Git') {
        deleteDir()
        git branch: '**', credentialsId: 'a14847e5-f1a5-48d1-8833-9709f75f8471', url: 'git@github.com:AquaMorph/FRC-Manager.git'
        withCredentials([string(credentialsId: 'TBA_KEY', variable: 'TBA_KEY')]) {
            sh "echo 'TBA_KEY=\"${TBA_KEY}\"' > secrets.properties"
        }
    }
    stage('Build') {
        sh './gradlew clean assemble build'
    }
    stage('Analysis') {
        sh './gradlew pmd'
        pmd canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/rules-pmd.xml', thresholdLimit: 'high', unHealthy: ''
    }
}
