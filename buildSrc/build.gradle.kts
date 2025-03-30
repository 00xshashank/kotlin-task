plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
    implementation("com.android.tools.build:gradle:7.4.0")
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}


gradlePlugin {
    plugins {
        create("kotlinSourcesPlugin") {
            id = "io.foxx.kotlinSourcesPlugin"
            implementationClass = "io.foxx.kotlin.KotlinPlugin"
        }
    }
}