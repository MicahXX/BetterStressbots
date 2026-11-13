subprojects {
    apply(plugin = "java")
    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
    group = "me.micahcode"
    version = rootProject.version

    if (name != "core") {
        configure<JavaPluginExtension> {
            sourceSets.named("main") {
                java.srcDir(rootProject.file("nms-common/src/main/java"))
            }
        }
    }
}

tasks.register<Sync>("buildAll") {
    group = "build"
    description = "Build all version jars and collect in build/dist"

    dependsOn(":v1_21:assemble", ":v1_21_11:assemble", ":v26.1.x:assemble", ":v26.2:assemble", ":v26.3:assemble")

    from(project(":v1_21").layout.buildDirectory.dir("libs")) {
        include("*-reobf.jar")
    }
    from(project(":v1_21_11").layout.buildDirectory.dir("libs")) {
        include("*-reobf.jar")
    }
    from(project(":v26.1.x").layout.buildDirectory.dir("libs")) {
        include("BetterStresstestbots-*.jar")
    }
    from(project(":v26.2").layout.buildDirectory.dir("libs")) {
        include("BetterStresstestbots-*.jar")
    }
    from(project(":v26.3").layout.buildDirectory.dir("libs")) {
        include("BetterStresstestbots-*.jar")
    }

    into(layout.buildDirectory.dir("dist"))

    doLast {
        println("Jars collected in build/dist/ yipie (1.21 is for 1.21 to 1.21.4 and rest of 1.21 is for 1.21.11)")
    }
}
