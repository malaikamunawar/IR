pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "IR"
include(":chaquopylib")
project(":chaquopylib").projectDir = File(settingsDir, "../modules/chaquopylib/chaquopylib")

include(":clientmodule")
project(":clientmodule").projectDir = File(settingsDir, "../modules/clientmodule/clientmodule")

include(":app")
include(":app")

