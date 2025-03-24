import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions.assertTrue;

class JvmTest {
    @TempDir
    lateinit var testProjectDir: File;

    @Test
    fun jvmPluginSourcesTest() {
        val buildFile = File(testProjectDir, "build.gradle.kts")
        buildFile.writeText(
            """
            plugins {
                id("io.foxx.kotlinSourcesPlugin")
                id("org.jetbrains.kotlin.jvm") version "1.9.23"
            }
            
            repositories {
                mavenCentral()
            }
            """.trimIndent()
        )

        val appFile = File(testProjectDir, "src/main/kotlin/App.kt")
        appFile.parentFile.mkdirs()
        appFile.writeText(
            """
                class App {
                    val greeting: String = "Hello!";
                }

                fun main() {
                    println(App().greeting)
                }
            """.trimIndent()
        )

        val appTestFile = File(testProjectDir, "src/test/kotlin/AppTest.kt")
        appTestFile.parentFile.mkdirs()
        appTestFile.writeText(
            """
                import org.junit.jupiter.api.Test;
                import org.junit.jupiter.api.Assertions.assertEquals;
                import org.junit.jupiter.api.Assertions.assertTrue;
                
                class AppTest {
                    private fun add(a: Int, b: Int): Int {
                        return a+b;
                    }
                
                    @Test
                    fun simpleTest() {
                        assertEquals(3, add(1, 2));
                    }
                }
            """.trimIndent()
        )



        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("listKotlinSourceSets")
            .withPluginClasspath()
            .build()

        val taskOutcome = result.task(":listKotlinSourceSets")?.outcome
        assertEquals(TaskOutcome.SUCCESS, taskOutcome)

        val reportFile = File(testProjectDir, "build/reports/sources.txt")
        assertTrue(reportFile.exists(), "sources.txt file was not generated.")

        val reportContent = reportFile.readText()
        println(reportContent)

        assertTrue(reportContent.contains("Source Set: main:"), "Report does not include the main source set information.")
        assertTrue(reportContent.contains("Source Set: test:"), "Report does not include the test source set information.")
        assertTrue(reportContent.contains("src\\main\\kotlin\\App.kt"), "Report does not include the App file from the main source set.")
        assertTrue(reportContent.contains("src\\test\\kotlin\\AppTest.kt"), "Report does not include the App file from the test source set.")
    }
}