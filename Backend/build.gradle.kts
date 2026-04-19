import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.animalleague"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:jdbc")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named<Test>("test") {
    filter {
        excludeTestsMatching("*Contract*")
        excludeTestsMatching("*Integration*")
        excludeTestsMatching("*Unit*")
        isFailOnNoMatchingTests = false
    }
}

fun registerSuiteTask(taskName: String, includePattern: String, descriptionText: String) =
    tasks.register<Test>(taskName) {
        description = descriptionText
        group = "verification"
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath
        useJUnitPlatform()
        filter {
            includeTestsMatching(includePattern)
            isFailOnNoMatchingTests = true
        }
        shouldRunAfter(tasks.named("test"))
    }

val contractTest = registerSuiteTask(
    taskName = "contractTest",
    includePattern = "*Contract*",
    descriptionText = "계약 테스트만 실행한다."
)

val integrationTest = registerSuiteTask(
    taskName = "integrationTest",
    includePattern = "*Integration*",
    descriptionText = "통합 테스트만 실행한다."
)

integrationTest.configure {
    doFirst {
        if (providers.environmentVariable("CI").orNull == "true") {
            providers.exec {
                commandLine("docker", "info")
            }.result.get()
        }
    }
}

val unitTest = registerSuiteTask(
    taskName = "unitTest",
    includePattern = "*Unit*",
    descriptionText = "단위 테스트만 실행한다."
)

tasks.named("check") {
    dependsOn(contractTest, integrationTest, unitTest)
}
