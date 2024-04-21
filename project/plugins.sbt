ThisBuild / autoStartServer := false

// // The std library for sbt is handled by sbt itself so no need to include it in the report.
// dependencyUpdatesFilter -= moduleFilter(name = "scala-library")

update / evictionWarningOptions := EvictionWarningOptions.empty

addDependencyTreePlugin

addSbtPlugin("com.eed3si9n"        % "sbt-assembly"     % "2.1.1")
addSbtPlugin("ch.epfl.scala"       % "sbt-scalafix"     % "0.11.1")
addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.1")
addSbtPlugin("com.timushev.sbt"    % "sbt-rewarn"       % "0.1.3")
addSbtPlugin("com.timushev.sbt"    % "sbt-updates"      % "0.6.4")
addSbtPlugin("io.spray"            % "sbt-revolver"     % "0.10.0")
addSbtPlugin("org.scalameta"       % "sbt-scalafmt"     % "2.5.2")
addSbtPlugin("org.wartremover"     % "sbt-wartremover"  % "3.1.6")
addSbtPlugin("com.simplytyped"     % "sbt-antlr4"       % "0.8.3")
