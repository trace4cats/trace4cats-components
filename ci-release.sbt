ThisBuild / scalaVersion := Dependencies.Versions.scala213
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("11"))

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
  ref = UseRef.Public("crazy-max", "ghaction-import-gpg", "v4"),
  id = Some("import_gpg"),
  name = Some("Import GPG key"),
  params = Map("gpg_private_key" -> "${{ secrets.GPG_PRIVATE_KEY }}", "passphrase" -> "${{ secrets.PGP_PASS }}")
)

ThisBuild / githubWorkflowPublishPostamble ++= {
  val dockerhubUsername = "janstenpickle"
  val githubRunNumber = "${{ github.run_number }}"
  val releaseVersion = "${{ env.RELEASE_VERSION }}"

  val common = Seq(
    WorkflowStep.Use(ref = UseRef.Public("docker", "setup-buildx-action", "v1"), name = Some("Set up Docker Buildx")),
    WorkflowStep.Use(
      ref = UseRef.Public("docker", "login-action", "v1"),
      name = Some("Login to Dockerhub"),
      params = Map("username" -> dockerhubUsername, "password" -> "${{ secrets.DOCKERHUB }}")
    ),
    WorkflowStep.ComputeVar(
      name = "RELEASE_VERSION",
      cmd =
        "sbt -Dsbt.log.noformat=true 'inspect actual version' | grep \"Setting: java.lang.String\" | cut -d '=' -f2 | tr -d ' '"
    ),
    WorkflowStep.ComputeVar(
      name = "IS_STABLE_RELEASE",
      cmd = "if [[ `echo $RELEASE_VERSION | grep '+'` ]]; then echo false; else echo true; fi"
    )
  )

  def perModule(module: String, isNativeImage: Boolean) = {
    val imageName = s"$dockerhubUsername/trace4cats-$module"

    val buildImage =
      if (isNativeImage)
        Seq(
          WorkflowStep.Sbt(
            name = Some(s"Build GraalVM native image for '$module'"),
            commands = List(s"project $module", "nativeImage")
          ),
          WorkflowStep.Use(
            ref = UseRef.Public("docker", "build-push-action", "v2"),
            name = Some(s"Build Docker image for '$module'"),
            params = Map(
              "file" -> s"modules/$module/src/main/docker/Dockerfile",
              "context" -> s"modules/$module/target/native-image",
              "tags" -> s"$imageName:$githubRunNumber",
              "push" -> "false",
              "load" -> "true"
            )
          )
        )
      else
        Seq(
          WorkflowStep.Sbt(
            name = Some(s"Build Docker image for '$module'"),
            commands =
              List(s"project $module", s"""set ThisBuild / version := "$githubRunNumber"""", "Docker / publishLocal")
          )
        )

    val pushImage = Seq(
      WorkflowStep.Run(
        name = Some(s"Push Docker images for '$module'"),
        commands = List(
          s"docker tag $imageName:$githubRunNumber $imageName:latest",
          s"docker push $imageName:$githubRunNumber",
          s"docker push $imageName:latest"
        )
      )
    )

    val pushVersionedImage =
      Seq(
        WorkflowStep.Run(
          name = Some(s"Push versioned Docker image for '$module'"),
          cond = Some("env.IS_STABLE_RELEASE == 'true'"),
          commands = List(
            s"docker tag $imageName:$githubRunNumber $imageName:$releaseVersion",
            s"docker push $imageName:$releaseVersion"
          )
        )
      )

    buildImage ++ pushImage ++ pushVersionedImage
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
