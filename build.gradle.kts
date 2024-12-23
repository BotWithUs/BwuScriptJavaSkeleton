
plugins {
    id("java")
    `maven-publish`
}

group = "net.botwithus.debug"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        setUrl("https://nexus.botwithus.net/repository/maven-snapshots/")
    }
}

configurations {
    create("includeInJar") {
        this.isTransitive = false
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

dependencies {
    implementation("net.botwithus.rs3:botwithus-api:1.+")
    implementation("net.botwithus.xapi.public:api:1.+")
    "includeInJar"("net.botwithus.xapi.public:api:1.+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    implementation("com.google.code.gson:gson:2.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

val copyJar by tasks.register<Copy>("copyJar") {
    from("build/libs/")
    into("${System.getProperty("user.home")}\\BotWithUs\\scripts\\local\\")
    include("*.jar")
}

tasks.named<Jar>("jar") {
    from({
        configurations["includeInJar"].map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    finalizedBy(copyJar)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}