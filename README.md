# kotlin-task
Kotlin source enumeration task

Plugin logic: [KotlinPlugin.kt](buildSrc/src/main/kotlin/io/foxx/kotlin/KotlinPlugin.kt)

When applied to a project, generates a file at build/reports/sources.txt containing a list of all source sets for the project.

Enumerates source sets for all Kotlin compilation targets.

Tests: [test/](buildSrc/src/test)