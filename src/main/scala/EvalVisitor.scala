package visitors

import org.antlr.v4.runtime.tree.*
import syntax.BinOp.*
import syntax.Expr.*
import syntax.Statement.*
import syntax.*
import util.*

import com.aetion.acal.sl.parser.DslParser.*
import com.aetion.acal.sl.parser.*

/** ANTLR4 grammar visitor implementation
  */
class EvalVisitor extends DslBaseVisitor[Option[Statement]]:

  // Top level override of Prog which is the entry point. We're using an override here
  // because of the fact that a `prog` is more than one statement. Without the override
  // we end up with only the first statement
  override def visitProg(ctx: ProgContext): Option[Statement] =
    flatMapJlist(ctx.stat(), visit) |> Statement.Block.apply |> Some.apply

  // whenever a non-terminal rule appears in other rules, we need a non-overridden
  // visit function for that rule. In this case, expr is a rule that is used/referred
  // to in the `stat` rule, so we need to have a visit function for it
  def visitExpr(tree: ExprContext | Null): Option[Expr] =
    for {
      s <- visit(tree) |> opt
      e <- s match
        case Expression(e) => Some(e)
        case _             => None
    } yield e

  // whenever we have a label, we should override the visit function for that label

  /** `'clear' NEWLINE` */
  override def visitClear(ctx: ClearContext): Option[Statement] =
    Some(Clear)

  /** `ID '=' expr NEWLINE` */
  override def visitAssign(ctx: AssignContext): Option[Statement] =
    for {
      id         <- ctx.ID |> opt
      expression <- ctx.expr |> visitExpr
      idTxt      <- id.getText() |> opt
    } yield Assign(idTxt, expression)

  /** `expr NEWLINE` */
  override def visitPrintExpr(ctx: PrintExprContext): Option[Statement] =
    for {
      c     <- ctx |> opt
      value <- c.expr |> visitExpr
    } yield Print(value)

  /** INT */
  override def visitInt(ctx: IntContext): Option[Statement] =
    for {
      v <- ctx.INT |> opt
      s <- v.getText |> opt
    } yield Expression(EInt(Integer.parseInt(s)))

  /** ID */
  override def visitId(ctx: IdContext): Option[Statement] =
    for {
      v <- ctx.ID |> opt
      s <- v.getText |> opt
    } yield Expression(EId(s))

  // When there's more than one optional token...
  /** expr op=(ADD | SUB) expr */
  override def visitAddSub(ctx: AddSubContext): Option[Statement] =
    for {
      op  <- ctx.op |> opt
      lhs <- visitExpr(ctx.expr(0))
      rhs <- visitExpr(ctx.expr(1))
      binOp = op.getType match
        case DslLexer.ADD => Add
        case DslLexer.SUB => Sub
    } yield Expression(EBinOp(lhs, rhs, binOp))

  /** expr op=(MUL | DIV) expr */
  override def visitMulDiv(ctx: MulDivContext): Option[Statement] =
    for {
      op  <- ctx.op |> opt
      lhs <- visitExpr(ctx.expr(0))
      rhs <- visitExpr(ctx.expr(1))
      binOp = op.getType match
        case DslLexer.MUL => Mul
        case DslLexer.DIV => Div
    } yield Expression(EBinOp(lhs, rhs, binOp))

  /** '(' expr ')' */
  override def visitParens(ctx: ParensContext): Option[Statement] =
    for {
      e <- visitExpr(ctx.expr())
    } yield Expression(e)
