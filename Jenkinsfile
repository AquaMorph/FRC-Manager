node {
    stage('Git') {
        deleteDir()
        git branch: '**', credentialsId: 'a14847e5-f1a5-48d1-8833-9709f75f8471', url: 'git@github.com:AquaMorph/FRC-Manager.git'
    }
    stage('Build') {
        sh '''
            ./gradlew clean assemble build
        '''
   }
}
