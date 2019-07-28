node {

    def mvnHome
    def nodeHome
    def version
    def app
    stage('Clean Workspace') {
        deleteDir()
    }
    stage('Setup build enviroment') {
        nodeHome = tool name: 'ProtoneNodejs', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
        env.PATH = "${nodeHome}/bin:${env.PATH}"
        git branch: 'develop', credentialsId: 'bitbucket', url: 'yourRepo'

    }
    stage('Check tools') {

        sh "${nodeHome}/bin/node -v"
        sh "node -v"
        sh "npm -v"

    }
    stage('Build version'){
        env.version= sh 'grep version package.json | cut -c 15- | rev | cut -c 3- | rev > outFile'
        env.version=  readFile 'outFile'
    }
    stage('Build -Install Dependencies') {
        sh "npm install"
    }
    stage('Run Lint') {
        sh "npm run lint"
    }

    stage('Image - latest') {
        sh "docker build . -t yourImageName:latest"
        sh "docker images -q yourImageName:latest >latest"
        def imageLatestID =  readFile 'latest'
        imageLatestID =imageLatestID.replaceAll("\\s","")
        sh "docker tag ${imageLatestID} yourDockerRepoAdress/yourImageName:latest"
        sh "docker rmi yourImageName:latest"
        sh "docker push yourDockerRepoAdress/yourImageName"
    }




}
