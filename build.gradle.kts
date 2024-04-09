plugins {
    // Java support
    id("java")
    // Kotlin support
    //id("org.jetbrains.kotlin.jvm") version "1.7.10"
    // Gradle Changelog Plugin
    //id("org.jetbrains.changelog") version "1.3.1"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij") version "1.14.2"
    kotlin("jvm") version "1.9.10"
}

group = "com.intellij"
version = "2.20.2.241"

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.1")
    implementation("io.sentry:sentry:6.9.0") {
        exclude("org.slf4j")
    }
    // https://mvnrepository.com/artifact/org.projectlombok/lombok

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testCompileOnly("org.projectlombok:lombok:1.18.26")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.26")

    testImplementation("junit:junit:4.13.2")
    //implementation(kotlin("stdlib-jdk8"))
}




sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDir("resources")
    }

    test {
        java.srcDir("test/src")
        resources.srcDir("test/resources")
    }

}


intellij {
    // full list of IntelliJ IDEA releases at https://www.jetbrains.com/intellij-repository/releases
    // full list of IntelliJ IDEA EAP releases at https://www.jetbrains.com/intellij-repository/snapshots
    //version "IU-233.11799.6-EAP-SNAPSHOT"
    type.set("IU")
    //version.set("241.14024.14-EAP-SNAPSHOT")
    version.set("241.14494.240")

    plugins.set(
        listOf(
            "java",
            "Pythonid:241.14494.240",
            "Kotlin",
            "org.intellij.scala:2024.1.7",
            "JavaScript",
            //"CSS",
            "Dart:241.15845",
            "Groovy",
            "properties",
            "org.jetbrains.plugins.ruby:241.14494.240",
            "com.jetbrains.php:241.14494.240",
            "java-i18n",
            "DatabaseTools",
            "org.toml.lang",
            "org.jetbrains.plugins.go:241.14494.240",
            "nl.rubensten.texifyidea:0.9.4"
        )
    )
    updateSinceUntilBuild.set(true)
}

tasks {
    // Avoid ClassNotFoundException: com.maddyhome.idea.copyright.psi.UpdateCopyrightsProvider
    buildSearchableOptions {
        // jvmArgs = ["-Djava.system.class.loader=com.intellij.util.lang.PathClassLoader"]
        enabled = false
    }

    runPluginVerifier {
        //ideVersions.set(listOf(intellij.type.get() + "-" + intellij.version.get()))
        //ideVersions("IU-222.3345.118")
        //setFailureLevel(RunPluginVerifierTask.FailureLevel.ALL)
    }

    publishPlugin {
        token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
    }
}
/*
kotlin {
    jvmToolchain(20)
}
 */
tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
