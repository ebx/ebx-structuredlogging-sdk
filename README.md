[![Maven Central](https://img.shields.io/maven-central/v/com.echobox/ebx-structuredlogging-sdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.echobox%22%20AND%20a:%22ebx-structuredlogging-sdk%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/ebx/ebx-structuredlogging-sdk/master/LICENSE) [![Build Status](https://travis-ci.org/ebx/ebx-structuredlogging-sdk.svg?branch=dev)](https://travis-ci.org/ebx/ebx-structuredlogging-sdk)
# ebx-structuredlogging-sdk

Logging is a critical pillar of service observability. Occasionally it's desirable to
include structured information and depending on the logging solution these can then be
more easily graphed, analysed etc, for example with 
[loggly](https://www.loggly.com/blog/introducing-support-for-percentiles-and-other-statistics/).
  
This library intends to make including such structured logging as easy as possible by allowing
structured arguments to be included in `ch.qos.logback.classic.net.SyslogAppender`.
 
## How to use

1. Include this sdk (assuming maven):

```
<dependency>
  <groupId>com.echobox</groupId>
  <artifactId>ebx-structuredlogging-sdk</artifactId>
  <version>1.0.1</version>
</dependency>
```

2.  Update the relevant project `logback.xml` file to include the extended `SyslogAppender` config:

```
<!-- Get an optional threadid, tid, value for logging -->
<conversionRule conversionWord="tid" converterClass="com.echobox.logging.ThreadIdConverter" />

<!-- Congiure the structured SyslogAppender -->
<appender name="SYSLOG" class="com.echobox.logging.SyslogAppenderWithAppendix">
<syslogHost>localhost</syslogHost>
<facility>USER</facility>
<suffixPattern>[%logger] %nopex</suffixPattern>
<appendixLayout
  class="net.logstash.logback.layout.LoggingEventCompositeJsonLayout">
  <providers>
    <pattern>
      <pattern>{ "threadId": "%tid-%thread", "message": "%msg", "exception":"%rEx{30}" }</pattern>
    </pattern>
    <arguments />
  </providers>
</appendixLayout>
</appender>
```

The pattern can be modified as required, see [here](http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout).

3. Start including `net.logstash.logback.argument.StructuredArguments` where required:

```
Map<String, Object> loggingMap = new HashMap<>();
loggingMap.put("executionTimeMS", endTimeMS - startTimeMS);
logger.debug("Completed execution in MS {}", StructuredArguments.entries(loggingMap));
```

or

```
logger.warn(...
```

etc.

## Getting in touch

* **[GitHub Issues](https://github.com/ebx/ebx-structuredlogging-sdk/issues/new)**: If you have ideas, bugs, 
or problems with our library, just open a new issue.

## Contributing

If you would like to get involved please follow the instructions 
[here](https://github.com/ebx/ebx-structuredlogging-sdk/tree/master/CONTRIBUTING.md)

## Releases

We use [semantic versioning](https://semver.org/).

All merges into DEV will automatically get released as a maven central snapshot, which can be easily
included in any downstream dependencies that always desire the latest changes (see above for 
'Most Up To Date' installation).

Each merge into the MASTER branch will automatically get released to Maven central and github 
releases, using the current library version. As such, following every merge to master, the version 
number of the dev branch should be incremented and will represent 'Work In Progress' towards the 
next release. 

Please use a merge (not rebase) commit when merging dev into master to perform the release.

To create a full release to Maven central please follow these steps:
1. Ensure the `CHANGELOG.md` is up to date with all the changes in the release, if not please raise 
a suitable PR into `DEV`. Typically the change log should be updated as we go.
3. Create a PR from `DEV` into `MASTER`. Ensure the version in the `pom.xml` is the 
correct version to be released. Merging this PR into `MASTER` will automatically create the maven 
and github releases. Please note that a release is final, it can not be undone/deleted/overwritten.
5. Once the public release has been successful create a final PR into `DEV` that contains an 
incremented `pom.xml` version to ensure the correct snapshot gets updated on subsequent merges
into `DEV`. This PR should also include:
    * An update to the `README.md` latest stable release version number.
    * A 'Work In Progress' entry for the next anticipated release in `CHANGELOG.md`.