plugins {
    application
    id("com.gradleup.shadow") version "9.6.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("io.javalin:javalin:7.2.3")
    implementation("io.javalin:javalin-rendering-jte:7.2.3")
    implementation("gg.jte:jte:3.2.4")

    implementation("org.slf4j:slf4j-simple:2.0.17")

    // JCB-зависимости оставляем
    implementation("com.zaxxer:HikariCP:6.3.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.postgresql:postgresql:42.7.7")

    // тестовые зависимости тоже оставляем
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "hexlet.code.App"
}