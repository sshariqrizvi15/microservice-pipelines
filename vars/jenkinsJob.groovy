def call(){
    node {
        stage('Preparation') { // for display purposes
                git 'https://github.com/sshariqrizvi15/pythonapp-withoutrds'
        }
        
        stage('Docker Push') {
                withDockerRegistry(credentialsId: 'dockerid') {
                    sh '''
                    docker build -t shariqrizvi/flaskapp .
                    docker push shariqrizvi/flaskapp
                    '''
                }
        }
        
        stage('Results') {
                sh label: '', script: '''#!/bin/sh
                # This is a comment!
                aws_instances=`aws ec2 describe-instances --filters Name=tag:Name,Values=ShariqPrivateInstance --query \'Reservations[].Instances[].[PrivateIpAddress]\' --output text`
                for instance in $aws_instances
                do
                    sudo su - ec2-user -c "scp /home/ec2-user/python_app.sh ec2-user@${instance}:/home/ec2-user/python_app.sh"
                    sudo su - ec2-user -c "ssh ec2-user@${instance} sudo chmod 777 /home/ec2-user/python_app.sh"
                    sudo su - ec2-user -c "ssh ec2-user@${instance} ./python_app.sh"
                done'''
        }
    }

}