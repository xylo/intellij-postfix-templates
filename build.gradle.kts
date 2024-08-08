import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = "com.intellij"
version = "2.20.5.242"

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

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
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
        intellijIdeaUltimate("2024.2")
        //plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        //bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        bundledPlugin("com.intellij.css")
        bundledPlugin("com.intellij.database")
        bundledPlugin("org.intellij.groovy")
        bundledPlugin("org.intellij.intelliLang")
        bundledPlugin("com.intellij.java")
        bundledPlugin("JavaScript")
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("org.jetbrains.kotlin")
        plugin("com.jetbrains.php", "242.20224.300")
        plugin("org.jetbrains.plugins.ruby", "242.20224.300")
        plugin("org.jetbrains.plugins.go", "242.20224.300")
        plugin("Pythonid", "242.16677.21")
        plugin("org.intellij.scala", "2024.2.20")
        plugin("nl.rubensten.texifyidea", "0.9.7")
        instrumentationTools()

        pluginVerifier()
        testFramework(TestFrameworkType.Platform)
    }

    implementation("commons-io:commons-io:2.16.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1")
    implementation("io.sentry:sentry:7.10.0") {
        exclude("org.slf4j")
    }

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

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

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
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
