import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

project {

    buildType(Build)

    params {
        password("huhu", "credentialsJSON:acba4d4c-7bb1-4252-bd23-63643639c1b6", label = "qwewqeqw", display = ParameterDisplay.HIDDEN)
    }
}

object Build : BuildType({
    name = "Build"

    params {
        param("DockerImagePostfix", "")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        dockerCommand {
            commandType = build {
                source = file {
                    path = "/"
                }
                namesAndTags = "myName-%DockerImagePostfix%:latest"
                commandArgs = "--pull"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        pullRequests {
            provider = github {
                authType = token {
                    token = "credentialsJSON:f8fd84c2-23fa-47e8-b996-060fdcb5df55"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
        commitStatusPublisher {
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:f8fd84c2-23fa-47e8-b996-060fdcb5df55"
                }
            }
        }
    }
})
