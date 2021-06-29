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
  val pre = Seq(
    WorkflowStep.Use(
      ref = UseRef.Public("rinx", "setup-graalvm-ce", "v0.0.5"),
      id = Some("setup_graalvm"),
      name = Some("Setup GraalVM CE"),
      params = Map("graalvm-version" -> "20.2.0", "java-version" -> "java11", "native-image" -> "true")
    ),
    WorkflowStep.Run(
      name = Some("Login to Dockerhub"),
      commands = List("docker login -u janstenpickle -p '${{ secrets.DOCKERHUB }}'")
    ),
    WorkflowStep.ComputeVar(
      name = "RELEASE_VERSION",
      cmd = "sbt --client --error 'print version' | tail -n 2 | head -n 1 |  sed 's/^[[:blank:]]*//;s/[[:blank:]]*$//'"
    )
  )

  def perModule(module: String, nativeImage: Boolean): Seq[WorkflowStep] = {
    val imgName = s"trace4cats-$module"

    val buildImg =
      if (nativeImage)
        Seq(
          WorkflowStep.Sbt(
            name = Some(s"Build native image for `$imgName`"),
            commands = List(s"project $module", "graalvm-native-image:packageBin")
          ),
          WorkflowStep.Run(
            name = Some(s"Build Docker image for `$imgName`"),
            commands = List(s"pushd modules/$module/src/main/docker", "sh build.sh", "popd")
          )
        )
      else
        Seq(
          WorkflowStep.Sbt(
            name = Some(s"Build Docker image for `$imgName`"),
            commands = List(s"project $module", "set ThisBuild / version := \"latest\"", "docker:publishLocal")
          )
        )

    val pushImg = Seq(
      WorkflowStep.Run(
        name = Some(s"Push Docker image for `$imgName`"),
        commands = List(
          s"docker tag janstenpickle/$imgName:latest janstenpickle/$imgName:$$GITHUB_RUN_NUMBER",
          s"docker push janstenpickle/$imgName:$$GITHUB_RUN_NUMBER",
          s"docker push janstenpickle/$imgName:latest"
        )
      )
    )

    val pushVersionedImg =
      Seq(
        WorkflowStep.Run(
          name = Some(s"Push versioned Docker image for `$imgName`"),
          commands = List(
            """if [[ "${{ env.RELEASE_VERSION }}" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[0-9A-Za-z-]+)?$ ]]; then""",
            s"  docker tag janstenpickle/$imgName:$$GITHUB_RUN_NUMBER janstenpickle/$imgName:$${{ env.RELEASE_VERSION }}",
            s"  docker push janstenpickle/$imgName:$${{ env.RELEASE_VERSION }}",
            "fi"
          )
        )
      )

    buildImg ++ pushImg ++ pushVersionedImg
  }

  pre ++ Seq("agent" -> true, "agent-kafka" -> true, "collector-lite" -> true, "collector" -> false)
    .flatMap { case (module, isNative) => perModule(module, isNative) }
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
