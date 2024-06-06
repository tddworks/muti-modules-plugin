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
        bundledPlugins("com.intellij.java")
        instrumentationTools()
    }
}

intellijPlatform {
    buildSearchableOptions = false
}
