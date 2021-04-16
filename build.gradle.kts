plugins {
    java
    kotlin("jvm") version "1.4.32"
//    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

application {
    mainClass.set("com.kaj.dmvsnare.ApplicationKt")
}

group = "com.kaj"
version = "0.2"
val ktorVersion = "1.5.3"
val logbackVersion = "1.2.3"

repositories {
    mavenCentral()
}

dependencies {
    //standard libs
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    implementation(kotlin("stdlib"))

    //http
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion") //CIO engine

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    //logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

