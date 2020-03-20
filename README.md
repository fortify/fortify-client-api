# Fortify Client API libraries

The **fortify-client-api** project provides the following Java modules for working with various Fortify products:

* **client-api-fod** Client library for working with the Fortify on Demand (FoD) REST API
* **client-api-ssc** Client library for working with the Fortify Software Security Center (SSC) REST API
* **client-api-webinspect** Client library for working with the Fortify WebInspect REST API
* **client-api-wie** Client library for working with the Fortify WebInspect Enterprise (WIE) REST API

The following modules in this project do not contain any Fortify-specific functionality,
but provide common, low-level functionality that is used by the various client modules
listed above:

* **common-log** Low-level functionality related to logging
* **common-spring** Low-level functionality related to the Spring framework and Spring Expression Language
* **common-rest** Low-level functionality for invoking REST API's and handling JSON data.

### Related links

* **Branches**: https://github.com/fortify-ps/fortify-client-api/branches  
  Current development is usually done on latest snapshot branch, which may not be the default branch
* **Automated builds**: https://travis-ci.com/github/fortify-ps/fortify-client-api
* **Maven Repositories**
  * **Snapshots**: https://oss.jfrog.org/artifactory/oss-snapshot-local
    * **Artifacts**: https://oss.jfrog.org/artifactory/oss-snapshot-local/com/fortify/client/api/
  * **Releases**: https://dl.bintray.com/fortify-ps/maven
    * **Artifacts**: https://dl.bintray.com/fortify-ps/maven/com/fortify/client/api/
  * **Releases (older versions)**: https://raw.githubusercontent.com/fortify-ps/FortifyMavenRepo/master
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

The published pom.xml file for fortify-client-api provides a dependencyManagement 
section that can be imported to declare the correct dependency versions. The 
following examples show how to configure Gradle or Maven to use the `client-api-ssc` 
project provided by fortify-client-api. Obviously, these examples need to be adjusted 
according to:

* The version of fortify-client-api to be used
* Specific module(s) to be used from fortify-client-api 
 
Please refer to the sample projects listed in the [Related Links](#related-links) sections
for more examples on how to add the `fortify-client-api` libraries to your projects.

#### Gradle Example

```gradle

plugins {
    id 'io.spring.dependency-management' version '1.0.8.RELEASE'
}

repositories {
    jcenter()
    maven {
        url "https://dl.bintray.com/fortify-ps/maven"
    }
}

dependencyManagement {
	imports {
		mavenBom("com.fortify.client.api:fortify-client-api:<version>")
	}
}

dependencies {
   compile 'com.fortify.client.api:client-api-ssc'
}
```

Note that most projects in the fortify-ps organization use the
[repo-helper.gradle](https://github.com/fortify-ps/gradle-helpers/blob/1.0/repo-helper.gradle)
Gradle helper script to configure repositories; that script uses
slightly different repository settings than listed above in order to also 
allow access to snapshot builds. See the https://github.com/fortify-ps/gradle-helpers 
project for more information and other Gradle helper scripts.


#### Maven Example

```xml

	<repositories>
		<repository>
			<id>FortifyPSMavenRepo</id>
			<url>https://dl.bintray.com/fortify-ps/maven</url>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
			</snapshots>
		</repository>
	</repositories>
	
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.fortify.client.api</groupId>
				<artifactId>fortify-client-api</artifactId>
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

### Gradle

It is strongly recommended to build this project using the included Gradle Wrapper
scripts; using other Gradle versions may result in build errors and other issues.

The Gradle build uses various helper scripts from https://github.com/fortify-ps/gradle-helpers;
please refer to the documentation and comments in included scripts for more information. 

### Commonly used commands

All commands listed below use Linux/bash notation; adjust accordingly if you
are running on a different platform. All commands are to be executed from
the main project directory.

* `./gradlew tasks --all`: List all available tasks
* Build & publish:
  * `./gradlew clean build`: Clean and build the project
  * `./gradlew build`: Build the project without cleaning
  * `./gradlew publish`: Publish the project to the local Maven repository, for use by other local projects. Should usually only be done from a snapshot branch; see [Versioning](#versioning).
* Version management:
  * `./gradlew printProjectVersion`: Print the current version
  * `./gradlew startSnapshotBranch -PnextVersion=2.0`: Start a new snapshot branch for an upcoming `2.0` version
  * `./gradlew releaseSnapshot`: Merge the changes from the current branch to the master branch, and create release tag
* `./fortify-scan.sh`: Run a Fortify scan; requires Fortify SCA to be installed

Note that the version management tasks operate only on the local repository; you will need to manually
push any changes (including tags and branches) to the remote repository.

### Versioning

The various version-related Gradle tasks assume the following versioning methodology:

* The `master` branch is only used for creating tagged release versions
* A branch named `<version>-SNAPSHOT` contains the current snapshot state for the upcoming release
* Optionally, other branches can be used to develop individual features, perform bug fixes, ...
  * However, note that the Gradle build may be unable to identify a correct version number for the project
  * As such, only builds from tagged versions or from a `<version>-SNAPSHOT` branch should be published to a Maven repository

### Automated Builds & publishing

Travis-CI builds are automatically triggered when there is any change in the project repository,
for example due to pushing changes, or creating tags or branches. If applicable, build artifacts 
are automatically published to a Maven repository:

* When building a tagged version, the Gradle `bintrayUpload` task will be invoked to upload the release version to JFrog Bintray
* When building a branch named `<version>-SNAPSHOT`, the Gradle `artifactoryPublish` task will be invoked to publish a snapshot version to JFrog Artifactory
* No artifacts will be deployed for any other build, for example when Travis-CI builds the `master` branch

See the [Related Links](#related-links) section for the relevant Travis-CI, Bintray and Artifactory links.


# Licensing
See [LICENSE.TXT](LICENSE.TXT)

