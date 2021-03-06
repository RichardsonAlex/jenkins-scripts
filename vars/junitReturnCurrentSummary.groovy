// https://stackoverflow.com/questions/39920437/how-to-access-junit-test-counts-in-jenkins-pipeline-project

import hudson.tasks.test.AbstractTestResultAction
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def currentTestResult() {
    def rawBuild = currentBuild.rawBuild
    if (rawBuild == null) {
        echo("WARNING: could not get rawBuild!")
        return [totalCount : 0,
                failCount  : 0,
                skipCount  : 0,
                passCount  : 0,
                passedTests: []]
    }
    AbstractTestResultAction testResultAction = rawBuild.getAction(AbstractTestResultAction.class)
    // TODO: do I need to convert this to a Map?
    return testResultAction
}

def call(Map args) {
    def newResult = [:]
    lock('junit-test-results-lock') {
        def result = junit(args)
        def prevResult = currentTestResult()
        echo("totalCount : ${result.totalCount} / ${prevResult?.totalCount}, " +
                "failCount : ${result.failCount} / ${prevResult?.failCount}, " +
                "skipCount : ${result.skipCount} / ${prevResult?.skipCount}, " +
                "passCount : ${result.passCount} / ${prevResult?.passedTests?.size()}")
//        newResult = [totalCount: result.totalCount - prevResult.totalCount,
//                     failCount : result.failCount - prevResult.failCount,
//                     skipCount : result.skipCount - prevResult.skipCount,
//                     passCount : result.passCount - prevResult.passedTests.size()]
        newResult = result
    }
    return newResult
}
