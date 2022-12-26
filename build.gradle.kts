import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.0"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.21"
	kotlin("plugin.spring") version "1.7.21"
}

group = "mateusz.pawlowski"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}
dependencies {
	implementation("org.xerial:sqlite-jdbc:3.30.1")
	implementation("org.jetbrains.exposed:exposed-core:0.40.1")
	implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
	implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
	implementation ("org.springframework.boot:spring-boot-starter-hateoas")
	implementation ("io.github.microutils:kotlin-logging-jvm:2.0.11")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
