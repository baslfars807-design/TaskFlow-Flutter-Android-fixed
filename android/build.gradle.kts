allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

// Match every Android plugin's Java and Kotlin bytecode targets.
// AGP defaults plugin Java tasks to 11, while some plugins (notably file_picker)
// still default Kotlin tasks to 1.8. Align both to 11 to avoid Gradle validation failure.
gradle.projectsEvaluated {
    allprojects {
        if (name != "app") {
            tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
                sourceCompatibility = "11"
                targetCompatibility = "11"
            }
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "11"
                }
            }
        }
    }
}