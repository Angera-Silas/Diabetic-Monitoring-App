pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
<<<<<<< HEAD


=======
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e
            }
        }
        mavenCentral()
        gradlePluginPortal()
<<<<<<< HEAD

=======
>>>>>>> f62f972d3243ae3a06a84796ac99140333b80c9e
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Diab"
include(":app")
 