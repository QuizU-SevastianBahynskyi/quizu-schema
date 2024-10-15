plugins {
    kotlin("jvm") version "2.0.21"
    id("org.openapi.generator") version "7.9.0"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

val openApiGenerateKotlin by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    group = "build"
    description = "Generates Kotlin data classes from OpenAPI specification"
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/../openapi/openapi.yaml") // Directly setting the string path
    outputDir.set("${project.buildDir}/generated/kotlin") // Output directory for generated files
    apiPackage.set("com.quizu.api") // Desired package name for API classes
    modelPackage.set("com.quizu.model") // Desired package name for model classes
}

// Task for generating Java data classes from OpenAPI specification
val openApiGenerateJava by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    group = "build"
    description = "Generates Java data classes from OpenAPI specification"
    generatorName.set("java")
    inputSpec.set("$rootDir/../openapi/openapi.yaml") // Directly setting the string path
    outputDir.set("${project.buildDir}/generated/java") // Output directory for generated files
    apiPackage.set("com.quizu.api") // Desired package name for API classes
    modelPackage.set("com.quizu.model") // Desired package name for model classes
}

// Ensure that the Kotlin compilation depends on the generation of Kotlin and Java models
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    dependsOn(openApiGenerateKotlin)
    dependsOn(openApiGenerateJava)
}



publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["java"]) // Publish Java components

            groupId = "com.quizu"
            artifactId = "quizu-schema"
            version = "0.1.0"

            // Add the generated Kotlin files to the publication
            artifact("${project.buildDir}/libs/quizu-schema.jar") {
                classifier = "kotlin"
            }
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/QUIZU-SevastianBahynskyi/quizu-schema")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
