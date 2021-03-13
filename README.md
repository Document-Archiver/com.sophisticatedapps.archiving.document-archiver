[![Build Status](https://travis-ci.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.svg)](https://travis-ci.com/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=coverage)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=bugs)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)

# Document Archiver

Inspired by the project "PDF Archiver" (https://github.com/PDF-Archiver/PDF-Archiver), I decided to create my own archiver application, which works on all mayor platforms und allows to archive documents of all kinds, not only PDFs.

## Get it running

```
git clone https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.git

cd com.sophisticatedapps.archiving.document-archiver

mvn clean compile assembly:single

cd target/

java -jar document-archiver-1.0-SNAPSHOT-jar-with-dependencies.jar
```
### \*tada\*

If you want to pass a certain file or a folder, you can run
```
java -jar document-archiver-1.0-SNAPSHOT-jar-with-dependencies.jar #path-to-file-or-folder#
```
## Quick Start
Have a look at the Wiki for a Quick Start manual:
https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/wiki/Quick-Start
