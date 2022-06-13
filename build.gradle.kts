import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.stefansevelda"
version = "1.0-SNAPSHOT"

fun kotlinCoroutines(module: String, version: String): Any =
    "org.jetbrains.kotlinx:kotlinx-coroutines-$module:$version"

repositories {
    mavenCentral()
}


dependencies {
    implementation("io.arrow-kt:arrow-core:1.1.2")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.1.2")
    implementation("io.arrow-kt:arrow-fx-stm:1.1.2")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.2.5")
    testImplementation(kotlinCoroutines("test", "1.5.2"))
    testImplementation("io.kotest:kotest-assertions-arrow-jvm:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(kotlin("test"))
    implementation(kotlin("script-runtime"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}