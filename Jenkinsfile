#!/usr/bin/env groovy

node('java') {
    
    stage('Checkout') {
        checkout scm            
    }

    stage('Build') {
        if (isUnix()) {
            sh './gradlew'            
        } else {
            bat './gradlew.bat'
        }
    }
}
