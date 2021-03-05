ThisBuild / scalaVersion     := "2.13.5"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "io.github.vigoo"
ThisBuild / organizationName := "vigoo"

lazy val root = (project in file("."))
  .settings(
    name                := "kubectl-lp",
    libraryDependencies ++= Seq(
      "dev.zio"                       %% "zio"                      % "1.0.4-2",
      "dev.zio"                       %% "zio-logging"              % "0.5.7",
      "dev.zio"                       %% "zio-logging-slf4j-bridge" % "0.5.7",
      "io.github.vigoo"               %% "clipp-zio"                % "0.5.0",
      "com.coralogix"                 %% "zio-k8s-client"           % "0.6.0",
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio"   % "3.1.7",
      "com.softwaremill.sttp.client3" %% "slf4j-backend"            % "3.1.7",
      "io.circe"                      %% "circe-yaml"               % "0.13.1",
      "io.circe"                      %% "circe-core"               % "0.13.0",
      "io.circe"                      %% "circe-generic"            % "0.13.0",
      "de.vandermeer"                  % "asciitable"               % "0.3.2"
    ),
    Compile / mainClass := Some("com.ziverge.kubectlplugin.Main"),
    nativeImageVersion  := "20.3.0",
    nativeImageOptions ++= Seq(
      "--initialize-at-build-time=org.slf4j",
      "--initialize-at-build-time=scala.Predef$",
      "--initialize-at-build-time=scala.Symbol$",
      "--initialize-at-build-time=scala.collection",
      "--initialize-at-build-time=scala.reflect",
      "--initialize-at-build-time=scala.package$",
      "--initialize-at-build-time=scala.math",
      "--enable-https",
      "--no-fallback",
      "--allow-incomplete-classpath",
      "--report-unsupported-elements-at-runtime",
      "--install-exit-handlers",
      "-H:+ReportExceptionStackTraces",
      "-H:+AllowVMInspection",
      "-H:JNIConfigurationFiles=../../src/native-image-configs/jni-config.json",
      "-H:ReflectionConfigurationFiles=../../src/native-image-configs/reflect-config.json",
      "-H:DynamicProxyConfigurationFiles=../../src/native-image-configs/proxy-config.json",
      "-H:ResourceConfigurationFiles=../../src/native-image-configs/resource-config.json"
    )
  )
  .enablePlugins(NativeImagePlugin)
