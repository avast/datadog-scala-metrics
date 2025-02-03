import sbt._
object Dependencies {
  object Cats {
    val effect = "org.typelevel" %% "cats-effect" % "2.5.5"
    val core   = "org.typelevel" %% "cats-core"   % "2.12.0"
  }

  object Datadog {
    val statsDClient = "com.datadoghq" % "java-dogstatsd-client" % "4.4.3"
  }

  object Http4s {
    val core = "org.http4s" %% "http4s-core" % "0.22.15"
  }

  object Testing {
    val mockitoScalatest = "org.mockito"   %% "mockito-scala-scalatest" % "1.15.1"
    val munit            = "org.scalameta" %% "munit"                   % "1.0.4"
  }

  object Logging {
    val logback = "ch.qos.logback" % "logback-classic" % "1.5.16"
  }

  object Mdoc {
    val libMdoc = "org.scalameta" %% "mdoc" % "2.6.3" excludeAll (ExclusionRule(
      organization = "org.slf4j"
    ))
  }

  object ScalaModules {
    val collectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0"
  }

}
