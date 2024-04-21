import scala.util.Using

import org.antlr.v4.runtime.*
import syntax.Statement
import util.*
import visitors.*

import com.aetion.acal.sl.parser.DslParser.ProgContext
import com.aetion.acal.sl.parser.*

object Compiler:
  def evaluate(program: String): Unit =
    for {
      cs <- CharStreams.fromString(program) |> opt
      parser = new DslParser(new CommonTokenStream(DslLexer(cs)))
      tree <- parser.prog() |> opt
      visitor = new EvalVisitor
      ast <- visitor.visit(tree) |> opt
      smart = SmartTreeWalker.walkTree(tree)
      _     = printResult(ast, smart, parser, tree)
    } yield ()

    def printResult(s: Statement, s2: Statement, p: Parser, c: ProgContext) =
      println("\n---------------- tree ------------------")
      println(c.toStringTree(p))
      println("\n---------------- ast  ------------------")
      println(s"$s")
      println("\n---------------- ast 2 ------------------")
      println(s"$s2")
      println("\ndone")

  def evalResource(fn: String) =
    val str =
      Using.resource(scala.io.Source.fromResource(fn))(_.mkString)
    Compiler.evaluate(str)

object Main:
  def main(args: Array[String]): Unit =
    Compiler.evalResource("test2.acl")
