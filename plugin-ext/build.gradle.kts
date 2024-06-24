import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.intellij.platform.module")
    id("java")
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(project(":plugin-base"))
    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion")
        )
        bundledPlugins("Git4Idea")
        instrumentationTools()
        testFramework(TestFrameworkType.Bundled)
    }
}

intellijPlatform {
    buildSearchableOptions = false
}
