node {

    def mvnHome
    def nodeHome
    stage('Clean Workspace') {
        deleteDir()
    }
    stage('Setup build enviroment') {
        env.PATH = "${nodeHome}/bin:${env.PATH}"
        git branch: 'develop', credentialsId: 'Github', url: 'yourRepo'
        mvnHome = tool 'M3'
    }
    stage('Check tools') {

        sh "'${mvnHome}/bin/mvn' -version"

    }
    stage('Build -Version') {
        def pom = readMavenPom file:'pom.xml'
        print pom.version
        env.version = pom.version
    }


    stage('Build - Compile') {
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' -DskipTests clean compile"
        } else {
            bat(/"${mvnHome}\bin\mvn" -DskipTests clean package/)
        }
    }

    stage('Build - Test') {
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' test"
        } else {
            bat(/"${mvnHome}\bin\mvn" test clean/)
        }
    }

    stage('Build - Pack') {
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' clean  package -Pprod -DskipTests"
        } else {
            bat(/"${mvnHome}\bin\mvn" clean  package -Pprod -DskipTests/)
        }
    }

    stage('Image - latest') {
        sh "docker build . -t yourImage:latest"
        sh "docker images -q yourImage:latest >latest"
        def imageLatestID =  readFile 'latest'
        imageLatestID =imageLatestID.replaceAll("\\s","")
        sh "docker tag ${imageLatestID} yourDockerRepoAdress/yourImage:latest"
        sh "docker rmi yourImage:latest"
        sh "docker push yourDockerRepoAdress/yourImage"
    }
}
