import jdk.internal.dynalink.linker.LinkerServices.Implementation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
}

group = "cn.uexpo"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
    jcenter()
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.alibaba:fastjson:2.0.32")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}