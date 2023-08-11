import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    `maven-publish`
    `signing`
}

group = "io.github.devzwy"
version = "2.1.3"

val sourceJar by tasks.registering(Jar::class) {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

tasks.withType<Javadoc> {
    options {
        encoding = "UTF-8"
    }
}

val javadocJar by tasks.creating(Jar::class) {
    classifier = "javadoc"
    from(tasks.getByName<Javadoc>("javadoc"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
//            from(components["kotlin"])

            groupId = "io.github.devzwy"
            artifactId = "mdhelper"
            version = project.version as String?

            // 发布 Javadoc JAR
            artifact(javadocJar)
            artifact(sourceJar)

            pom {
                name.set("mdhelper")
                description.set("明道云工具类")
                url.set("https://github.com/devzwy/mdhelper")

                scm {
                    url.set("https://github.com/devzwy/mdhelper.git")  // 添加 SCM URL
                    connection.set("scm:git:https://github.com/devzwy/mdhelper.git")
                    developerConnection.set("scm:git:https://github.com/devzwy/mdhelper.git")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("dev_zwy")
                        name.set("devzwy")
                        email.set("dev_zwy@aliyun.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("mavenUser") as String? ?: System.getenv("MAVEN_USERNAME")
                password = project.findProperty("mavenPassword") as String? ?: System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

repositories {
    //https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
    //https://oss.sonatype.org/content/repositories/releases/
    maven { url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") }
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

configure<SigningExtension> {
    sign(publishing.publications["mavenJava"])
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