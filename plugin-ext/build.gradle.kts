plugins {
    id("org.jetbrains.intellij.platform.module") version "2.0.0-beta4"
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))
        bundledPlugins("com.intellij.java")
        localPlugin(project(":plugin-base"))
        instrumentationTools()
    }
}

intellijPlatform {
    buildSearchableOptions = false
}
