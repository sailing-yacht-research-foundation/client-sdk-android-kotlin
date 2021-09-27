# SYRF-ClientSDK-Android-Java
The SYRF Client SDK on Android simplifies tracker and race management for mobile app developers.

# Documentation generation

Project is using dokka for documentation auto generation.
Run below command when you want to update documentation for all library modules.

```groovy
./gradlew dokkaHtmlMultiModule
```

# Maven central distribution

The maven central distribution have been setup using the tutorial [here](https://proandroiddev.com/publishing-android-libraries-to-mavencentral-in-2021-8ac9975c3e52). Please check it out if you find any difficult with below steps.

# Setup

- The first step is generate and upload the gpg key for distribution. Please follow this link for
more detail https://central.sonatype.org/publish/requirements/gpg/

- When you already done with gpg step. Please update your local.properties adding below lines

```groovy
# For Maven central
# The last eight digits of gpg key
signing.keyId=your_gpg_key_id
# The password of gpg key
signing.password=your_gpg_key_password
# Path to exported gpg key
signing.key=/Path/to/your/gpg_key.gpg
# Please dont update below values
sonatypeStagingProfileId=27d283eebe1a26
ossrhUsername=jweisbaum
ossrhPassword=SailYachtRS2021!
```

# Distribution

- Update the module version: increase PUBLISH_VERSION in module gradle file.
Notes: please dont change PUBLISH_GROUP_ID and PUBLISH_ARTIFACT_ID values

- Execute the following command to start publication:

```groovy
./gradlew syrf-authentication:publishReleasePublicationToSonatypeRepository
./gradlew syrf-geospatial:publishReleasePublicationToSonatypeRepository
./gradlew syrf-location:publishReleasePublicationToSonatypeRepository
./gradlew syrf-time:publishReleasePublicationToSonatypeRepository
```

- After publish process is finished. Please open this url https://s01.oss.sonatype.org/#welcome
and login with credential: jweisbaum/SailYachtRS2021! -> Open Staging repositories -> Select just uploaded artifact -> Close -> Release\
The time this process takes can vary a bit. If you get lucky, your artifact will show up on MavenCentral in 10–15 minutes, but it could also take an hour or more in other cases. You can check whether your artifact is available by going to https://repo1.maven.org/maven2/io/syrf/ and browsing for it.

