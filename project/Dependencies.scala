import sbt._

object Dependencies {
  object Versions {
    val trace4cats = "0.14.0"

    val trace4catsAvro = "0.14.0"

    val trace4catsAvroKafka = "0.14.0"

    val trace4catsDatadog = "0.14.0"

    val trace4catsJaeger = "0.14.0"

    val trace4catsOpentelemetry = "0.14.0"

    val trace4catsCloudtrace = "0.14.0"

    val trace4catsNewrelic = "0.14.0"

    val trace4catsZipkin = "0.14.0"

    val trace4catsTailSamplingExtras = "0.14.0"

    val catsEffect = "3.3.14"

    val circe = "0.14.1"

    val circeYaml = "0.14.1"

    val decline = "2.3.1"

    val graalKafkaClient = "0.1.0"

    val grpc = "1.59.0"

    val http4sJdkClient = "0.7.0"

    val odin = "0.13.0"

    val scala213 = "2.13.8"

    val kindProjector = "0.13.2"

    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsCore = "io.janstenpickle"         %% "trace4cats-core"          % Versions.trace4cats
  lazy val trace4catsMeta = "io.janstenpickle"         %% "trace4cats-meta"          % Versions.trace4cats
  lazy val trace4catsTailSampling = "io.janstenpickle" %% "trace4cats-tail-sampling" % Versions.trace4cats

  lazy val trace4catsAvroExporter = "io.janstenpickle" %% "trace4cats-avro-exporter" % Versions.trace4catsAvro
  lazy val trace4catsAvroServer = "io.janstenpickle"   %% "trace4cats-avro-server"   % Versions.trace4catsAvro
  lazy val trace4catsAvroKafkaConsumer =
    "io.janstenpickle" %% "trace4cats-avro-kafka-consumer" % Versions.trace4catsAvroKafka
  lazy val trace4catsAvroKafkaExporter =
    "io.janstenpickle" %% "trace4cats-avro-kafka-exporter" % Versions.trace4catsAvroKafka
  lazy val trace4catsDatadogHttpExporter =
    "io.janstenpickle" %% "trace4cats-datadog-http-exporter" % Versions.trace4catsDatadog
  lazy val trace4catsJaegerThriftExporter =
    "io.janstenpickle" %% "trace4cats-jaeger-thrift-exporter" % Versions.trace4catsJaeger
  lazy val trace4catsOpentelemetryOltpGrpcExporter =
    "io.janstenpickle" %% "trace4cats-opentelemetry-otlp-grpc-exporter" % Versions.trace4catsOpentelemetry
  lazy val trace4catsOpentelemetryOltpHttpExporter =
    "io.janstenpickle" %% "trace4cats-opentelemetry-otlp-http-exporter" % Versions.trace4catsOpentelemetry
  lazy val trace4catsOpentelemetryJaegerExporter =
    "io.janstenpickle" %% "trace4cats-opentelemetry-jaeger-exporter" % Versions.trace4catsOpentelemetry
  lazy val trace4catsStackdriverGrpcExporter =
    "io.janstenpickle" %% "trace4cats-stackdriver-grpc-exporter" % Versions.trace4catsCloudtrace
  lazy val trace4catsStackdriverHttpExporter =
    "io.janstenpickle" %% "trace4cats-stackdriver-http-exporter" % Versions.trace4catsCloudtrace
  lazy val trace4catsNewrelicHttpExporter =
    "io.janstenpickle" %% "trace4cats-newrelic-http-exporter" % Versions.trace4catsNewrelic
  lazy val trace4catsZipkinHttpExporter =
    "io.janstenpickle" %% "trace4cats-zipkin-http-exporter" % Versions.trace4catsZipkin
  lazy val trace4catsTailSamplingCacheStore =
    "io.janstenpickle" %% "trace4cats-tail-sampling-cache-store" % Versions.trace4catsTailSamplingExtras
  lazy val trace4catsTailSamplingRedisStore =
    "io.janstenpickle" %% "trace4cats-tail-sampling-redis-store" % Versions.trace4catsTailSamplingExtras

  lazy val catsEffect = "org.typelevel"         %% "cats-effect"            % Versions.catsEffect
  lazy val circeGeneric = "io.circe"            %% "circe-generic-extras"   % Versions.circe
  lazy val circeYaml = "io.circe"               %% "circe-yaml"             % Versions.circeYaml
  lazy val declineEffect = "com.monovore"       %% "decline-effect"         % Versions.decline
  lazy val graalKafkaClient = "io.janstenpickle" % "graal-kafka-client"     % Versions.graalKafkaClient
  lazy val grpcOkHttp = "io.grpc"                % "grpc-okhttp"            % Versions.grpc
  lazy val http4sJdkClient = "org.http4s"       %% "http4s-jdk-http-client" % Versions.http4sJdkClient
  lazy val odin = "com.github.valskalla"        %% "odin-slf4j"             % Versions.odin

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}
