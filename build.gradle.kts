
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(ktorLibs.plugins.ktor)
}

group = "com.ada.training"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "com.ada.training.MainKt"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-status-pages")
    implementation(ktorLibs.server.cio)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.core)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}

tasks.register<JavaExec>("runServer") {
    group = "examples"
    description = "Runs the Ktor API used in the training."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.ada.training.MainKt"
}

tasks.register<JavaExec>("runKotlinBasicsDemo") {
    group = "examples"
    description = "Runs an interactive Kotlin language demo."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.ada.training.playground.KotlinBasicsDemoKt"
    standardInput = System.`in`
}

tasks.register<JavaExec>("runKotlinSyntaxDemo") {
    group = "examples"
    description = "Runs a Kotlin syntax comparison demo."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.ada.training.playground.KotlinSyntaxDemoKt"
}

tasks.register<JavaExec>("runOrderScenarioDemo") {
    group = "examples"
    description = "Runs an interactive domain/service demo without HTTP."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.ada.training.playground.OrderScenarioDemoKt"
    standardInput = System.`in`
}

tasks.register<JavaExec>("runSseClientDemo") {
    group = "examples"
    description = "Consumes a Spring WebFlux text/event-stream endpoint with Ktor Client."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.ada.training.integration.sse.SpringFluxSseDemoKt"
    args = listOf("http://localhost:8081/inventory/stream")
}
