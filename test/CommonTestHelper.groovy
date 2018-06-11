import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.rules.TemporaryFolder

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.LocalSource.localSource

class CommonTestHelper {

    static class DockerMock {
        class Image {
            String name;
            public Image(String name) { this.name = name; }
            public void pull() { println("docker.pull " + this.name); }
            public void inside(String args, Closure C) {
                C();
            }
        }

        public Image image(String name) {
            println()
            return new Image(name)
        }
    }


    static void setupTestEnv(TemporaryFolder folder, BasePipelineTest test) {
        def helper = test.helper
        def binding = test.binding
        def library = library().name('ctsrd-jenkins-scripts')
                .defaultVersion("master")
                .allowOverride(true)
                .implicit(false)
                .targetPath(folder.root.getAbsolutePath())
                .retriever(localSource('build/libs/'))
                .build()
        helper.registerSharedLibrary(library)
        helper.registerAllowedMethod("timeout", [Integer.class, Closure.class], null)
        helper.registerAllowedMethod("timeout", [Map.class, Closure.class], null)
        helper.registerAllowedMethod("disableResume", [], null)
        helper.registerAllowedMethod("githubPush", [], null)
        helper.registerAllowedMethod("deleteDir", [], null)
        helper.registerAllowedMethod("checkout", [Map.class], { args -> [GIT_URL:args.url, GIT_COMMIT:"abcdef123456"] })
        helper.registerAllowedMethod("compressBuildLog", [], null)
        helper.registerAllowedMethod("junit", [Map.class], null)
        helper.registerAllowedMethod("durabilityHint", [String.class], null)
        helper.registerAllowedMethod("timestamps", [Closure.class], null)
        helper.registerAllowedMethod("ansiColor", [String.class, Closure.class], null)
        helper.registerAllowedMethod("copyArtifacts", [Map.class], /*{ args -> println "Copying $args" }*/null)
        helper.registerAllowedMethod("warnings", [Map.class], /*{ args -> println "Copying $args" }*/null)
        helper.registerAllowedMethod("git", [String.class], { url -> [GIT_URL:url, GIT_COMMIT:"abcdef123456"] })
        helper.registerAllowedMethod("git", [Map.class], { args -> [GIT_URL:args.url, GIT_COMMIT:"abcdef123456"] })
        // binding.getVariable('env').JOB_NAME = "CHERI1-TEST-pipeline"
        // helper.registerAllowedMethod("cheriHardwareTest", [Map.class], { args -> cheriHardwareTest.call(args) })
        def scmBranch = "feature_test"
        binding.setVariable('scm', [branch: 'master', url:'scm.git'])
        binding.setVariable('docker', new DockerMock())
        /* binding.setVariable('scm', [
                $class                           : 'GitSCM',
                branches                         : [[name: scmBranch]],
                doGenerateSubmoduleConfigurations: false,
                extensions                       : [],
                submoduleCfg                     : [],
                userRemoteConfigs                : [[
                                                            credentialsId: 'gitlab_git_ssh',
                                                            url          : 'github.com/lesfurets/JenkinsPipelineUnit.git'
                                                    ]]
        ]) */
        binding.setVariable('currentBuild', [result: 'SUCCESS', currentResult: 'SUCCESS', durationString: "XXX seconds"])
    }

}