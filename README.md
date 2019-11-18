# fortify-client-api
This project provides Java modules for working with various Fortify products.

As of version 5.4:

* Snapshot versions are provided on https://oss.jfrog.org/artifactory/oss-snapshot-local
* Release versions are provided on https://dl.bintray.com/fortify-ps/maven

For older versions:

* Snapshot versions are not available
* Release versions are provided on https://raw.githubusercontent.com/fortify-ps/FortifyMavenRepo/master

TODO: Verify instructions in this readme

## Configure dependent projects

The published pom.xml file for fortify-client-api provides a dependencyManagement 
section that can be imported to declare the correct dependency versions. The 
following sections provide examples on how to configure Gradle or Maven to use 
the `client-api-ssc` project provided by fortify-client-api. Obviously, these
examples need to be adjusted according to:

* The version of fortify-client-api to be used
* Specific module(s) to be used from fortify-client-api
 

### Gradle

TODO: Verify this

```groovy

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
				<version>version</version>
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

## Version Management

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

## Build Environment

Whenever changes are pushed to the remote repository, a build will be triggered 
on travis-ci.org/travis-ci.com. These builds will automatically publish the current
project version:

* Release versions are published to https://dl.bintray.com/fortify-ps/maven
* Snapshot versions are published to https://oss.jfrog.org/artifactory/oss-snapshot-local
* Any other versions are only built, but not published

TODO: Publishing to Bintray/JFrog not yet implemented due to travis-ci issues


