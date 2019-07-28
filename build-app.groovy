node {

    stage('Application'){
        parallel (
                'server':  {build job: 'build-server'},
                'client':  {build job: 'build-fronted'}
        )


    }

    stage('Deployment'){
        build job: 'deployment', wait: true
    }

}
