lazy val commonSettings = Seq(
  Compile / compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(compilerPlugin(Dependencies.kindProjector), compilerPlugin(Dependencies.betterMonadicFor))
      case _ => Seq.empty
    }
  },
  scalacOptions += {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) => "-Wconf:any:wv"
      case _ => "-Wconf:any:v"
    }
  },
  Test / fork := true,
  resolvers += Resolver.sonatypeRepo("releases"),
)

lazy val noPublishSettings =
  commonSettings ++ Seq(publish := {}, publishArtifact := false, publishTo := None, publish / skip := true)

lazy val publishSettings = commonSettings ++ Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  Test / publishArtifact := false
)

lazy val graalSettings = Seq(
  nativeImageVersion := "22.1.0",
  nativeImageJvm := "graalvm-java11",
  nativeImageOptions ++= Seq(
    "--verbose",
    "--no-server",
    "--no-fallback",
    "--enable-http",
    "--enable-https",
    "--enable-all-security-services",
    "--report-unsupported-elements-at-runtime",
    "--allow-incomplete-classpath",
    "-Djava.net.preferIPv4Stack=true",
    "-H:IncludeResources='.*'",
    "-H:+ReportExceptionStackTraces",
    "-H:+ReportUnsupportedElementsAtRuntime",
    "-H:TraceClassInitialization=true",
    "-H:+PrintClassInitialization",
    "-H:+RemoveSaturatedTypeFlows",
    "-H:+StackTrace",
    "-H:+JNI",
    "-H:-SpawnIsolates",
    "-H:-UseServiceLoaderFeature",
    "-H:ConfigurationFileDirectories=../../native-image/",
    "--install-exit-handlers",
    "--initialize-at-build-time=scala.runtime.Statics$VM",
    "--initialize-at-build-time=sun.instrument.InstrumentationImpl",
    "--initialize-at-build-time=scala.Symbol$",
    "--initialize-at-build-time=org.slf4j.impl.StaticLoggerBinder",
    "--initialize-at-build-time=io.odin.formatter.Formatter$",
    "--initialize-at-build-time=io.odin.formatter.options.ThrowableFormat$",
    "--initialize-at-build-time=org.slf4j.LoggerFactory",
    "--initialize-at-build-time=org.apache.kafka,net.jpountz",
    "--initialize-at-build-time=com.github.luben.zstd.ZstdInputStream",
    "--initialize-at-build-time=com.github.luben.zstd.ZstdOutputStream",
    "--initialize-at-build-time=com.sun.management.internal.Flag",
    "--initialize-at-build-time=com.sun.management.internal.OperatingSystemImpl",
    "--initialize-at-run-time=org.apache.kafka.common.security.authenticator.SaslClientAuthenticator",
    "--initialize-at-run-time=org.apache.kafka.common.security.oauthbearer.internals.expiring.ExpiringCredentialRefreshingLogin",
  )
)

Global / excludeLintKeys ++= Set(nativeImageVersion, nativeImageJvm)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(name := "Trace4Cats Components")
  .aggregate(common, agent, `agent-common`, `agent-kafka`, collector, `collector-common`, `collector-lite`)

lazy val `agent-common` = (project in file("modules/agent-common"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-agent-common",
    libraryDependencies ++= Seq(
      Dependencies.declineEffect,
      Dependencies.trace4catsAvroServer,
      Dependencies.trace4catsCore,
      Dependencies.trace4catsMeta,
    )
  )
  .dependsOn(common)

lazy val common = (project in file("modules/common"))
  .settings(publishSettings)
  .settings(name := "trace4cats-common", libraryDependencies ++= Seq(Dependencies.catsEffect, Dependencies.odin))

lazy val agent = (project in file("modules/agent"))
  .settings(noPublishSettings)
  .settings(graalSettings)
  .settings(name := "trace4cats-agent", libraryDependencies ++= Seq(Dependencies.trace4catsAvroExporter))
  .dependsOn(`agent-common`)
  .enablePlugins(NativeImagePlugin)

lazy val `agent-kafka` = (project in file("modules/agent-kafka"))
  .settings(noPublishSettings)
  .settings(graalSettings)
  .settings(
    name := "trace4cats-agent-kafka",
    libraryDependencies ++= Seq(Dependencies.trace4catsAvroKafkaExporter, Dependencies.graalKafkaClient)
  )
  .dependsOn(`agent-common`)
  .enablePlugins(NativeImagePlugin)

lazy val `collector-common` = (project in file("modules/collector-common"))
  .settings(publishSettings)
  .settings(
    name := "trace4cats-collector-common",
    libraryDependencies ++= Seq(
      Dependencies.circeGeneric,
      Dependencies.circeYaml,
      Dependencies.declineEffect,
      Dependencies.http4sJdkClient,
      Dependencies.trace4catsCore,
      Dependencies.trace4catsMeta,
      Dependencies.trace4catsAvroExporter,
      Dependencies.trace4catsAvroServer,
      Dependencies.trace4catsAvroKafkaExporter,
      Dependencies.trace4catsAvroKafkaConsumer,
      Dependencies.trace4catsJaegerThriftExporter,
      Dependencies.trace4catsDatadogHttpExporter,
      Dependencies.trace4catsOpentelemetryOltpHttpExporter,
      Dependencies.trace4catsStackdriverHttpExporter,
      Dependencies.trace4catsNewrelicHttpExporter,
      Dependencies.trace4catsZipkinHttpExporter,
      Dependencies.trace4catsTailSampling,
      Dependencies.trace4catsTailSamplingCacheStore,
      Dependencies.trace4catsTailSamplingRedisStore,
    )
  )
  .dependsOn(common)

lazy val collector = (project in file("modules/collector"))
  .settings(noPublishSettings)
  .settings(
    name := "trace4cats-collector",
    dockerRepository := Some("janstenpickle"),
    dockerUpdateLatest := true,
    dockerBaseImage := "adoptopenjdk/openjdk11:alpine-jre",
    dockerExposedPorts += 7777,
    dockerExposedUdpPorts += 7777,
    Docker / daemonUserUid := Some("9000"),
    Universal / javaOptions ++= Seq(
      "-Djava.net.preferIPv4Stack=true",
      "-J-XX:+UnlockExperimentalVMOptions",
      "-J-XX:MaxRAMPercentage=90"
    ),
    libraryDependencies ++= Seq(
      Dependencies.declineEffect,
      Dependencies.grpcOkHttp,
      Dependencies.trace4catsOpentelemetryJaegerExporter,
      Dependencies.trace4catsOpentelemetryOltpGrpcExporter,
      Dependencies.trace4catsStackdriverGrpcExporter
    )
  )
  .dependsOn(`collector-common`)
  .enablePlugins(UniversalPlugin, JavaServerAppPackaging, DockerPlugin, AshScriptPlugin)

lazy val `collector-lite` = (project in file("modules/collector-lite"))
  .settings(noPublishSettings)
  .settings(graalSettings)
  .settings(
    name := "trace4cats-collector-lite",
    libraryDependencies ++= Seq(Dependencies.declineEffect, Dependencies.graalKafkaClient)
  )
  .dependsOn(`collector-common`)
  .enablePlugins(NativeImagePlugin)
