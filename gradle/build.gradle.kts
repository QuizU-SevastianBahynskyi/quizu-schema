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
    // Kotlin dependencies (already added)
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Dependencies for the generated Java classes
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("io.gsonfire:gson-fire:1.8.5")
//    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
}

val openApiGenerateKotlin by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    group = "build"
    description = "Generates Kotlin data classes from OpenAPI specification"
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/../openapi/openapi.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated/kotlin")
    apiPackage.set("com.quizu.kotlin.api")
    modelPackage.set("com.quizu.kotlin.model")
}

val openApiGenerateJava by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    group = "build"
    description = "Generates Java data classes from OpenAPI specification"
    generatorName.set("java")
    inputSpec.set("$rootDir/../openapi/openapi.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated/java")
    apiPackage.set("com.quizu.java.api")
    modelPackage.set("com.quizu.java.model")
}

// Add generated Kotlin sources to the Kotlin source set
sourceSets["main"].apply {
    java.srcDir("${layout.buildDirectory.get()}/generated/kotlin")
    java.srcDir("${layout.buildDirectory.get()}/generated/java")
    java.exclude("**/test/**")
}


// Ensure that the Kotlin and Java compilation depends on the generation of models
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(openApiGenerateKotlin, openApiGenerateJava)
}

tasks.withType<JavaCompile> {
    dependsOn(openApiGenerateKotlin, openApiGenerateJava)
}


// Task to create a JAR specifically for the generated classes
val generatedClassesJar by tasks.registering(Jar::class) {
    from("${layout.buildDirectory.get()}/classes/kotlin/main")
    from("${layout.buildDirectory.get()}/classes/java/main")
    dependsOn(openApiGenerateKotlin)
    dependsOn(openApiGenerateJava)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Publication block
publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            groupId = "com.quizu"
            artifactId = "quizu-schema"
            version = System.getenv("VERSION")

            // Use the generated JAR as an artifact
            artifact(generatedClassesJar.get())

            pom {
                name.set("QuizU Schema")
                description.set("Schema generated from OpenAPI specifications for QuizU project")
                url.set("https://github.com/QUIZU-SevastianBahynskyi/quizu-schema")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("QUIZU-SevastianBahynskyi")
                        name.set("Sevastian Bahynskyi")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/QUIZU-SevastianBahynskyi/quizu-schema.git")
                    developerConnection.set("scm:git:ssh://github.com:QUIZU-SevastianBahynskyi/quizu-schema.git")
                    url.set("https://github.com/QUIZU-SevastianBahynskyi/quizu-schema")
                }
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
