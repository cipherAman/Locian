pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Locian"

include(":app")
include(":core:core-ui")
include(":core:core-model")
include(":core:core-common")
include(":inference")
include(":data:data-database")
include(":data:data-datastore")
include(":data:data-network")
include(":data:data-repository")
include(":domain:domain-inference")
include(":feature:feature-chat")
include(":feature:feature-models")
include(":feature:feature-settings")
include(":feature:feature-conversations")
