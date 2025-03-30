package io.foxx.kotlin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import com.android.build.gradle.BaseExtension


open class KotlinSourcesListTask: DefaultTask() {

    private fun getSourceSetNamesString(
        project: Project,
        header: String,
        sourceSets: Iterable<*>,
        getName: (Any) -> String,
        getFiles: (Any) -> Iterable<File>
    ): String {
        val sourceSetNames = StringBuilder()
        sourceSetNames.append(header).append("\n")
        sourceSets.forEach { sourceSet ->
            sourceSetNames.append("Source Set: ${sourceSet?.let { getName(it) }}:\n")
            if (sourceSet != null) {
                getFiles(sourceSet).forEach { file ->
                    sourceSetNames.append("\t").append(project.relativePath(file)).append("\n")
                }
            }
        }
        return sourceSetNames.toString()
    }



    @TaskAction
    fun run() {
        val reportFile = project.layout.buildDirectory.file("reports/sources.txt").get().asFile
        reportFile.parentFile.mkdirs()
        val allSourceSetNames = StringBuilder()

        project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            println("Kotlin JVM plugin is applied.")
            val jvmSourceSets = project.extensions.findByType(SourceSetContainer::class.java)
            jvmSourceSets?.let {
                allSourceSetNames.append(
                    getSourceSetNamesString(
                        project,
                        "--- JVM Source Sets ---",
                        it,
                        { sourceSet -> (sourceSet as SourceSet).name },
                        { sourceSet -> (sourceSet as SourceSet).allSource.asFileTree.files }
                    )
                )
            }
        }

        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            println("Kotlin Multiplatform plugin is applied.")
            val kmpExtension = project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)
            kmpExtension?.sourceSets?.let { sourceSets ->
                allSourceSetNames.append(
                    getSourceSetNamesString(
                        project,
                        "--- Multiplatform Source Sets ---",
                        sourceSets,
                        { sourceSet -> (sourceSet as org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet).name },
                        { sourceSet -> (sourceSet as org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet).kotlin.srcDirs }
                    )
                )
            }
        }

        project.pluginManager.withPlugin("org.jetbrains.kotlin.android") {
            println("Kotlin Android plugin is applied.")
            val androidExtension = project.extensions.findByName("android")
            if (androidExtension != null) {
                // Cast to a common Android extension type, for example BaseExtension.
                val baseExt = androidExtension as? com.android.build.gradle.BaseExtension
                baseExt?.sourceSets?.let { sourceSets ->
                    // Here, Android source sets don't provide a dedicated Kotlin container,
                    // so we use the java source directories.
                    allSourceSetNames.append(
                        getSourceSetNamesString(
                            project,
                            "--- Android Source Sets ---",
                            sourceSets,
                            { sourceSet -> (sourceSet as com.android.build.gradle.api.AndroidSourceSet).name },
                            { sourceSet -> (sourceSet as com.android.build.gradle.api.AndroidSourceSet).java.srcDirs }
                        )
                    )
                }
            }
        }

        project.pluginManager.withPlugin("org.jetbrains.kotlin.js") {
            println("Kotlin JS plugin is applied.")
            val jsSourceSets = project.extensions.findByType(SourceSetContainer::class.java)
            jsSourceSets?.let {
                allSourceSetNames.append(
                    getSourceSetNamesString(
                        project,
                        "--- JS Source Sets ---",
                        it,
                        { sourceSet -> (sourceSet as SourceSet).name },
                        { sourceSet -> (sourceSet as SourceSet).allSource.asFileTree.files }
                    )
                )
            }
        }

        // Write the combined report to the file.
        reportFile.writeText(allSourceSetNames.toString())
    }
}


class KotlinPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("listKotlinSourceSets", KotlinSourcesListTask::class.java) {
            group = "Reporting"
            description = "Outputs a list of all Kotlin source sets to a file"
        }
    }
}