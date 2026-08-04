import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = "com.intellij"
version = "2.24.0.262"

// TODO: improve, see https://github.com/gitpod-io/gitpod/blob/b43c97ed9a7e81a0568e237ef0d267fb312a317a/components/ide/jetbrains/backend-plugin/build.gradle.kts#L17

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
    id("org.jetbrains.intellij.platform") version "2.18.1"
    //id("org.jetbrains.intellij.platform.migration") version "2.0.0-beta6"
    //id("org.jetbrains.intellij") version "1.17.4"
    //kotlin("jvm") version "1.9.20"
    id("org.jetbrains.kotlin.jvm") version "2.4.10"

    // Plugin which can check for Gradle dependencies, use the help/dependencyUpdates task.
    //id("com.github.ben-manes.versions") version "0.51.0"

    // Plugin which can update Gradle dependencies, use the help/useLatestVersions task.
    //id("se.patrikerdes.use-latest-versions") version "0.2.18"

    // Vulnerability scanning
    //id("org.owasp.dependencycheck") version "9.2.0"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
        //jetbrainsRuntime()
        releases()
        snapshots()
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

intellijPlatform {
    pluginConfiguration {
        name = "Custom Postfix Templates"

        ideaVersion {
            untilBuild.set(provider { null })
        }
    }
}

dependencies {
    intellijPlatform {
        // full list of IntelliJ IDEA releases at https://www.jetbrains.com/intellij-repository/releases
        // full list of IntelliJ IDEA EAP releases at https://www.jetbrains.com/intellij-repository/snapshots
        //create("IU", "242.20224.159-EAP-SNAPSHOT")
        //intellijIdeaUltimate("242.20224.159-EAP-SNAPSHOT")
        intellijIdeaUltimate("2026.2.0.1")
        //jetbrainsRuntime()
        //plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        //bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        bundledPlugin("com.intellij.css")
        bundledPlugin("com.intellij.database")
        bundledPlugin("org.intellij.groovy")
        bundledModule("org.intellij.intelliLang")
        bundledPlugin("com.intellij.java")
        bundledPlugin("JavaScript")
        bundledPlugin("org.jetbrains.kotlin")
        plugin("com.jetbrains.php", "262.8665.176")
        plugin("com.jetbrains.rust", "262.8665.323")
        plugin("org.jetbrains.plugins.ruby", "262.8665.309")
        plugin("org.jetbrains.plugins.go", "262.8665.258")
        plugin("PythonCore", "262.8665.337")
        plugin("Pythonid", "262.8665.337")
        plugin("Dart", "508.0.0")
        //plugin("intellij.jupyter", "243.24978.50")
        plugin("org.intellij.scala", "2026.2.15")
        plugin("nl.rubensten.texifyidea", "1.0.0")
        plugin("dev.j-a.swift", "2.1.1.499-262")

        pluginVerifier()
        testFramework(TestFrameworkType.Platform)
    }

    implementation("commons-io:commons-io:2.22.0")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.22.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.1")
    implementation("io.sentry:sentry:8.51.0") {
        exclude("org.slf4j")
    }

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")

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

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 25
    }
    withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
            freeCompilerArgs.add("-jvm-default=no-compatibility")
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
