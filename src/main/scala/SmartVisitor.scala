package visitors

import java.rmi.UnexpectedException

import scala.jdk.CollectionConverters.*

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import syntax.BinOp.*
import syntax.Expr.*
import syntax.Statement.*
import syntax.*
import util.*

import com.aetion.acal.sl.parser.DslParser.*
import com.aetion.acal.sl.parser.*

// scalafix:off DisableSyntax.asInstanceOf
// scalafix:off DisableSyntax.isInstanceOf
// scalafix:off DisableSyntax.defaultArgs
// scalafix:off DisableSyntax.throw
object SmartVisitor:

  def getRuleName(idx: Int): String =
    val names = DslParser.ruleNames.nn
    if idx >= names.length then throw java.lang.IndexOutOfBoundsException()
    names(idx).nn

  def getTerminalName(idx: Int) =
    DslParser.VOCABULARY.nn.getSymbolicName(idx) match
      case null => None
      case s    => Some(s)

  def parseRule(rule: RuleContext): Option[Statement] =
    rule match
      case prog: ProgContext =>
        // list of statements
        prog.stat().nn.asScala.toList.flatMap(parseTreeToAST(0))
      case ps: PrintExprContext => ???
      case as: AssignContext    => ???
      case clr: ClearContext    => ???
      case blank: BlankContext  => ???
      case bop: BinOpContext    => ???
      case i: IntContext        => ???
      case id: IdContext        => ???
      case _ =>
        throw new UnexpectedException(s"context not found: ${rule.getClass()}")
    None

  def parseTreeToAST(ind: Int = 0)(tree: ParseTree): Option[Statement] =
    val space = s"${" " * ind}"
    if tree.isInstanceOf[ParserRuleContext] then
      parseRule(tree.asInstanceOf[RuleContext])
      // val rule     = tree.asInstanceOf[ParserRuleContext]
      // val ruleIdx  = rule.getRuleIndex
      // val ruleName = getRuleName(ruleIdx)

      // println(s"${space}RULE($ruleIdx):$ruleName")
      // if rule.children != null then
      //   val subTrees          = rule.children.nn.asScala.toList
      //   val subTreeStatements = subTrees.flatMap(parseTreeToAST(ind + 2))

      // rule match
      //   case binOp: DslParser.BinOpContext => None
      //   case intCtx: DslParser.IntContext  => None
      //   case _                             => None
      // rule.getRuleIndex match
      //   case DslParser.RULE_prog =>
      //     // collect all the children, they should all be statements
      //     val subTrees          = rule.children.nn.asScala.toList
      //     val subTreeStatements = subTrees.flatMap(parseTreeToAST)
      //     None
      //   case DslParser.RULE_stat =>
      //     /*
      //       stat:
      //         expr NEWLINE			    # printExpr
      //         | ID '=' expr NEWLINE	# assign
      //         | 'clear' NEWLINE		  # clear
      //         | NEWLINE				      # blank;
      //      */

      //     None
      //   case DslParser.RULE_expr => None
    else if tree.isInstanceOf[TerminalNode] then
      val terminal = tree.asInstanceOf[TerminalNode]
      val termIdx  = terminal.getSymbol().nn.getType()
      val termName = getTerminalName(termIdx).getOrElse("<no name>")

      termIdx match
        case DslParser.INT | DslParser.ID =>
          println(
            s"${" " * ind}$termName@$termIdx : ${terminal.getText()}"
          )
        case _ => ()

      // symbolicName match
      //   case "INT" | "ID" =>
      //     println(
      //       s"${" " * ind}$symbolicName@$termIdx : ${terminal.getText()}"
      //     )
      //   case _ =>
      //     println(
      //       s"${" " * ind}TERMINAL($symbolicName)@$termIdx : ${terminal.getText()}"
      //     )
      None
    else
      println("Neither a TerminalNode nor a ParserRuleContext")
      None
