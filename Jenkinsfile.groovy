pipeline {
    agent any
    
    stages {
        stage('Clean') {
            steps {
                cleanWs()
                sh 'rm -rf .git'
            }
        }
        
        stage('Deploy') {
            steps {
                sh "sed -i 's/NoJenkins/Jenkins/' index.html"
                withCredentials([sshUserPrivateKey(credentialsId: 'jenkins', keyFileVariable: 'keyfile', usernameVariable: 'username')]) {
                    script {
                        [130, 140, 150].each {
                            sh "scp -i ${keyfile} -r $WORKSPACE ${username}@172.27.11.${it}:/tmp/html"
                            sh "ssh -i ${keyfile} ${username}@172.27.11.${it} 'sudo rm -rf /usr/share/nginx/html && sudo mv /tmp/html /usr/share/nginx/html && sudo chown -R root: /usr/share/nginx/html'"
                        }
                    }
                }
            }
        }
    }
}