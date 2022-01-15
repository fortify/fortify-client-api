# Fortify Client API libraries

The `fortify-client-api` project provides the following Java modules for working with various Fortify products:

* `client-api-fod`: Client library for working with the Fortify on Demand (FoD) REST API
* `client-api-ssc`: Client library for working with the Fortify Software Security Center (SSC) REST API
* `client-api-webinspect`: Client library for working with the Fortify WebInspect REST API
* `client-api-wie`: Client library for working with the Fortify WebInspect Enterprise (WIE) REST API

The following modules in this project do not contain any Fortify-specific functionality,
but provide common, low-level functionality that is used by the various client modules
listed above:

* `common-log` Low-level functionality related to logging
* `common-spring` Low-level functionality related to the Spring framework and Spring Expression Language
* `common-rest` Low-level functionality for invoking REST API's and handling JSON data.

## Disclaimer

Please note that `fortify-client-api` is by no means meant to be an official Fortify client SDK. It is mainly used as a shared library by various Fortify-provided integration utilities. In particular, please note the following before considering using `fortify-client-api` in any application:

* Absolutely no support is provided for `fortify-client-api`
* There is absolutely no guarantee that any functionality provided by `fortify-client-api` actually works; functionality is only tested indirectly through the various integration utilities that utilize `fortify-client-api`
* `fortify-client-api` only covers a subset of the API's provided by the various Fortify products, as required by the various integration utilities
* New versions of `fortify-client-api` may introduce significant changes to the API without taking backward compatibility into account, and existing functionality may cease to exist; upgrading to a new version of `fortify-client-api` may require significant effort
* Absolutely no maintenance is being done on older versions of `fortify-client-api`
* Feature requests are not accepted
* Bug fixes may only be considered if a bug affects any of the Fortify-provided integration utilities


### Related links

* **Source code**: https://github.com/fortify-ps/fortify-client-api
* **Automated builds**: https://github.com/fortify-ps/fortify-client-api/actions
* **Maven Repositories**
  * **Releases**: https://repo1.maven.org/maven2/ 
  * **Snapshots**: https://s01.oss.sonatype.org/content/repositories/snapshots/
* **Sample Projects using fortify-client-api**
  * https://github.com/fortify-ps/FortifyBugTrackerUtility
  * https://github.com/fortify-ps/FortifySyncFoDToSSC
  * https://github.com/fortify-ps/fortify-integration-sonarqube
  * https://github.com/fortify-ps/fortify-integration-maven-webinspect 


## Usage

### API
Please refer to the JavaDoc and sample projects listed in the [Related Links](#related-links) section
for details on how to use these client libraries.

### Build System
The Maven artifacts for this project are automatically deployed to
the Maven repositories listed in the [Related Links](#related-links) section.

The published pom.xml file for fortify-client-api-bom provides a dependencyManagement 
section that can be imported to declare the correct dependency versions. The 
following examples show how to configure Gradle or Maven to use the `client-api-ssc` 
project provided by fortify-client-api. Obviously, these examples need to be adjusted 
according to:

* The version of fortify-client-api to be used
* Specific module(s) to be used from fortify-client-api 
 
Please refer to the sample projects listed in the [Related Links](#related-links) sections
for more examples on how to add the `fortify-client-api` libraries to your projects.

#### Gradle Example

```groovy

dependencies {
	implementation platform('com.fortify.client.api:fortify-client-api-bom:<version>')

   implementation 'com.fortify.client.api:client-api-ssc'
}
```

The configuration listed above will only allow access to release versions of this library.
Usually it is not recommended to depend on snapshot versions of this library, but if necessary
you can use the example below to access snapshot versions of this library. Note the additional
repository definition, and the `changing: true` property on `implementation`.

```groovy

repositories {
   maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
}

dependencies {
	implementation platform('com.fortify.client.api:fortify-client-api-bom:<version>')

   implementation('com.fortify.client.api:client-api-ssc', changing: true) 
}
```


#### Maven Example

```xml
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.fortify.client.api</groupId>
				<artifactId>fortify-client-api-bom</artifactId>
				<version>[version]</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
	
	<dependencies>
		<dependency>
			<groupId>com.fortify.client.api</groupId>
			<artifactId>client-api-ssc</artifactId>
		</dependency>
	</dependencies>
```


## Information for library developers

The following sections provide information that may be useful for developers of the 
`fortify-client-api` library.

### IDE's

Most of the modules in this project use Lombok. In order to have your IDE compile these
projects without errors, you may need to add Lombok support to your IDE. Please see
https://projectlombok.org/setup/overview for more information.

### Conventional commits & versioning

Versioning is handled automatically by [`release-please-action`](https://github.com/google-github-actions/release-please-action) based on [Conventional Commits](https://www.conventionalcommits.org/). Every commit to the `main`
branch should follow the Conventional Commits convention, for example:

* `fix: Some fix (#2)`
* `feat: New feature (#3)`
* `api: Some API change`
* `api!: Some breaking API change`

`release-please-action` invoked from the GitHub CI workflow generates pull requests containing updated `CHANGELOG.md` and `version.txt` files based on these commit messages. Merging the pull request will
result in a new release version being published.

The `build.gradle` script reads the version number of the last published release from `version.txt`,
by default this will result in a version named `x.y.z.SP-SNAPSHOT`, indicating that this is a snapshot
of an upcoming Service Pack release. Note the 'Service Pack' here just means 'the next version after the last
published release'. We never actually release service packs; once the 'Service Pack' is ready for release, 
a new version number will be generated by `release-please-action` based on the Conventional Commit
messages.

This default behavior can be modified by passing the `-PisReleaseVersion` property, in which case the `build.gradle` will generate a version named `x.y.z.RELEASE`. This should fit nicely with [Gradle semantics](https://docs.gradle.org/current/userguide/single_versions.html), which states that for example `1.0-RELEASE < 1.0-SP1`.

### Commonly used commands

All commands listed below use Linux/bash notation; adjust accordingly if you
are running on a different platform. All commands are to be executed from
the main project directory.

It is strongly recommended to build this project using the included Gradle Wrapper
scripts; using other Gradle versions may result in build errors and other issues.

* `./gradlew tasks --all`: List all available tasks
* Build & publish:
  * `./gradlew clean build`: Clean and build the project
  * `./gradlew build`: Build the project without cleaning
  * `./gradlew publish`: Publish this build as a snapshot version to the local Maven repository
  * `./gradlew publishToOSSRH`: Publish this build as a snapshot version to OSSRH; usually only done from a GitHub Actions workflow
  * `./gradlew publishToOSSRH closeOSSRHStagingRepository -PisReleaseVersion=true`: Publish this build as a release version to the OSSRH staging area; use this for first-time publishing to check release contents
  * `./gradlew publishToOSSRH closeAndReleaseOSSRHStagingRepository -PisReleaseVersion=true`: Publish this build as a release version to Maven Central; usually only done from a GitHub Actions workflow
  
All OSSRH-related tasks require the following Gradle properties to be set:

* `signingKey`: GPG secret key used to sign the artifacts
* `signingPassword`: Password for the GPG secret key
* `OSSRHUsername`: Sonatype OSSRH user name
* `OSSRHPassword`: Sonatype OSSRH password

These properties can be set on the command line, in a local `gradle.properties` file, or through environment variables named `ORG_GRADLE_PROJECT_<propertyName>`. For automated build pipelines, it is recommended to use a Sonatype OSSRH token rather than actual username and password.


### Automated Builds & publishing

A GitHub Actions `ci.yml` workflow is used to automatically build and publish both snapshot versions and release versions to OSSRH/Maven Central.

See the [Related Links](#related-links) section for the relevant links.


# Licensing
See [LICENSE.TXT](LICENSE.TXT)

