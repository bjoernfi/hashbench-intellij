// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

version = "1.0.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.2")
        instrumentationTools()

        // caution: unfortunately, plugin.xml needs to be updated as well
        bundledPlugins("com.intellij.java")
    }

    implementation("javax.inject:javax.inject:1")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.76")

    // @NotNull
    implementation("org.jetbrains:annotations:24.0.0")
}

intellijPlatform {
    pluginConfiguration {
        id = "hashbench"
        name = "hashbench"
        description = ""

        ideaVersion {
            sinceBuild = "232"

            // compatibility with all future versions
            untilBuild = provider { null }
        }
    }

    signing {
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    runIde {
        systemProperties.put("idea.log.debug.categories", "#hashbench")
    }
}
