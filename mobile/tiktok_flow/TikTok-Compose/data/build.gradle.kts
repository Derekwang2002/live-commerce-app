plugins {
    id("plugin.android-common")
}

android {
    sourceSets {
        getByName("main") {
            assets.srcDir(file("${rootDir.parent}/resources"))
        }
    }
}

dependencies {
    CORE
}
