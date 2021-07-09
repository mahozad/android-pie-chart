See this good template repository for Android (specifically, how it configures publishing):
https://github.com/cortinico/kotlin-android-template/blob/master/buildSrc/src/main/kotlin/publish.gradle.kts

For successful login, go to the url replied in Sonatype Jira ticket of the corresponding repository ([currently here](https://s01.oss.sonatype.org/)).  
The username and password are the same as the Sonatype Jira account.

- *ir.mahozad* repository [ticket](https://issues.sonatype.org/browse/OSSRH-69101)
- *io.github.mahozad* repository [ticket](https://issues.sonatype.org/browse/OSSRH-69099)

The Sonatype Jira account details and the gpg properties and its secret file
and also the GitHub personal access token (which can be regenerated easily in GitHub, anyway)
required here for publishing artifacts, are stored in one of my private GitHub repositories
and also in the *Secrets* section of the library GitHub repository.

The required properties for publishing tasks can either be declared in an ignored
*local.properties* file or specified as environment variables.  
Refer to [this script](../scripts/publish.gradle) for required values.

To publish a new version:
  1. Run the *incrementVersion* task  
     Usage: ```gradlew incrementVersion [-P[mode=major|minor|patch]|[overrideVersion=x]]```
  2. Publish on Maven Central
     1. Run the Gradle task
     *publish<NAME_OF_THE_PUBLISH_DEFINED_IN_BUILD_SCRIPT>PublicationToSonatypeRepository*
      on the desired project (module) to stage it on Sonatype
     2. Run the Gradle task
        *closeAndReleaseSonatypeStagingRepository* on the root project
        or visit the sonatype Web app as described in the PDF to release it
  3. Run the Gradle task
     *publish<NAME_OF_THE_PUBLISH_DEFINED_IN_BUILD_SCRIPT>PublicationToGitHubPackagesRepository*
     on the desired project (module) to publish it on GitHub

Example:
```shell
gradlew :piechart:publishPieChartReleaseForMavenPublicationToSonatypeRepository
gradlew closeAndReleaseSonatypeStagingRepository
```

See the following pages for how to set up GitHub packages and automate publishing using GitHub actions:

https://proandroiddev.com/android-libraries-on-github-packages-21f135188d58
https://docs.github.com/en/actions/guides/about-continuous-integration
https://docs.github.com/en/actions/guides/publishing-java-packages-with-gradle#publishing-packages-to-github-packages
https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry









See these:
https://www.raywenderlich.com/19407406-continuous-delivery-for-android-using-github-actions
https://www.raywenderlich.com/10562143-continuous-integration-for-android
https://www.rallyhealth.com/coding/code-coverage-for-android-testing
https://blog.codecentric.de/en/2021/02/github-actions-pipeline/
https://stefma.medium.com/how-to-store-a-android-keystore-safely-on-github-actions-f0cef9413784
https://www.igorkromin.net/index.php/2017/04/26/base64-encode-or-decode-on-the-command-line-without-installing-extra-tools-on-linux-windows-or-macos/
https://about.codecov.io/blog/code-coverage-for-android-development-using-kotlin-jacoco-github-actions-and-codecov/

To publish a new version of the library do these:
MANUAL VERSION
1 clean the project (gradle clean task)
2 run all the tests and make sure all pass
3 update the changelog file (by looking and inspecting the commit history since last release)
4 run my custom gradle task that updates the version of the library
5 commit the changes with message "Increment ..."
6 create a git tag on the new commit with new version string
7 merge the master branch into *prod* branch
8 make sure to checkout and return to the master branch again
9 run gradle tasks to publish the new version to maven central and github packages
10 create a release in the github repository with the same changelog as those specified in the changelog file
AUTOMATED VERSION
1 clean the project (gradle clean task)
2 update the changelog file (by looking and inspecting the commit history since last release)
3 run my custom gradle task that updates the version of the library
4 commit the changes with message "Increment ..."
5 create a git tag on the new commit with new version string
6 push the commits to github
7 github action will...
run all the tests (https://github.com/marketplace/actions/android-emulator-runner)
merge the master branch into prod branch (https://github.com/marketplace/actions/update-git-branch)
publish the release to github packages and maven central
create a release with the same changelog in the github repository (https://github.com/marketplace/actions/release-please-action or https://github.com/softprops/action-gh-release or https://github.com/ncipollo/release-action)
create the changelog by writing a kotlin script and running it with https://github.com/marketplace/actions/setup-kotlin
generate the codecoverage and upload to codecov or whatever
generate lint and static analysis and so on for the workflow (https://singhajit.com/android-ci-cd-using-github-actions/)
8 update the local prod branch from github

also setup another workflow that runs tests whenever a new pull request is created (so we make sure that
the code that the contributor changed does not break our tests)
