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
  1. Publish on Maven Central
     1. Run the Gradle task
     *publish<NAME_OF_THE_PUBLISH_DEFINED_IN_BUILD_SCRIPT>PublicationToSonatypeRepository*
      on the desired project (module) to stage it on Sonatype
     2. Run the Gradle task
        *closeAndReleaseSonatypeStagingRepository* on the root project
        or visit the sonatype Web app as described in the PDF to release it
  2. Run the Gradle task
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
