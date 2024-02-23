import org.gradle.jvm.tasks.Jar


repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "deblugger.me"
version = "1.0-SNAPSHOT"


dependencies {
    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")



    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}


val fatJar = task("fatJar", type = Jar::class) {
    manifest {
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "deblugger.me.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
