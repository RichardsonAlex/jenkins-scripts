import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.LocalSource.localSource

import org.junit.Before
import org.junit.Test

import com.lesfurets.jenkins.unit.cps.BasePipelineTestCPS

class TestCHERI1Test extends BasePipelineTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Override
    @Before
    void setUp() throws Exception {
        scriptRoots += 'vars'
        super.setUp()
        // String sharedLibs = this.class.getResource('vars').getFile()
        /* def library = library().name('cheriHardwareTest')
                .defaultVersion("master")
                .allowOverride(true)
                .implicit(true)
                .targetPath(folder.root.getAbsolutePath())
                .retriever(localSource('vars'))
                .build()
        helper.registerSharedLibrary(library) */
        helper.registerAllowedMethod("timeout", [Integer.class, Closure.class], null)
        helper.registerAllowedMethod("timeout", [Map.class, Closure.class], null)
        helper.registerAllowedMethod("copyArtifacts", [Map.class], /*{ args -> println "Copying $args" }*/null)
        binding.setVariable("env", ["JOB_NAME":"CHERI1-FPU-TEST-pipeline/master"])
        // binding.getVariable('env').JOB_NAME = "CHERI1-TEST-pipeline"
        // helper.registerAllowedMethod("cheriHardwareTest", [Map.class], { args -> cheriHardwareTest.call(args) })
        def scmBranch = "feature_test"
        binding.setVariable('scm', [branch: 'master'])
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
    }

    @Test
    void should_execute_without_errors() throws Exception {
        def script = loadScript("test-scripts/CHERI1Test.groovy")
        script.run()
        printCallStack()
    }
}