import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask

plugins {
    id("java")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.intellij.platform") version "2.0.0-beta6"
    id("org.jetbrains.intellij.platform.migration") version "2.0.0-beta6"
    id("org.jetbrains.changelog") version "2.2.0"
}

fun properties(key: String) = project.findProperty(key).toString()
fun environment(key: String) = providers.environmentVariable(key)

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    maven("https://cache-redirector.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-repository/snapshots")
    maven("https://cache-redirector.jetbrains.com/maven-central")
    mavenLocal()
    mavenCentral()
    intellijPlatform {
        // Repositories Extension
        defaultRepositories()
//        jetbrainsRuntime()
    }
}

allprojects {
    apply {
        plugin("org.jetbrains.kotlin.plugin.serialization")
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("co.touchlab:kermit:2.0.3")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0-M2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.0-M2")

        testRuntimeOnly("junit:junit:4.13.2")
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.0-M2")

        testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
        testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        testRuntimeOnly("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.3")
        testRuntimeOnly("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.1")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.0-M2")
    }
}

subprojects {
    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

kotlin {
    jvmToolchain(17)
}

// Fix issue - https://stackoverflow.com/questions/75694002/problem-with-ktor-client-in-intellij-idea-plugin-development
configurations.all {
    exclude("org.slf4j", "slf4j-api")
}

// Configure Gradle Changelog Plugin - Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set(properties("pluginVersion"))
    headerParserRegex.set("(.*)".toRegex())
    groups.set(emptyList())
    lineSeparator.set("\n")
}

dependencies {

    intellijPlatform {
        // Dependencies Extension
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion")
        )

        pluginModule(implementation(project(":plugin-base")))
        pluginModule(implementation(project(":plugin-ext")))

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}
// Configure Gradle IntelliJ Plugin - Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
    // Builds an index of UI components (searchable options) for the plugin. This task runs a headless IDE instance to collect all the available options provided by the plugin's Settings.
    // https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html#tasks
    buildSearchableOptions =
        providers.gradleProperty("enableBuildSearchableOptions").getOrElse("false")
            .toBoolean()
    // IntelliJ Platform Extension
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        ideaVersion {
            sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
            untilBuild.set(providers.gradleProperty("pluginUntilBuild"))
        }
    }
    publishing {
        channels.set(listOf("stable"))
        token.set(environment("PUBLISH_TOKEN"))
    }
    signing {
        certificateChain.set(environment("CERTIFICATE_CHAIN"))
        privateKey.set(environment("PRIVATE_KEY"))
        password.set(environment("PRIVATE_KEY_PASSWORD"))
    }

    verifyPlugin {
        ides {
            recommended()
        }
        failureLevel = setOf(
            VerifyPluginTask.FailureLevel.COMPATIBILITY_PROBLEMS,
            VerifyPluginTask.FailureLevel.NOT_DYNAMIC
        )
        teamCityOutputFormat = true
    }
}

tasks {

    runIde {
        // Default args for IDEA installation
        jvmArgs("-Xmx768m", "-XX:+UseG1GC", "-XX:SoftRefLRUPolicyMSPerMB=50")

        applyRunIdeSystemSettings()
    }
}

fun RunIdeTask.applyRunIdeSystemSettings() {
    // Disable plugin auto reloading. See `com.intellij.ide.plugins.DynamicPluginVfsListener`
    systemProperty("idea.auto.reload.plugins", false)
    // Enable NewUI
    systemProperty("ide.experimental.ui", true)
    // Don't show "Tip of the Day" at startup
    systemProperty("ide.show.tips.on.startup.default.value", false)

    systemProperty("idea.trust.all.projects", true)

    systemProperty("jb.consents.confirmation.enabled", false)
}

