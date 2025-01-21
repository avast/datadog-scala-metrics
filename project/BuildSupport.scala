import Dependencies.{Cats, Http4s}
import com.typesafe.sbt.site.SitePlugin.autoImport._
import mdoc.MdocPlugin.autoImport._
import microsites.MicrositesPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtunidoc.ScalaUnidocPlugin.autoImport._
import sbtdynver.DynVerPlugin.autoImport._

object BuildSupport {
  object ScalaVersions {
    lazy val scala212               = "2.12.20"
    lazy val scala213               = "2.13.16"
    lazy val scala3                 = "3.6.2"
    lazy val supportedScalaVersions = List(scala212, scala213, scala3)
  }

  lazy val micrositeSettings = Seq(
    micrositeName             := "datadog4s",
    micrositeDescription      := "Great monitoring made easy",
    micrositeAuthor           := "Tomas Herman",
    micrositeGithubOwner      := "datadog4s",
    micrositeGithubRepo       := "datadog4s",
    micrositeUrl              := "https://datadog4s.github.io",
    micrositeDocumentationUrl := "api/latest/io/github/datadog4s/",
    micrositeBaseUrl          := "/datadog4s",
    micrositeFooterText       := None,
    micrositeGitterChannel    := false,
    micrositeTheme            := "pattern",
    mdocIn                    := file("site") / "docs",
    mdocVariables := Map(
      "VERSION" -> {
        if (!isSnapshot.value) { version.value }
        else { previousStableVersion.value.getOrElse("latestVersion") }
      },
      "CATS_VERSION"        -> Cats.core.revision,
      "CATS_EFFECT_VERSION" -> Cats.effect.revision,
      "HTTP4S_212_VERSION"  -> Http4s.core.revision,
      "HTTP4S_213_VERSION"  -> Http4s.core.revision,
      "SCALA_3_VERSION"     -> ScalaVersions.scala3
    ),
    mdocAutoDependency           := false,
    micrositeDataDirectory       := file("site"),
    ScalaUnidoc / siteSubdirName := "api/latest",
    addMappingsToSiteDir(
      ScalaUnidoc / packageDoc / mappings,
      ScalaUnidoc / siteSubdirName
    )
  )
}
