@Library('dim_jenkins_shared_library') _
pipeline {

    agent {
        node {
            label 'dim-jenkins-cloud-build-agent-jdk17'
        }

    }

    environment {
        SONAR_USER_TOKEN = credentials('project-bt-perso-analytics-token')
        ECR_DOCKER_URL = "860032816463.dkr.ecr.us-east-1.amazonaws.com"

    }

    parameters {
        string(name: 'SONAR_HOST_URL', defaultValue: 'https://cqc.cloud.sysco.net', description: 'SonarQube host URL')
        string(name: 'AWS_REGION_PROD', defaultValue: 'us-east-1', description: 'AWS region for production and pre-prod deployments (prod, stag)')
        string(name: 'CHANGE_ID', defaultValue: "", description: 'changeID')
        string(name: 'IMAGE_NAME', defaultValue: "bt-perso-analytics-ecr", description: 'image name')
        string(name: 'AWS_ACCOUNT_ID', defaultValue: "860032816463", description: 'aws account id')
        string(name: 'APPLICATION_NAME', defaultValue: "bt-perso-analytics", description: 'application name for build args')
        string(name: 'PROFILE', defaultValue: 'test', description: 'spring profile')
        choice(name: 'APPLICATION', choices: ['yes', 'no'], description: 'Select Yes If ECS Application Pipeline')
        choice(name: 'DEPLOYMENT_CHOICE', choices: ['None', 'prod'], description: 'Deploy to')
        choice(name: 'TERRAFORM_ACTION', choices: ['apply', 'destroy'], description: 'terraform action')
    }

    options {
        disableConcurrentBuilds()
    }

 stages{
     stage('Authenticate_to_ECR'){
         steps {
             authenticateForEcr(

                 ECR_DOCKER_CREDENTIALS_USR: 'AWS',
                 ECR_DOCKER_CREDENTIALS_PSW: '481241272663.dkr.ecr.us-east-1.amazonaws.com'

             )
         }
     }
    stage('Build_stage'){
        steps {
                persoUnitTestDb(
                    profile: params.PROFILE
                )

                bootstrapBuild(
                    profile: params.PROFILE
                )
        }
    }
    stage('docker_build'){
        steps {
            dockerBuildAndPublish(
             application: params.APPLICATION,
             repository_url: env.ECR_DOCKER_URL,
             region: params.AWS_REGION_PROD,
             image_name: params.IMAGE_NAME,
             environment: params.DEPLOYMENT_CHOICE,
             app_name: params.APPLICATION_NAME,
             awsAccountId: params.AWS_ACCOUNT_ID,
             awsRoleName: 'Dim-Jenkins-Cross-Account-Role'
             )
        }
    }
    stage('deploy'){
         when {
             allOf {
                   expression { GIT_BRANCH.matches("feature.*") || GIT_BRANCH.matches("master.*")}
                   environment name: 'DEPLOYMENT_CHOICE', value: 'prod'
                    }
                 }
        steps {
            script{
            ecsTerrafoamAction(
                application: params.APPLICATION,
                awsAccountId: params.AWS_ACCOUNT_ID,
                varFile: params.DEPLOYMENT_CHOICE+'/bt-perso-analitics.tfvars',
                awsRoleName: 'Dim-Jenkins-Cross-Account-Role',
                path: params.DEPLOYMENT_CHOICE,
                action: params.TERRAFORM_ACTION

                )
                }
            }
        }
    }
}
