import Dependencies.*
import _root_.com.simplytyped.Antlr4Plugin.autoImport.antlr4PackageName

val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .enablePlugins(Antlr4Plugin)
  .settings(commonSettings)
  .settings(autoImportSettings)
  .settings(
    name                       := "antlr2",
    version                    := "0.1.0-SNAPSHOT",
    scalaVersion               := scala3Version,
    Antlr4 / antlr4Version     := "4.13.1",
    Antlr4 / antlr4GenListener := false,
    Antlr4 / antlr4GenVisitor  := true,
    Antlr4 / antlr4PackageName := Some("com.aetion.acal.sl.parser"),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit"          % "0.7.29" % Test,
      "org.antlr"      % "antlr4-runtime" % "4.13.1"
    ),
    libraryDependencies ++= dependencies
  )

/* --------------------------------------------
 *
 * SETTINGS
 *
 * ----------------------------------------------- */

// defines the common dependencies
lazy val dependencies =
  Seq(
    // main dependencies
    dev.zio.zio,
    dev.zio.`zio-streams`,
    dev.zio.`zio-prelude`,
    com.lihaoyi.fastparse
  ) ++
    Seq(
      // test dependencies
      org.scalacheck.scalacheck,
      org.scalameta.`munit-scalacheck`,
      org.scalameta.munit,
      org.typelevel.`discipline-munit`,
      dev.zio.`zio-test`,
      dev.zio.`zio-test-sbt`,
      dev.zio.`zio-test-magnolia`
    ).map(_ % Test)

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions
)

lazy val autoImportSettings = Seq(
  scalacOptions +=
    Seq(
      "java.lang",
      "scala",
      "scala.Predef",
      "scala.annotation",
      "scala.util.chaining"
    ).mkString(start = "-Yimports:", sep = ",", end = ""),
  Test / scalacOptions +=
    Seq(
      "org.scalacheck",
      "org.scalacheck.Prop"
    ).mkString(start = "-Yimports:", sep = ",", end = "")
)

/* --------------------------------------------
 *
 * Build Utilities & Definitions
 *
 * ------------------------------------------- */

// removes warnings sbt will give when manipulating keys it doesn't recognize
Global / excludeLintKeys += idePackagePrefix
Global / excludeLintKeys += ideBasePackages

// Automatically reload sbt changes
Global / onChangedBuildSource := ReloadOnSourceChanges

/* Set which class will have the main method for running under SBT. You'll need
 * to change this to specify a different main method to run from the global (top
 * level) SBT prompt.
 *
 * You can run other main methods in SBT, but not from global, by changing to
 * the project that has the main method you want with:
 *
 * sbt:global> project cl-repl
 *
 * sbt:cl-repl> run
 *
 * this first changes sbt to the cl-repl project, then runs what it finds there */
Compile / mainClass := Some("Main")

// settings for the overall build
inThisBuild(
  List(
    scalaVersion := scala3Version,
    organization := "com.aetion",
    // scalafix requires semantic db to do its thing
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixOnCompile := true
  )
)
