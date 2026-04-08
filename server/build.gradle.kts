/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.asciidoctor.convert)
    checkstyle
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

tasks.javadoc {

    title = "${properties["appName"]} server ${properties["version"]}"

    source = sourceSets.main.get().allJava
    classpath = sourceSets.main.get().compileClasspath

    destinationDir = rootProject.projectDir.resolve("docs/api/server")

    exclude("**/test/**")

    with(options as StandardJavadocDocletOptions) {
        windowTitle = docTitle
        memberLevel = JavadocMemberLevel.PROTECTED
        isLinkSource = true
        isAuthor = false
        links(
            "https://docs.oracle.com/en/java/javase/${libs.versions.java.get()}/docs/api/",
            "https://docs.spring.io/spring-framework/docs/current/javadoc-api/",
            "https://docs.spring.io/spring-boot/api/java/",
            "https://docs.spring.io/spring-hateoas/docs/current/api/",
            "https://docs.spring.io/spring-data/commons/docs/current/api/",
            "https://docs.spring.io/spring-data/data-jpa/docs/current/api/",
            "https://docs.spring.io/spring-security/site/docs/current/api/",
            "https://docs.jboss.org/hibernate/orm/current/javadocs/",
            "https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-core/latest/",
            "https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/latest/",
            "https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/latest/",
            "https://javadoc.io/doc/com.fasterxml.jackson.datatype/jackson-datatype-jdk8/latest/",
            "https://javadoc.io/doc/com.fasterxml.jackson.datatype/jackson-datatype-jsr310/latest/"
        )
        addBooleanOption("html5", true)
        addStringOption("Xdoclint:none", "-quiet")
        encoding = "UTF-8"
        charSet = "UTF-8"
        docEncoding = "UTF-8"
    }

    isFailOnError = true
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.spring.boot.oauth2.resource.server)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.web)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(libs.thymeleaf.spring.security)
    runtimeOnly(libs.h2)
    annotationProcessor(libs.spring.boot.configuration.processor)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.restdocs.mockmvc)
    testImplementation(libs.spring.security.test)
    testRuntimeOnly(libs.junit.platform)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

checkstyle {
    toolVersion = "13.3.0"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

tasks.register("checkstyle", Checkstyle::class) {
    source("src/main/java")
    include("**/*.java")
    classpath = files()
}

tasks.named("check").configure {
    dependsOn("checkstyle")
}
