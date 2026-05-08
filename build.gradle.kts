plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "id.ac.ui.cs.advprog.yomu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/release")
    }
}


dependencies {
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.1.1"))
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    implementation("id.ac.ui.cs.advprog.yomu:shared-lib:0.0.1-SNAPSHOT") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-security")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-amqp")
    }

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

