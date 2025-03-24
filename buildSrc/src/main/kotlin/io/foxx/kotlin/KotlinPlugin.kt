package io.foxx.kotlin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File


open class KotlinSourcesListTask: DefaultTask() {

    @TaskAction
    fun run() {
        val sources = StringBuilder()
        val file: File = project.layout.buildDirectory.file("reports/sources.txt").get().asFile
        file.parentFile.mkdirs()

        project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            println("Kotlin JVM plugin is applied.")
            sources.append("--- JVM Source Sets ---\n")
            val jvmSourceSets = project.extensions
                .findByType(SourceSetContainer::class.java)

            jvmSourceSets?.forEach { sourceSet ->
                    sources.append("Source Set: " + sourceSet.name + ":\n")
                    sourceSet.allSource.asFileTree.files.forEach { file ->
                    sources.append("\t" + project.relativePath(file) + "\n")
                }
            }
            file.writeText(sources.toString())
        }
    }
}

class KotlinPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val KotlinPluginIds = listOf(
            "org.jetbrains.kotlin.jvm",
            "org.jetbrains.kotlin.multiplatform",
            "org.jetbrains.kotlin.android",
            "org.jetbrains.kotlin.js"
        )

        target.tasks.register("listKotlinSourceSets", KotlinSourcesListTask::class.java) {
            group = "Reporting"
            description = "Outputs a list of all Kotlin source sets to a file"
        }
    }
}