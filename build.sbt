import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

import sbt.enablePlugins

// ZIO Version
val zioVersion       = "1.0.4-2"
val zioConfigVersion = "1.0.0"
val scala_2_13       = "2.13.3"

lazy val supportedScalaVersions = List(scala_2_13)

Global / scalaVersion := scala_2_13

lazy val root = (project in file("."))
  .settings(
    skip in publish := true,
    name := "root",
  )
  .aggregate(zhttp, zhttpBenchmarks, example)

// CI Configuration
ThisBuild / githubWorkflowPublishTargetBranches := List()
ThisBuild / githubWorkflowBuildPreamble += WorkflowStep.Sbt(
  List("fmtCheck"),
  name = Some("Check formatting"))

// Test Configuration
ThisBuild / libraryDependencies ++=
  Seq(
    "dev.zio" %% "zio-test"     % zioVersion % "test",
    "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
  )
ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// Scalafix
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

// Projects

// Project zio-http
lazy val zhttp = (project in file("./zio-http"))
  .settings(
    version := "1.0.0.0-RC13",
    organization := "io.d11",
    organizationName := "d11",
    crossScalaVersions := supportedScalaVersions,
    licenses += ("MIT License", new URL("https://github.com/dream11/zio-http/blob/master/LICENSE")),
    homepage in ThisBuild := Some(url("https://github.com/dream11/zio-http")),
    scmInfo in ThisBuild :=
      Some(
        ScmInfo(url("https://github.com/dream11/zio-http"), "scm:git@github.com:dream11/zio-http.git"),
      ),
    developers in ThisBuild :=
      List(
        Developer(
          "tusharmath",
          "Tushar Mathur",
          "tushar@dream11.com",
          new URL("https://github.com/tusharmath"),
        ),
        Developer(
          "amitksingh1490",
          "Amit Kumar Singh",
          "amit.singh@dream11.com",
          new URL("https://github.com/amitksingh1490"),
        ),
      ),
    publishMavenStyle in ThisBuild := true,
    publishTo := {
      val nexus = "https://s01.oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    libraryDependencies ++=
      Seq(
        "dev.zio" %% "zio"         % zioVersion,
        "dev.zio" %% "zio-streams" % zioVersion,
        "io.netty" % "netty-all"   % "4.1.59.Final",
      ),
  )

// Project Benchmarks
lazy val zhttpBenchmarks = (project in file("./zio-http-benchmarks"))
  .enablePlugins(JmhPlugin)
  .dependsOn(zhttp)
  .settings(
    skip in publish := true,
    libraryDependencies ++=
      Seq(
        "dev.zio" %% "zio" % zioVersion,
      ),
  )

lazy val example = (project in file("./example"))
  .settings(
    fork := true,
    skip in publish := true,
    mainClass in (Compile, run) := Option("HelloWorldAdvanced"),
  )
  .dependsOn(zhttp)

Global / onChangedBuildSource := ReloadOnSourceChanges

// Compiler options
// RECOMMENDED SETTINGS: https://tpolecat.github.io/2017/04/25/scalac-flags.html
Global / scalacOptions ++=
  Seq(
    "-language:postfixOps",              // Added by @tusharmath
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:higherKinds",             // Allow higher-kinded types
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.

    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unused",                     // TODO check if we still need -Wunused below
    "-Xlint:nonlocal-return",            // A return statement used an exception for flow control.
    "-Xlint:implicit-not-found",         // Check @implicitNotFound and @implicitAmbiguous messages.
    "-Xlint:serial",                     // @SerialVersionUID on traits and non-serializable classes.
    "-Xlint:valpattern",                 // Enable pattern checks in val definitions.
    "-Xlint:eta-zero",                   // Warn on eta-expansion (rather than auto-application) of zero-ary method.
    "-Xlint:eta-sam",                    // Warn on eta-expansion to meet a Java-defined functional interface that is not explicitly annotated with @FunctionalInterface.
    "-Xlint:deprecation",                // Enable linted deprecations.

    "-Wdead-code",                       // Warn when dead code is identified.
    "-Wextra-implicit",                  // Warn when more than one implicit parameter section is defined.
    "-Wmacros:both",                     // Lints code before and after applying a macro
    "-Wnumeric-widen",                   // Warn when numerics are widened.
    "-Woctal-literal",                   // Warn on obsolete octal syntax.
    "-Wunused:imports",                  // Warn if an import selector is not referenced.
    "-Wunused:patvars",                  // Warn if a variable bound in a pattern is unused.
    "-Wunused:privates",                 // Warn if a private member is unused.
    "-Wunused:locals",                   // Warn if a local definition is unused.
    "-Wunused:explicits",                // Warn if an explicit parameter is unused.
    "-Wunused:implicits",                // Warn if an implicit parameter is unused.
    "-Wunused:params",                   // Enable -Wunused:explicits,implicits.
    "-Wunused:linted",
    "-Wvalue-discard",                   // Warn when non-Unit expression results are unused.

    "-Ybackend-parallelism", "8",                 // Enable paralellisation — change to desired number!
    "-Ycache-plugin-class-loader:last-modified",  // Enables caching of classloaders for compiler plugins
    "-Ycache-macro-class-loader:last-modified",   // and macro definitions. This can lead to performance improvements.

    // FIXME: Disabled because of scalac bug https://github.com/scala/bug/issues/11798
    //  "-Xlint:infer-any",                 // Warn when a type argument is inferred to be `Any`.
    //  "-Ywarn-infer-any",                 // Warn when a type argument is inferred to be `Any`.
    //  "-language:experimental.macros",   // Allow macro definition (besides implementation and application). Disabled, as this will significantly change in Scala 3
    //  "-language:implicitConversions",   // Allow definition of implicit functions called views. Disabled, as it might be dropped in Scala 3. Instead use extension methods (implemented as implicit class Wrapper(val inner: Foo) extends AnyVal {}

  )

addCommandAlias("fmt", "scalafmt; test:scalafmt; sFix;")
addCommandAlias("fmtCheck", "scalafmtCheck; test:scalafmtCheck; sFixCheck")
addCommandAlias("sFix", "scalafix OrganizeImports; test:scalafix OrganizeImports")
addCommandAlias("sFixCheck", "scalafix --check OrganizeImports; test:scalafix --check OrganizeImports")

Global / semanticdbEnabled := true
Global / semanticdbVersion := scalafixSemanticdb.revision
Global / watchAntiEntropy := FiniteDuration(2000, TimeUnit.MILLISECONDS)
