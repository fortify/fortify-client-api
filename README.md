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

* **Automated builds**: https://travis-ci.com/github/fortify-ps/fortify-client-api
* **Maven Repositories**
  * **Snapshots**: https://oss.jfrog.org/artifactory/oss-snapshot-local
  * **Releases**: https://dl.bintray.com/fortify-ps/maven
  * **Releases (older versions)**: https://raw.githubusercontent.com/fortify-ps/FortifyMavenRepo/master
* **Sample Projects using fortify-client-api**
  * https://github.com/fortify-ps/FortifyBugTrackerUtility
  * https://github.com/fortify-ps/FortifySyncFoDToSSC
  * https://github.com/fortify-ps/fortify-integration-sonarqube
  * https://github.com/fortify-ps/fortify-integration-maven-webinspect 


## Usage

Please refer to the JavaDoc and sample projects listed in the previous sections
for details on how to use these client libraries. The remainder of this section
just describes the build configuration to be used to include the fortify-client-api
libraries into your own projects.

The published pom.xml file for fortify-client-api provides a dependencyManagement 
section that can be imported to declare the correct dependency versions. The 
following sections provide examples on how to configure Gradle or Maven to use 
the `client-api-ssc` project provided by fortify-client-api. Obviously, these
examples need to be adjusted according to:

* The version of fortify-client-api to be used
* Specific module(s) to be used from fortify-client-api
 

### Gradle

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
Gradle helper script to configure repositories; this script uses
slightly different repository settings than listed above, in order to also 
allow access to snapshot builds. See the https://github.com/fortify-ps/gradle-helpers 
project for more information and other Gradle helper scripts.


### Maven

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

## Information for developers

### Version Management

Version management for this project is handled by the 
[version-helper.gradle](https://github.com/fortify-ps/gradle-helpers/blob/1.0/version-helper.gradle)
Gradle helper script. 

This section provides a quick overview on how to manage branches and versions 
for fortify-client-api. Basically:

* Any development is done on a `<version>-SNAPSHOT` branch
* When a snapshot is ready to be released:
    * Changes from the snapshot branch are merged with master
    * A new `<version>` tag is created 

Main thing to note is that Gradle project version is based on these git branches 
and tags; the project source code does not define any version numbers.

### Git commands

* Clone the project: 
    * `git clone https://github.com/fortify-ps/fortify-client-api.git`
* Check out an existing snapshot branch: 
    * `git checkout <version>-SNAPSHOT`
* Allow `git push` to push to snapshot branch: 
    * `git config --global push.default current`

### Gradle commands

* Publish artifacts to local Maven repository to make them available for dependent projects:
    * `gradle publish`
* Show current project version: 
    * `gradle printProjectVersion`
* Start a new snapshot branch from master; Both these operations will check out a new branch named `<nextVersion>-SNAPSHOT`
    * Prompt for new version number: 
        * `gradle startSnapshotBranch`
    * Provide new version number on command line: 
        * `gradle startSnapshotBranch -PnextVersion=<major>.<minor>[.<patch>]`
 * Release new version from snapshot branch: 
     * `gradle releaseSnapshot`
 
 Note that all of these commands operate on the local Git repository only;
 you will need to push any changes to the remote repository where applicable.

### IDE's

Most of the modules in this project use Lombok. In order to have your IDE compile these
projects without errors, you may need to add Lombok support to your IDE. Please see
https://projectlombok.org/setup/overview for more information.


# Licensing
See [LICENSE.TXT](LICENSE.TXT)
 

