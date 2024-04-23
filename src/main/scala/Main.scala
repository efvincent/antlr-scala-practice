import scala.util.Using

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionMode
import syntax.Statement
import util.*
import visitors.*

import com.aetion.acal.sl.parser.DslParser.ProgContext
import com.aetion.acal.sl.parser.*

class ErrListener extends BaseErrorListener:
  override def syntaxError(
      recognizer: Recognizer[?, ?] | Null,
      offendingSymbol: Object | Null,
      line: Int,
      charPositionInLine: Int,
      msg: String | Null,
      e: RecognitionException | Null): Unit =
    println(s"OMFG! Line $line: $charPositionInLine - $msg")

object Compiler:
  def evaluate(program: String): Unit =
    for {
      cs <- CharStreams.fromString(program) |> opt
      parser = new DslParser(new CommonTokenStream(DslLexer(cs)))
      interpreter <- parser.getInterpreter() |> opt
      _ = interpreter.setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION)
      _ = parser.removeErrorListeners()
      _ = parser.addErrorListener(new ErrListener)
      tree <- parser.prog() |> opt
      smart = SmartTreeWalker.walkTree(tree)
      _     = printResult(smart, parser, tree)
    } yield ()

    def printResult(s: Statement, p: Parser, c: ProgContext) =
      println("\n---------------- tree ------------------")
      println(c.toStringTree(p))
      println("\n---------------- ast  ------------------")
      println(s"$s")
      println("\ndone")

  def evalResource(fn: String) =
    val str =
      Using.resource(scala.io.Source.fromResource(fn))(_.mkString)
    Compiler.evaluate(str)

object Main:
  def main(args: Array[String]): Unit =
    Compiler.evalResource("test3.acl")
