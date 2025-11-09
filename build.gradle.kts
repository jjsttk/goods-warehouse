plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.jjsttk"
version = "0.0.1-SNAPSHOT"
description = "goods-warehouse"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.projectreactor:reactor-core:3.6.3")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.2")
    implementation ("org.mapstruct:mapstruct:1.6.2")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.6.2")
    implementation ("org.instancio:instancio-junit:5.0.2")
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")


    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty ("spring.profiles.active", "test")
}
