pipeline {
    agent any
    parameters {
        string(name: 'DevHost', defaultValue: '127.0.0.1', description: 'Host name of DEV server')
        choice(name: 'Orchestrator', choices: ['kubernetes', 'openshift'], description: 'Which orchestrator')
    }
    environment {
        COMMIT_ID = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim();
        ORCHESTRATOR = "${params.Orchestrator}"
        DB_IMAGE = "${params.Orchestrator == 'kubernetes' ? 'postgres:10.4' : 'centos/postgresql-96-centos7'}"
    }
    stages {
        stage('Build') {
            steps {
                sh './mvn_steps.sh build'
            }
        }
        stage('Component tests') {
            steps {
                sh './mvn_steps.sh component_tests'
            }
        }
        stage('Build Images') {
            steps {
                sh "./mvn_steps.sh build_image ${env.COMMIT_ID}"
            }
        }
        stage('Push Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'username', passwordVariable: 'password')]) {
                    sh "./mvn_steps.sh push_image ${env.COMMIT_ID} ${username} ${password}"
                }
            }
        }
        stage("Deploy") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dev-ssh', usernameVariable: 'username', passwordVariable: 'password')]) {
                    script {
                        remote = [:]
                        remote.name = 'dev'
                        remote.host = "${params.DevHost}"
                        remote.allowAnyHosts = true
                        remote.user = username
                        remote.password = password
                    }
                    sh 'envsubst < config/templates/deployment.yml.template > config/templates/deployment.yml'
                    sshPut remote: remote, from: 'config/templates', into: 'config'

                    sh 'envsubst < accounts/templates/deployment.yml.template > accounts/templates/deployment.yml'
                    sshPut remote: remote, from: 'accounts/templates', into: 'accounts'
                    sshPut remote: remote, from: "accounts/templates-${params.Orchestrator}", into: "accounts"

                    sh 'envsubst < authorization/templates/deployment.yml.template > authorization/templates/deployment.yml'
                    sshPut remote: remote, from: 'authorization/templates', into: 'authorization'

                    sh 'envsubst < exercises/templates/deployment.yml.template > exercises/templates/deployment.yml'
                    sshPut remote: remote, from: 'exercises/templates', into: 'exercises'

                    sh 'envsubst < web/templates/deployment.yml.template > web/templates/deployment.yml'
                    sshPut remote: remote, from: 'web/templates', into: 'web'

                    sshScript remote: remote, script: "deploy/${params.Orchestrator}.sh"
                }
            }
        }
    }
}