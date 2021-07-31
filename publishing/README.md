See this good template repository for Android (specifically, how it configures publishing):
https://github.com/cortinico/kotlin-android-template/blob/master/buildSrc/src/main/kotlin/publish.gradle.kts

For successful login, go to the url replied in Sonatype Jira ticket of the corresponding repository ([currently here](https://s01.oss.sonatype.org/)).  
The username and password are the same as the Sonatype Jira account.

- *ir.mahozad* repository [ticket](https://issues.sonatype.org/browse/OSSRH-69101)
- *io.github.mahozad* repository [ticket](https://issues.sonatype.org/browse/OSSRH-69099)

The Sonatype Jira account details and the gpg properties and its secret file
and also the GitHub personal access token (which can be regenerated easily in GitHub, anyway)
required here for publishing artifacts, and the signing key used to sign the apks,
are stored in one of my private GitHub repositories
and also in the *Secrets* section of the library GitHub repository.

The required properties for publishing and signing tasks can either be declared in a *.properties*
file (such as *local.properties* that is ignored by VCS and so not pushed to remote public repositories)
or specified as environment variables (specially required to run gradle scripts on CI servers such
as in GitHub workflows).

## To publish a new version automatically:
  1. Clean the project (just to make sure)
  2. Run all the tests and make sure all pass
  3. Update the CHANGELOG file (by looking and inspecting commit history since last release)
  4. Run the *incrementVersion* task  
     Usage: ```gradlew incrementVersion [-P[mode=major|minor|patch]|[overrideVersion=x]]```
  5. Commit the changes with message "Increment ..."     
  6. Create a Git tag on the new commit with the new version string
  7. Push the commits to GitHub
  8. Update the package descriptions of the new version in GitHub with the same content as those in the GitHub releases
  9. At some later time, fetch changes from GitHub to update the *prod* branch pointer

## To publish a new version manually:
  1. Clean the project (just to make sure)
  2. Run all the tests and make sure all pass
  3. Update the CHANGELOG file (by looking and inspecting commit history since last release)
  4. Run the *incrementVersion* task  
     Usage: ```gradlew incrementVersion [-P[mode=major|minor|patch]|[overrideVersion=x]]```
  5. Commit the changes with message "Increment ..."
  6. Create a Git tag on the new commit with the new version string
  7. Merge the master branch into *prod* branch
  8. Make sure to checkout and return to the master branch again
  9. Publish on Maven Central
     1. Run the Gradle task
     *publish<NAME_OF_THE_PUBLISH_DEFINED_IN_BUILD_SCRIPT>PublicationToSonatypeRepository*
      on the desired project (module) to stage it on Sonatype
     2. Run the Gradle task
        *closeAndReleaseSonatypeStagingRepository* on the root project
        or visit the sonatype Web app as described in the PDF to release it
  10. Publish on GitHub Packages by running the Gradle task
     *publish<NAME_OF_THE_PUBLISH_DEFINED_IN_BUILD_SCRIPT>PublicationToGitHubPackagesRepository*
     on the desired project (module) to publish it on GitHub
  11. Create a release in the GitHub repository with the same changelog as those in the CHANGELOG file (though a little different)
  12. Update the package descriptions of the new version in GitHub with the same changelog

Example releasing on Maven Central:
```shell
gradlew :piechart:publishPieChartReleaseForMavenPublicationToSonatypeRepository
gradlew closeAndReleaseSonatypeStagingRepository
```

See the following pages for how to set up GitHub packages, CI, code coverage, etc. using GitHub actions:
  - https://proandroiddev.com/android-libraries-on-github-packages-21f135188d58
  - https://singhajit.com/android-ci-cd-using-github-actions/
  - https://github.com/marketplace/actions/setup-kotlin
  - https://github.com/marketplace/actions/release-please-action
  - https://github.com/softprops/action-gh-release
  - https://github.com/ncipollo/release-action
  - https://github.com/marketplace/actions/android-emulator-runner
  - https://docs.github.com/en/actions/guides/about-continuous-integration
  - https://docs.github.com/en/actions/guides/publishing-java-packages-with-gradle#publishing-packages-to-github-packages
  - https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry
  - https://www.raywenderlich.com/19407406-continuous-delivery-for-android-using-github-actions
  - https://www.raywenderlich.com/10562143-continuous-integration-for-android
  - https://www.rallyhealth.com/coding/code-coverage-for-android-testing
  - https://blog.codecentric.de/en/2021/02/github-actions-pipeline/
  - https://stefma.medium.com/how-to-store-a-android-keystore-safely-on-github-actions-f0cef9413784
  - https://www.igorkromin.net/index.php/2017/04/26/base64-encode-or-decode-on-the-command-line-without-installing-extra-tools-on-linux-windows-or-macos/
  - https://about.codecov.io/blog/code-coverage-for-android-development-using-kotlin-jacoco-github-actions-and-codecov/


To sign the commits, I used the same gpg key as above.
Refer to https://stackoverflow.com/a/68557572 and to https://superuser.com/a/1428651
for how to set up commit signing.
To set up and start gpg-agent on Windows see https://stackoverflow.com/a/51407128
