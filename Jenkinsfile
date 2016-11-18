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

        stage 'Build'

        //branch name from Jenkins environment variables
        echo "My branch is: ${env.BRANCH_NAME}"

        sh "cd 'web app'; ./gradlew clean assemble test"

        stage 'Archive Jar'
        archiveArtifacts artifacts: 'web app/build/libs/*.jar', fingerprint: true

        stage 'Store Test Results'
        junit 'web app/build/test-results/*.xml'
    } catch (e) {
        // If there was an exception thrown, the build failed
        currentBuild.result = "FAILED"
        throw e
    } finally {
        // Success or failure, always send notifications
        stage 'Notify Slack'
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

