[![Build Status](https://travis-ci.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.svg)](https://travis-ci.com/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=coverage)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=bugs)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)

# Document Archiver

We all store different kind of documents (office documents, miscellaneous types of PDFs, Images, Audio-Files, etc.) on our systems.

The challenge here is to find a consistent approach which will help us to retrieve the documents later in a fast and easy way.

Traditionally many people tend to establish some kind of folder structure on their storage space, which will then hold documents by a topic, a year, or whatever seems to fit.
Anyhow, this approach often leads to a clutter of heterogeneous folder arrangements (over time one finds that another subfolder is needed at some place - and already consistency is broken).
It may also lead to duplicates (Should one store a document regarding a car insurance in a folder "car" or in a folder "insurances"? Maintaining a copy in both places is not really a good idea.).

This application is inspired by the project "PDF Archiver" (https://github.com/PDF-Archiver/PDF-Archiver), which utilizes a consistent way to store PDF files in a defined folder structure and by using a fixed file naming pattern.
Document Archiver picks up this core idea and makes it available for all document types and on all mayor platforms. Thanks to Julian Kahnert for agreeing to build on his great work!

### Convention

Documents will be archived in this manner:
```
└── ~/Documents/DocumentArchiver
    ├── pdfs
    │   ├── 2020
    │   │   ├── 2020-01-05--invoice__car_insurance.pdf
    │   │   └── 2020-07-01--invoice__house_insurance.pdf
    │   └── 2021
    │       └── 2021-01-05--invoice__car_insurance.pdf
    └── images
        ├── 2020
        │   ├── 2020-01-20-00-03-17--cake__birthday_stephan.jpg
        │   ├── 2020-01-20-00-03-54--cake__birthday_stephan.jpg
        │   └── 2020-03-16-15-32-23--party__birthday_natalia.jpg
        └── 2021
            └── 2021-01-20-00-02-42--party__birthday_stephan.jpg
```
* **Date\[-Time\]:** `yyyy-MM-dd` or `yyyy-MM-dd-HH-mm-ss` Date (and - if requested - time) of the document content.
* **Description:** `--invoice` Meaningful description of the document.
* **Tags:** `__car_insurance` Tags which will help you find the document in your archive.

## Get it running

```
git clone https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.git

cd com.sophisticatedapps.archiving.document-archiver

mvn clean package -DskipTests

cd target/

java -jar document-archiver-1.0-0-with-dependencies.jar
```
### \*tada\*

If you want to pass a certain file or a folder, you can run
```
java -jar document-archiver-1.0-0-with-dependencies.jar #path-to-file-or-folder#
```
## Quick Start
Have a look at the Wiki for a Quick Start manual:
https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/wiki/Quick-Start

***

Copyright 2021 by Stephan Sann

Application icon made by [Freepik](https://www.freepik.com "Freepik") from [www.flaticon.com](https://www.flaticon.com/ "Flaticon")
