lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """carlist""",
    version := "1.0",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      ehcache,
      filters,
      javaWs,
      openId,
      guice,
      javaJpa,

      "com.google.inject" % "guice" % "5.1.0",
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",

      "mysql" % "mysql-connector-java" % "8.0.32",
      "org.hibernate" % "hibernate-core" % "6.1.7.Final",
      "org.projectlombok" % "lombok" % "1.18.20" % "provided",

      "software.amazon.awssdk" % "s3" % "2.16.59" exclude("com.fasterxml.jackson.core", "jackson-databind"),
      "org.simplejavamail" % "simple-java-mail" % "6.5.3",

      "co.elastic.clients" % "elasticsearch-java" % "8.7.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.0",

      "org.apache.commons" % "commons-lang3" % "3.12.0",
      "com.google.code.gson" % "gson" % "2.8.6",
      "org.jsoup" % "jsoup" % "1.7.3",

      "org.im4java" % "im4java" % "1.4.0",
      "net.coobird" % "thumbnailator" % "0.4.2",

      "commons-codec" % "commons-codec" % "1.15",
      "commons-io" % "commons-io" % "2.8.0",
      "org.projectlombok" % "lombok" % "1.18.20" % "provided",
      "org.jobrunr" % "jobrunr" % "1.3.1",
      "com.github.dfabulich" % "sitemapgen4j" % "1.1.2",

      "com.maxmind.geoip2" % "geoip2" % "2.9.0",
      "au.com.flyingkite" % "mobiledetect" % "1.1.1",
      "net.sf.uadetector" % "uadetector-resources" % "2014.04",
      "com.googlecode.libphonenumber" % "libphonenumber" % "8.12.32",

      "com.google.javascript" % "closure-compiler" % "v20210505",
      "com.yahoo.platform.yui" % "yuicompressor" % "2.4.8",
      "org.jasypt" % "jasypt" % "1.9.3",
      "org.apache.poi" % "poi" % "3.16",
      "org.apache.poi" % "poi-ooxml" % "3.16",
      "joda-time" % "joda-time" % "2.12.5",

      "org.seleniumhq.selenium" % "selenium-java" % "4.16.1",
      "io.github.bonigarcia" % "webdrivermanager" % "5.6.2",

      // Testing libraries for dealing with CompletionStage...
      "org.assertj" % "assertj-core" % "3.24.2" % Test,
      "org.awaitility" % "awaitility" % "4.2.0" % Test,
    ),
    javacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-parameters",
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      //"-Werror"
    ),
    // Make verbose tests
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    pipelineStages := Seq(digest, gzip),
    PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
  )
