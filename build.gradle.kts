plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "5.1.0.4882"
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
    implementation("id.ac.ui.cs.advprog.yomu:shared-lib:0.0.1-SNAPSHOT")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // gRPC
    implementation("io.grpc:grpc-netty:1.63.0")
    implementation("net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "yomu-app-api-gateway")
        // property("sonar.organization", "your-organization-key") // Uncomment if using SonarCloud
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "https://sonarcloud.io")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
    }
}
