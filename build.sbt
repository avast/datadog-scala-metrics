import CompilerSettings._

lazy val scala212               = "2.12.11"
lazy val scala213               = "2.13.1"
lazy val scala3                 = "0.27.0-RC1"
lazy val supportedScalaVersions = List(scala212, scala213, scala3)

lazy val scalaSettings = Seq(
  scalaVersion := scala212,
  scalacOptions ++= scalacOptionsFor(scalaVersion.value),
  scalacOptions.in(Compile, console) ~= filterConsoleScalacOptions,
  scalacOptions.in(Test, console) ~= filterConsoleScalacOptions,
  scalacOptions ++= { if (isDotty.value) Seq("-source:3.0-migration") else Nil },
  crossScalaVersions := supportedScalaVersions,
  mimaPreviousArtifacts := previousStableVersion.value.map(organization.value %% name.value % _).toSet,
  libraryDependencies += (Dependencies.Testing.scalaTest        % Test).withDottyCompat(scalaVersion.value),
  libraryDependencies += (Dependencies.Testing.mockitoScalatest % Test).withDottyCompat(scalaVersion.value)
)

lazy val commonSettings = Seq(
  sonatypeProfileName := "com.avast",
  organization := "com.avast.cloud",
  homepage := Some(url("https://github.com/avast/datadog4s")),
  licenses := List("MIT" -> url(s"https://github.com/avast/datadog4s/blob/${version.value}/LICENSE")),
  description := "Library for datadog app monitoring",
  developers := List(
    Developer(
      "tomasherman",
      "Tomas Herman",
      "hermant@avast.com",
      url("https://tomasherman.cz")
    )
  ),
  publishArtifact in Test := false,
  testOptions += Tests.Argument(TestFrameworks.JUnit)
)

lazy val global = project
  .in(file("."))
  .settings(name := "datadog4s")
  .settings(commonSettings)
  .settings(scalaSettings)
  .aggregate(api, statsd, http4s, jvm, site, common)
  .dependsOn(api, statsd, http4s, jvm)
  .disablePlugins(MimaPlugin)

lazy val api = project
  .in(file("code/api"))
  .settings(
    name := "datadog4s-api",
    scalaSettings,
    commonSettings,
    libraryDependencies += Dependencies.Cats.core.withDottyCompat(scalaVersion.value)
  )

lazy val common = project
  .in(file("code/common"))
  .settings(
    name := "datadog4s-common",
    scalaSettings,
    commonSettings,
    libraryDependencies += Dependencies.Cats.effect.withDottyCompat(scalaVersion.value),
    libraryDependencies += (Dependencies.Testing.scalaTest % Test).withDottyCompat(scalaVersion.value),
    libraryDependencies += (Dependencies.Logging.logback   % Test).withDottyCompat(scalaVersion.value)
  )
  .dependsOn(api)

lazy val statsd = project
  .in(file("code/statsd"))
  .settings(
    name := "datadog4s-statsd",
    scalaSettings,
    commonSettings,
    libraryDependencies += Dependencies.Cats.effect.withDottyCompat(scalaVersion.value),
    libraryDependencies += Dependencies.Datadog.statsDClient.withDottyCompat(scalaVersion.value)
  )
  .dependsOn(api)

lazy val http4s = project
  .in(file("code/http4s"))
  .settings(
    name := "datadog4s-http4s",
    scalaSettings,
    commonSettings,
    libraryDependencies += Dependencies.Cats.effect.withDottyCompat(scalaVersion.value),
    libraryDependencies := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case _ =>
          libraryDependencies.value ++ Seq(
            Dependencies.Http4s.core212.withDottyCompat(scalaVersion.value)
          )
      }
    }
  )
  .dependsOn(api)

lazy val jvm  = project
  .in(file("code/jvm"))
  .settings(
    name := "datadog4s-jvm",
    scalaSettings,
    commonSettings,
    libraryDependencies += Dependencies.Cats.effect.withDottyCompat(scalaVersion.value),
    libraryDependencies += Dependencies.ScalaModules.collectionCompat.withDottyCompat(scalaVersion.value)
  )
  .dependsOn(api, common % "compile->compile;test->test")

lazy val site = (project in file("site"))
  .settings(scalaSettings)
  .settings(commonSettings)
  .disablePlugins(MimaPlugin)
  .enablePlugins(
    MdocPlugin,
    MicrositesPlugin,
    SiteScaladocPlugin,
    ScalaUnidocPlugin
  )
  .settings(
    libraryDependencies += Dependencies.Mdoc.libMdoc.withDottyCompat(scalaVersion.value),
    libraryDependencies -= "org.tpolecat" %% "tut-core" % "0.6.13",
    libraryDependencies -= "org.tpolecat" %% "tut-core" % "0.6.13" % Tut
  )
  .settings(publish / skip := true)
  .settings(BuildSupport.micrositeSettings: _*)
  .dependsOn(api, statsd, http4s, jvm)

addCommandAlias(
  "checkAll",
  "; scalafmtSbtCheck; scalafmtCheckAll; coverage; +test; coverageReport; doc; site/makeMdoc"
)

addCommandAlias("fixAll", "; scalafmtSbt; scalafmtAll")
