[![Build Status](https://travis-ci.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.svg)](https://travis-ci.com/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver)

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
