ThisBuild / scalaVersion := Dependencies.Versions.scala213
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.11")

ThisBuild / githubWorkflowBuildPreamble += WorkflowStep.Sbt(
  List("scalafmtCheckAll", "scalafmtSbtCheck"),
  name = Some("Check formatting")
)

ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.Equals(Ref.Branch("master")))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ciReleaseSonatype"),
    name = Some("Publish artifacts"),
    env = Map(
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)
ThisBuild / githubWorkflowPublishCond := Some("github.actor != 'mergify[bot]'")
ThisBuild / githubWorkflowPublishPreamble += WorkflowStep.Use(
  ref = UseRef.Public("crazy-max", "ghaction-import-gpg", "v3"),
  id = Some("import_gpg"),
  name = Some("Import GPG key"),
  params = Map("gpg-private-key" -> "${{ secrets.GPG_PRIVATE_KEY }}", "passphrase" -> "${{ secrets.PGP_PASS }}")
)

ThisBuild / githubWorkflowPublishPostamble ++= {
  val common = Seq(
    WorkflowStep.Use(ref = UseRef.Public("docker", "setup-buildx-action", "v1"), name = Some("Set up Docker Buildx")),
    WorkflowStep.Use(
      ref = UseRef.Public("docker", "login-action", "v1"),
      name = Some("Login to Dockerhub"),
      params = Map("username" -> "janstenpickle", "password" -> "${{ secrets.DOCKERHUB }}")
    ),
    WorkflowStep.ComputeVar(
      name = "RELEASE_VERSION",
      cmd =
        "sbt -Dsbt.log.noformat=true --client 'inspect actual version' | grep \"Setting: java.lang.String\" | cut -d '=' -f2 | tr -d ' '"
    )
  )

  def perModule(module: String, isNativeImage: Boolean) = {
    val name = s"trace4cats-$module"

    val buildImg =
      if (isNativeImage)
        Seq(
          WorkflowStep.Sbt(
            name = Some(s"Build GraalVM native image for '$name'"),
            commands = List(s"project $module", "nativeImage")
          ),
          WorkflowStep.Run(
            name = Some(s"Build Docker image for '$name'"),
            commands = {
              val dockerfile = s"modules/$module/src/main/docker/Dockerfile"
              val tag = s"janstenpickle/$name:$${{ github.run_number }}"
              val path = s"modules/$module/target/native-image"
              List(s"docker build -f $dockerfile -t $tag $path")
            }
          ),
          WorkflowStep.Use(
            ref = UseRef.Public("docker", "build-push-action", "v2"),
            name = Some(s"Build Docker image for '$name' (alt)"),
            params = Map(
              "file" -> s"modules/$module/src/main/docker/Dockerfile",
              "context" -> s"modules/$module/target/native-image",
              "tags" -> s"janstenpickle/$name:$${{ github.run_number }}"
            )
          )
        )
      else
        Seq(
          WorkflowStep.Sbt(
            name = Some(s"Build Docker image for '$name'"),
            commands = List(
              s"project $module",
              "set ThisBuild / version := \"${{ github.run_number }}\"",
              "Docker / publishLocal"
            )
          )
        )

    val pushImg = Seq(
      WorkflowStep.Run(
        name = Some(s"Push Docker images for '$name'"),
        commands = List(
          s"docker tag janstenpickle/$name:$${{ github.run_number }} janstenpickle/$name:latest",
          s"docker push janstenpickle/$name:$${{ github.run_number }}",
          s"docker push janstenpickle/$name:latest"
        )
      )
    )

    val pushVersionedImg =
      Seq(
        WorkflowStep.Run(
          name = Some(s"Push versioned Docker image for '$name'"),
          commands = List(
            """if [[ "${{ env.RELEASE_VERSION }}" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[0-9A-Za-z-]+)?$ ]]; then""",
            s"  docker tag janstenpickle/$name:$${{ github.run_number }} janstenpickle/$name:$${{ env.RELEASE_VERSION }}",
            s"  docker push janstenpickle/$name:$${{ env.RELEASE_VERSION }}",
            "fi"
          )
        )
      )

    buildImg ++ pushImg ++ pushVersionedImg
  }

  common ++ Seq("agent" -> true, "agent-kafka" -> true, "collector-lite" -> true, "collector" -> false)
    .flatMap((perModule _).tupled)
}
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / versionScheme := Some("early-semver")

ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
ThisBuild / developers := List(
  Developer(
    "janstenpickle",
    "Chris Jansen",
    "janstenpickle@users.noreply.github.com",
    url = url("https://github.com/janstepickle")
  ),
  Developer(
    "catostrophe",
    "λoλcat",
    "catostrophe@users.noreply.github.com",
    url = url("https://github.com/catostrophe")
  )
)
ThisBuild / homepage := Some(url("https://github.com/trace4cats/trace4cats-components"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/trace4cats/trace4cats-components"),
    "scm:git:git@github.com:trace4cats/trace4cats-components.git"
  )
)
ThisBuild / organization := "io.janstenpickle"
ThisBuild / organizationName := "trace4cats"
