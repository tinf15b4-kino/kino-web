node {
    try {
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
        slackSend "${env.BRANCH_NAME}: ${currentBuild.result} ()"
    }
}

