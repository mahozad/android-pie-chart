For successful login, go to the url replied in Sonatype Jira ticket of the corresponding repository ([currently here](https://s01.oss.sonatype.org/)).  
The username and password are the same as the Sonatype Jira account.

- *ir.mahozad* repository [ticket](https://issues.sonatype.org/browse/OSSRH-69101)
- *io.github.mahozad* repository [ticket](https://issues.sonatype.org/browse/OSSRH-69099)

The Sonatype Jira account details and the gpg properties and its secret file
(required here for publishing artifacts) are stored in a private Git repository.

The required properties for publishing tasks can either be declared in an ignored
*local.properties* file or specified as environment variables.  
Refer to [this script](../scripts/publish.gradle) for required values.

To publish a new version:
  1. Run the Gradle task
     *publish<NAME_OF_THE_PUBLISH_DEFINED_IN_BUILD_SCRIPT>PublicationToSonatypeRepository*
      on the desired project (module) to stage it
  2. Run the Gradle task
     *closeAndReleaseSonatypeStagingRepository* on the root project
     or visit the sonatype Web app as described in the PDF to release it

Example:
```shell
gradlew :piechart:publishPieChartReleaseForMavenPublicationToSonatypeRepository
gradlew closeAndReleaseSonatypeStagingRepository
```
