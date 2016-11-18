node {
  // Mark the code checkout 'stage'....
  stage 'Checkout'

  // Checkout code from repository and update any submodules
  checkout scm
  sh 'git submodule update --init'

  stage 'Build'

  //branch name from Jenkins environment variables
  echo "My branch is: ${env.BRANCH_NAME}"

  sh "cd 'web app'; ./gradlew clean assemble test"

  stage 'Archive Jar'
  archiveArtifacts artifacts: 'web app/build/libs/*.jar', fingerprint: true

  stage 'Store Test Results'
  junit 'web app/build/test-results/*.xml'
}
