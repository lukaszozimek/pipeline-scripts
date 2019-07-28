node {
    stage('Clean Workspace') {
        deleteDir()
    }
    stage('Stop Images'){
        sh " docker ps -q --filter ancestor=\"yourRepoAdress/yourImageName\" | xargs -r docker stop"
        sh " docker ps -q --filter ancestor=\"yourDb\" | xargs -r docker stop"
        sh " docker ps -q --filter ancestor=\"yourRepoAdress/yourImageFrontedName\" | xargs -r docker stop"
    }
    stage('Remove unused'){
        sh "docker rm -v \$(docker ps -a -q -f status=exited)"
    }
    stage('Setup build enviroment') {
        git branch: 'master', credentialsId: 'bitbucket', url: 'yourRepoAdressWithDevopsTools'
    }
    stage('Setup docker'){
        dir('protone-compose'){
            sh "nohup docker-compose -f yourComposeFile.yaml up &"
        }

    }
    stage('Wait until Server up'){
        timeout(240) {
            waitUntil {
                def r = sh script: "wget -q http://yourDNs/index.html -O /dev/null", returnStatus: true
                return (r == 0);
            }
        }

    }

}
