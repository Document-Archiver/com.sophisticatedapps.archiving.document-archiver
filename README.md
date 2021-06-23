[![Build Status](https://travis-ci.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.svg)](https://travis-ci.com/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=coverage)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=bugs)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.sophisticatedapps.archiving%3Adocument-archiver&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.sophisticatedapps.archiving%3Adocument-archiver)
[![Known Vulnerabilities](https://snyk.io/test/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/badge.svg)](https://snyk.io/test/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver)
[![Maintainability](https://api.codeclimate.com/v1/badges/a25879f38c9d39120a39/maintainability)](https://codeclimate.com/github/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/maintainability)

# Document Archiver

How can Document Archiver help you to get a handle on your multitude of documents with ease? Find out on
[this project's website](https://document-archiver.github.io/):

[![Screenshot website](https://user-images.githubusercontent.com/9678681/117995725-d7d15b00-b341-11eb-813a-46d8579d778a.png)](https://document-archiver.github.io/)

## How to get it

There are four alternative ways to get Document Archiver.

### Option A - Download an installer package (The Lazy Man's Way 🤷‍)

Complete package including Java runtime environment - "all singing and dancing".
For those who would like to install the application on their system and don't want to deal with things like setting up
Java or executing commands on the command line.

Depending on your system, download one of the following files:

**[\[ INSTALLER - LINUX \]](https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/releases/download/v2.2.0/DocumentArchiver_unix_2_2_0.sh)
-or-
[\[ INSTALLER - MACOS \]](https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/releases/download/v2.2.0/DocumentArchiver_macos_2_2_0.dmg)
-or-
[\[ INSTALLER - WINDOWS \]](https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/releases/download/v2.2.0/DocumentArchiver_windows-x64_2_2_0.exe)**

Note:  
The Windows installer package is not code-signed (Why? Just because the required code signing certificates are expensive
and Document Archiver does not yield any profits.).
Anyhow it is safe like the both other packages. You just have to tell Windows to keep the downloaded file (in the
appearing dialog choose "Show more -> Keep anyway") and unblock it afterwards like described
[here](https://winaero.com/how-to-unblock-files-downloaded-from-internet-in-windows-10/).  
The macOS installer package is code-signed and notarized by Apple; Linux doesn't check for code-signing.

### Option B - Download the last release version ZIP file (quick and easy 😌)

If you already have Java on your system, don't feel like installing and don't fear the command line, you can download
one of the following ZIP files (and furthermore save some MBs on your storage).

Depending on your system, download one of the following files:

**[\[ ZIP-FILE - LINUX \]](https://repository.sophisticatedapps.com/releases/com/sophisticatedapps/archiving/document-archiver/2.2.0/document-archiver-2.2.0-linux.zip)
-or-
[\[ ZIP-FILE - MACOS \]](https://repository.sophisticatedapps.com/releases/com/sophisticatedapps/archiving/document-archiver/2.2.0/document-archiver-2.2.0-mac.zip)
-or-
[\[ ZIP-FILE - WINDOWS \]](https://repository.sophisticatedapps.com/releases/com/sophisticatedapps/archiving/document-archiver/2.2.0/document-archiver-2.2.0-win.zip)**

**Startup**

Linux
```
unzip document-archiver-2.2.0-linux.zip
cd document-archiver-2.2.0-linux/
./DocumentArchiver.sh
```
macOS
```
unzip document-archiver-2.2.0-mac.zip
cd document-archiver-2.2.0-mac/
./DocumentArchiver.sh
```
Windows
```
:: Extract ZIP file (for example with Windows Explorer) ::
cd document-archiver-2.2.0-win/
DocumentArchiver.bat
```

### Option C - Download the latest SNAPSHOT release (get the latest and greatest ✨)

The latest SNAPSHOT releases can be downloaded here:

**[\[ SNAPSHOTs repository \]](https://repository.sophisticatedapps.com/snapshots/com/sophisticatedapps/archiving/document-archiver/2.3.0-SNAPSHOT)**

Select the ZIP file suitable for your system ("linux", "mac" or "win")

**Startup (example - version and timestamp will vary)**

Linux
```
unzip document-archiver-X.Y.Z-yyyyMMdd.HHmmss-n-linux.zip
cd document-archiver-X.Y.Z-SNAPSHOT-linux/
./DocumentArchiver.sh
```
macOS
```
unzip document-archiver-X.Y.Z-yyyyMMdd.HHmmss-n-mac.zip
cd document-archiver-X.Y.Z-SNAPSHOT-mac/
./DocumentArchiver.sh
```
Windows
```
:: Extract ZIP file (for example with Windows Explorer) ::
cd document-archiver-X.Y.Z-SNAPSHOT-win/
DocumentArchiver.bat
```

### Option D - Build it yourself (the nerdy way 🤓)

```
git clone https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver.git

cd com.sophisticatedapps.archiving.document-archiver

mvn clean package -DskipTests

cd target/
```
**Startup (example - version will vary)**

Linux & macOS
```
unzip document-archiver-X.Y.Z-SNAPSHOT-with-dependencies.zip
cd document-archiver-X.Y.Z-SNAPSHOT-with-dependencies/
./DocumentArchiver.sh
```
Windows
```
:: Extract ZIP file (for example with Windows Explorer) ::
cd document-archiver-X.Y.Z-SNAPSHOT-with-dependencies/
DocumentArchiver.bat
```

## Quick Start
Have a look at the Wiki for a Quick Start manual:
https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver/wiki/Quick-Start

---

Copyright 2021 by Stephan Sann

Document Archiver makes grateful use of these libraries:
- **PDFViewerFX** ([Dansoftowner/PDFViewerFX](https://github.com/Dansoftowner/PDFViewerFX "Dansoftowner/PDFViewerFX"))
- **jSystemThemeDetector** ([Dansoftowner/jSystemThemeDetector](https://github.com/Dansoftowner/jSystemThemeDetector "Dansoftowner/jSystemThemeDetector"))
- **Restart4j** ([Dansoftowner/Restart4j](https://github.com/Dansoftowner/Restart4j "Dansoftowner/Restart4j"))
- **Apache POI** ([poi.apache.org](https://poi.apache.org "poi.apache.org"))
- **Mammoth** ([mwilliamson/java-mammoth](https://github.com/mwilliamson/java-mammoth "mwilliamson/java-mammoth"))
- **AppDirs** ([harawata/appdirs](https://github.com/harawata/appdirs "harawata/appdirs"))
- **Version Compare** ([G00fY2/version-compare](https://github.com/G00fY2/version-compare "G00fY2/version-compare"))
- **GestureFX** ([tom91136/GestureFX](https://github.com/tom91136/GestureFX "tom91136/GestureFX"))

Application icons made by [Freepik](https://www.freepik.com "Freepik") from [www.flaticon.com](https://www.flaticon.com/ "Flaticon")
