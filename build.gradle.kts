import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    // Java support
    id("java")
    // Kotlin support
    //id("org.jetbrains.kotlin.jvm") version "1.7.10"
    // Gradle Changelog Plugin
    //id("org.jetbrains.changelog") version "1.3.1"
    // Gradle Qodana Plugin
    //id("org.jetbrains.qodana") version "2024.1.5"
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij.platform") version "2.0.0"
    //id("org.jetbrains.intellij.platform.migration") version "2.0.0-beta6"
    //id("org.jetbrains.intellij") version "1.17.4"
    //kotlin("jvm") version "1.9.20"
    id("org.jetbrains.kotlin.jvm") version "2.0.0"

    // Plugin which can check for Gradle dependencies, use the help/dependencyUpdates task.
    //id("com.github.ben-manes.versions") version "0.51.0"

    // Plugin which can update Gradle dependencies, use the help/useLatestVersions task.
    //id("se.patrikerdes.use-latest-versions") version "0.2.18"

    // Vulnerability scanning
    //id("org.owasp.dependencycheck") version "9.2.0"
}

group = "com.intellij"
version = "2.20.3.242"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        //localPlatformArtifacts()
    }
    /*
    intellijPlatformTesting {
      runIde
      testIde
      testIdeUi
      testIdePerformance
    }
     */
}


dependencies {
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1")
    implementation("io.sentry:sentry:7.10.0") {
        exclude("org.slf4j")
    }
    // https://mvnrepository.com/artifact/org.projectlombok/lombok

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation("junit:junit:4.13.2")
    //implementation(kotlin("stdlib-jdk8"))
    //testImplementation("org.opentest4j:opentest4j:1.3.0")
}




sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDir("resources")
    }

    /*
    test {
        java.srcDir("test/src")
        resources.srcDir("test/resources")
    }
     */

}

intellijPlatform {
    pluginConfiguration {
        name = "foo"
    }
}

dependencies {
    intellijPlatform {
        // full list of IntelliJ IDEA releases at https://www.jetbrains.com/intellij-repository/releases
        // full list of IntelliJ IDEA EAP releases at https://www.jetbrains.com/intellij-repository/snapshots
        //create("IU", "242.20224.159-EAP-SNAPSHOT")
        intellijIdeaUltimate("2024.1.4")
        //plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        //bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        bundledPlugin("com.intellij.css")
        bundledPlugin("com.intellij.database")
        bundledPlugin("org.intellij.groovy")
        bundledPlugin("org.intellij.intelliLang")
        bundledPlugin("com.intellij.java")
        bundledPlugin("JavaScript")
        bundledPlugin("org.jetbrains.kotlin")
        plugin("org.intellij.scala", "2024.2.5")
        plugin("com.jetbrains.php", "242.16677.21")
        plugin("org.jetbrains.plugins.ruby", "242.16677.21")
        plugin("org.jetbrains.plugins.go", "242.16677.21")
        plugin("nl.rubensten.texifyidea", "0.9.6")
        plugin("Pythonid", "242.16677.21")
        instrumentationTools()
    }
}

/*
intellij {
    //version "IU-233.11799.6-EAP-SNAPSHOT"
//    type.set("IU")
    //version.set("241.14024.14-EAP-SNAPSHOT")
//    version.set("242.16677.21-EAP-SNAPSHOT")

    plugins.set(
        listOf(
            "java",
            "Pythonid:242.16677.21",
            //"Kotlin",
            "org.intellij.scala:2024.2.5",
            "JavaScript",
            //"CSS",
            "Dart:242.16677.12",
            "Groovy",
            "properties",
            "org.jetbrains.plugins.ruby:242.16677.21",
            "com.jetbrains.php:242.16677.21",
            "java-i18n",
            "DatabaseTools",
            "org.toml.lang",
            "org.jetbrains.plugins.go:242.16677.21",
            "nl.rubensten.texifyidea:0.9.6"
        )
    )
    updateSinceUntilBuild.set(true)
}
 */

tasks {
    withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    // Avoid ClassNotFoundException: com.maddyhome.idea.copyright.psi.UpdateCopyrightsProvider
    buildSearchableOptions {
        // jvmArgs = ["-Djava.system.class.loader=com.intellij.util.lang.PathClassLoader"]
        enabled = false
    }

    verifyPlugin {
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
