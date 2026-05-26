import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.application
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "org.course"
version = "1.0"
description = "Kotlin Training Labs"
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}
plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.3.21"
    id("org.jetbrains.kotlin.plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    java
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

object Versions {
    const val kotlin_version = "2.3.21"
    const val kotlinx_version = "1.10.2"
    const val spring_ai_version = "2.0.0-M3"
    const val kotest_version = "6.0.2"
    const val junit_jupiter_version = "6.0.2"
    const val springmockk_version = "5.0.1"
    const val immutable_collections_version = "0.4.0"
    const val micrometer_atlas_version = "1.11.11"
}

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:${Versions.spring_ai_version}"))
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:${Versions.kotlin_version}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin_version}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.kotlinx_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:${Versions.immutable_collections_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:${Versions.kotlinx_version}")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.ai:spring-ai-client-chat")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-h2")
    implementation("io.micrometer:micrometer-registry-atlas:${Versions.micrometer_atlas_version}")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:${Versions.kotest_version}")
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit_jupiter_version}")
    testImplementation("com.ninja-squad:springmockk:${Versions.springmockk_version}")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")

}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

application{
    applicationDefaultJvmArgs = listOf("--enable-preview", "--add-modules", "jdk.incubator.concurrent")
}

tasks.test {
    useJUnitPlatform()
    reports {
        junitXml.isOutputPerTestCase = true
    }
    systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "true")
}

tasks.register<Delete>("cleanup") {
    delete(layout.buildDirectory)
}
