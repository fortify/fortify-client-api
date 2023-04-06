
<!-- START-INCLUDE:repo-usage.md -->

# Fortify Client API Libraries - Usage Instructions

As mentioned in [README.md](README.md), `fortify-client-api` is intended to be used only by Fortify-developed integrations; use by 3<sup>rd</sup>-parties is neither endorsed not recommended, and such use will not be supported in any way.

## API Documentation

Other than the generic usage instructions provided in this document, and potentially some JavaDoc provided with the source code, this project does not provide any documentation on project architecture, API usage, etcetera. Examples on how to use the libraries can be found in the various utilities that utilize `fortify-client-api`, as listed below. Note that different projects may be using older releases of `fortify-client-api`, which may be significantly different from the latest release.

* **Sample Projects using fortify-client-api**
  * https://github.com/fortify-ps/FortifyBugTrackerUtility
  * https://github.com/fortify-ps/FortifySyncFoDToSSC
  * https://github.com/fortify-ps/fortify-integration-maven-webinspect 

## Overview

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

## Maven & Gradle Artifacts
The Maven artifacts for this project are automatically deployed to the following Maven repositories:

* **Maven Repositories**
    * **Releases**: https://repo1.maven.org/maven2/ 
    * **Snapshots**: https://s01.oss.sonatype.org/content/repositories/snapshots/

The published pom.xml file for fortify-client-api-bom provides a dependencyManagement section that can be imported to declare the correct dependency versions. The following examples show how to configure Gradle or Maven to use the `client-api-ssc` project provided by fortify-client-api. Obviously, these examples need to be adjusted according to:

* The version of fortify-client-api to be used
* Specific module(s) to be used from fortify-client-api 

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

<!-- END-INCLUDE:repo-usage.md -->


---

*[This document was auto-generated from USAGE.template.md; do not edit by hand](https://github.com/fortify/shared-doc-resources/blob/main/USAGE.md)*
