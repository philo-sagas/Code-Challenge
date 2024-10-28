pipeline {
    agent none
    stages {
        stage('package by maven') {
            agent {
                docker {
                    image 'maven:3.8.5-openjdk-17'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '*/target/*.tar.gz', fingerprint: true
                    junit '*/target/surefire-reports/**/*.xml'
                    jacoco(
                        execPattern: '*/target/jacoco.exec',
                        classPattern: '*/target/classes',
                        sourcePattern: '*/src/main/java'
                    )
                }
            }
        }
        stage('create a docker image') {
            agent { docker 'docker:27-cli' }
            steps {
                sh 'docker rmi `docker images -q philosagas/code-challenge:latest` || true'
                sh 'docker build -t philosagas/code-challenge:latest .'
            }
        }
    }
}