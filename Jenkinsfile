pipeline {
  agent any
  stages {
    stage('Preparation') {
      steps {
        sh 'echo "Preparation"'
      }
    }
    stage('Build') {
      steps {
        sh 'mvn clean install -U -DskipTests -pl !cs-vmware'
      }
    }
    stage('Test') {
      steps {
        sh 'echo "Test"'
      }
    }
    stage('Deploy') {
      steps {
        sh 'echo "Deploy something"'
      }
    }
    stage('Notify') {
      steps {
        sh 'echo "Notification"'
      }
    }
  }
}
