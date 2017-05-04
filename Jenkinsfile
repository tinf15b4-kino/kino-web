node {
    try {
        properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')), pipelineTriggers([])])

        stage 'Clean workspace'
        deleteDir()

        // Mark the code checkout 'stage'....
        stage 'Checkout'

        // Checkout code from repository and update any submodules
        checkout scm
        sh 'git submodule update --init'

        // Show yellow circle on GitHub
        setGithubBuildStatus("Started Build", "PENDING")

        stage 'Build'

        //branch name from Jenkins environment variables
        echo "My branch is: ${env.BRANCH_NAME}"

        sh "./gradlew clean assemble"

        stage 'Archive Jar'
        archiveArtifacts artifacts: '*/build/libs/*.jar', fingerprint: true

        stage 'Run Tests'
        sh "./gradlew test -Dkinotest.driver=remote --debug || true"
        junit '*/build/test-results/*.xml'
    } catch (e) {
        // If there was an exception thrown, the build failed
        currentBuild.result = "FAILED"

        throw e
    } finally {
        if (currentBuild.result == null) {
            // Show checkmark on GitHub
            setGithubBuildStatus("Finished", "SUCCESS")
        } else {
            // Show red x on GitHub
            setGithubBuildStatus("Finished", "FAILURE")
        }

        // Success or failure, always send notifications
        notifyBuild(currentBuild.result)
    }
}

def notifyBuild(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus =  buildStatus ?: 'SUCCESSFUL'

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = "${subject} (${env.BUILD_URL})"

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

    // Send notifications
    slackSend (color: colorCode, message: summary)
}

def getRepoURL() {
  sh "git config --get remote.origin.url > .git/remote-url"
  return readFile(".git/remote-url").trim()
}

def getCommitSha() {
  sh "git rev-parse HEAD > .git/current-commit"
  return readFile(".git/current-commit").trim()
}

void setGithubBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: getRepoURL()],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: getCommitSha()],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}
