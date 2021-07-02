import sbt._

object Dependencies {
  object Versions {
    val trace4cats = "0.12.0-RC1+191-347e4fc7"

    val circe = "0.14.1"
    val circeYaml = "0.14.0"
    val decline = "2.0.0"
    val graalKafkaClient = "0.1.0"
    val grpc = "1.39.0"
    val http4sJdkClient = "0.5.0-RC1"
    val log4cats = "2.1.1"
    val logback = "1.2.3"
    val scala213 = "2.13.6"

    val kindProjector = "0.13.0"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsModel = "io.janstenpickle"             %% "trace4cats-model"               % Versions.trace4cats
  lazy val trace4catsAvroExporter = "io.janstenpickle"      %% "trace4cats-avro-exporter"       % Versions.trace4cats
  lazy val trace4catsAvroKafkaConsumer = "io.janstenpickle" %% "trace4cats-avro-kafka-consumer" % Versions.trace4cats
  lazy val trace4catsAvroKafkaExporter = "io.janstenpickle" %% "trace4cats-avro-kafka-exporter" % Versions.trace4cats
  lazy val trace4catsAvroServer = "io.janstenpickle"        %% "trace4cats-avro-server"         % Versions.trace4cats
  lazy val trace4catsExporterCommon = "io.janstenpickle"    %% "trace4cats-exporter-common"     % Versions.trace4cats
  lazy val trace4catsMeta = "io.janstenpickle"              %% "trace4cats-meta"                % Versions.trace4cats
  lazy val trace4catsRateSampling = "io.janstenpickle"      %% "trace4cats-rate-sampling"       % Versions.trace4cats
  lazy val trace4catsLogExporter =
    "io.janstenpickle" %% "trace4cats-log-exporter" % Versions.trace4cats
  lazy val trace4catsDatadogHttpExporter =
    "io.janstenpickle" %% "trace4cats-datadog-http-exporter" % Versions.trace4cats
  lazy val trace4catsJaegerThriftExporter =
    "io.janstenpickle" %% "trace4cats-jaeger-thrift-exporter" % Versions.trace4cats
  lazy val trace4catsOpentelemetryOltpGrpcExporter =
    "io.janstenpickle" %% "trace4cats-opentelemetry-otlp-grpc-exporter" % Versions.trace4cats
  lazy val trace4catsOpentelemetryOltpHttpExporter =
    "io.janstenpickle" %% "trace4cats-opentelemetry-otlp-http-exporter" % Versions.trace4cats
  lazy val trace4catsOpentelemetryJaegerExporter =
    "io.janstenpickle" %% "trace4cats-opentelemetry-jaeger-exporter" % Versions.trace4cats
  lazy val trace4catsStackdriverGrpcExporter =
    "io.janstenpickle" %% "trace4cats-stackdriver-grpc-exporter" % Versions.trace4cats
  lazy val trace4catsStackdriverHttpExporter =
    "io.janstenpickle" %% "trace4cats-stackdriver-http-exporter" % Versions.trace4cats
  lazy val trace4catsNewrelicHttpExporter =
    "io.janstenpickle"                                       %% "trace4cats-newrelic-http-exporter" % Versions.trace4cats
  lazy val trace4catsZipkinHttpExporter = "io.janstenpickle" %% "trace4cats-zipkin-http-exporter"   % Versions.trace4cats
  lazy val trace4catsTailSampling = "io.janstenpickle"       %% "trace4cats-tail-sampling"          % Versions.trace4cats
  lazy val trace4catsTailSamplingCacheStore =
    "io.janstenpickle" %% "trace4cats-tail-sampling-cache-store" % Versions.trace4cats
  lazy val trace4catsTailSamplingRedisStore =
    "io.janstenpickle"                              %% "trace4cats-tail-sampling-redis-store" % Versions.trace4cats
  lazy val trace4catsFiltering = "io.janstenpickle" %% "trace4cats-filtering"                 % Versions.trace4cats

  lazy val circeGeneric = "io.circe"            %% "circe-generic-extras"   % Versions.circe
  lazy val circeYaml = "io.circe"               %% "circe-yaml"             % Versions.circeYaml
  lazy val declineEffect = "com.monovore"       %% "decline-effect"         % Versions.decline
  lazy val graalKafkaClient = "io.janstenpickle" % "graal-kafka-client"     % Versions.graalKafkaClient
  lazy val grpcOkHttp = "io.grpc"                % "grpc-okhttp"            % Versions.grpc
  lazy val http4sJdkClient = "org.http4s"       %% "http4s-jdk-http-client" % Versions.http4sJdkClient
  lazy val log4cats = "org.typelevel"           %% "log4cats-slf4j"         % Versions.log4cats
  lazy val logback = "ch.qos.logback"            % "logback-classic"        % Versions.logback

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}
