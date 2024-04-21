import scala.util.*
import scala.sys.process.*
import sbt.*

object SbtUtil {
  def styled(in: Any): String =
    scala.Console.CYAN + in + scala.Console.RESET

  def prompt(prj: String): String =
    gitPrompt.fold(projectPrompt(prj))(g => s"$g:${projectPrompt(prj)}")

  private def projectPrompt(prj: String): String =
    s"sbt:${styled(prj)}"

  def projectName(state: State): String =
    Project
      .extract(state)
      .currentRef
      .project

  private def gitPrompt: Option[String] =
    for {
      b <- branch.map(styled)
      h <- hash.map(styled)
    } yield s"git:$b:$h"

  private def branch: Option[String] =
    run("git rev-parse --abbrev-ref HEAD")

  private def hash: Option[String] =
    run("git rev-parse --short HEAD")

  private def run(cmd: String): Option[String] =
    Try(
      cmd
        .split(" ")
        .toSeq
        .!!(noopProcessLogger)
        .trim
    ).toOption

  private val noopProcessLogger: ProcessLogger =
    ProcessLogger(_ => (), _ => ())
  // val Cctt: String =
  //   "compile->compile;test->test"
}
