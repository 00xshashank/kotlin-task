import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File

class ManualSourceAdditionTest {

    @TempDir
    lateinit var testDir: File

    @Test
    fun ManualSourceAddTest() {
        val buildFile = File(testDir, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("io.foxx.kotlinSourcesPlugin")
                id("org.jetbrains.kotlin.jvm") version "1.9.23"
                java 
            }
            
            repositories {
                mavenCentral()
            }
            
            sourceSets {
                create("manual") {
                    java.srcDir("src/manual/java")
                }
            }
        """.trimIndent()
        )

        val srcDir = File(testDir, "src/main/java")
        srcDir.mkdirs()
        val manuallyAddedDir = File(testDir, "src/manual/java")
        manuallyAddedDir.mkdirs()

        File(srcDir, "Dummy.java").writeText(
            """
            public class Dummy {}
            """.trimIndent()
        )

        File(manuallyAddedDir, "ManualDummy.java").writeText(
            """
            public class ManualDummy {}
            """.trimIndent()
        )

        val settingsFile = File(testDir, "settings.gradle.kts")
        settingsFile.writeText("rootProject.name = \"test-project\"")

        val result = GradleRunner.create()
            .withProjectDir(testDir)
            .withArguments("listKotlinSourceSets", "--stacktrace")
            .withPluginClasspath()
            .build()

        val taskOutcome = result.task(":listKotlinSourceSets")?.outcome
        println("Task outcome: $taskOutcome")
        assertEquals(TaskOutcome.SUCCESS, taskOutcome)

        val reportFile = File(testDir, "build/reports/sources.txt")
        assertTrue(reportFile.exists(), "sources.txt file was not generated.")

        val reportContent = reportFile.readText()
        println("Report content:\n$reportContent")
        assertTrue(reportContent.contains("Source Set: manual:"), "Report does not include the manual source set information.")
    }
}