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
  <version>1.0.0</version>
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