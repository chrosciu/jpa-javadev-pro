plugins {
    java
}

group = "pl.training"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val lombokVersion = "1.18.24"
val mapstructVersion = "1.4.2.Final"
val junitVersion = "5.8.2"

dependencies {
    implementation("org.hibernate:hibernate-core:6.0.0.Final")
    implementation("org.hibernate:hibernate-validator:7.0.4.Final")
    implementation("jakarta.el:jakarta.el-api:4.0.0")
    implementation("org.glassfish:jakarta.el:4.0.2")
    implementation("org.hibernate:hibernate-ehcache:6.0.0.Alpha7")
    implementation("javax.transaction:javax.transaction-api:1.3")
    implementation("org.postgresql:postgresql:42.3.4")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.javamoney:moneta:1.4.2")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.hamcrest:hamcrest:2.2")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
