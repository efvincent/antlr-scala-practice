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
  override def visitProg(ctx: ProgContext): Option[Statement] =
    flatMapJlist(ctx.stat(), visit, Statement.Block.apply)

  // def visitStat(stmt: StatContext | Null): Option[Statement] =
  //   visit(stmt) |> opt

  /** Visit a parse tree expected to produce an expression
    *
    * @param tree the parse tree to visit
    * @return `Some(Expression)` if the parse tree is not null and
    * evaluates to an expression, `None` otherwise
    */
  def visitExp(tree: ExprContext | Null): Option[Expr] =
    for {
      s <- visit(tree) |> opt
      e <- s match
        case Expression(e) => Some(e)
        case _             => None
    } yield e

  override def visitClear(ctx: ClearContext): Option[Statement] =
    Some(Clear)

  /** `ID '=' expr NEWLINE` */
  override def visitAssign(ctx: AssignContext): Option[Statement] =
    for {
      id         <- ctx.ID |> opt
      expression <- ctx.expr |> visitExp
      idTxt      <- id.getText() |> opt
    } yield Assign(idTxt, expression)

  /** `expr NEWLINE` */
  override def visitPrintExpr(ctx: PrintExprContext): Option[Statement] =
    for {
      c     <- ctx |> opt
      value <- c.expr |> visitExp
    } yield Print(value)

  /** INT */
  override def visitInt(ctx: IntContext): Option[Statement] = for {
    v <- ctx.INT |> opt
    s <- v.getText |> opt
  } yield Expression(EInt(Integer.parseInt(s)))

  /** ID */
  override def visitId(ctx: IdContext): Option[Statement] = for {
    v <- ctx.ID |> opt
    s <- v.getText |> opt
  } yield Expression(EId(s))

  override def visitBinOp(ctx: BinOpContext): Option[Statement] = for {
    op    <- ctx.op |> opt
    left  <- visitExp(ctx.expr(0))
    right <- visitExp(ctx.expr(1))
    binop =
      op.getType match
        case DslLexer.MUL => Mul
        case DslLexer.DIV => Div
        case DslLexer.ADD => Add
        case DslLexer.SUB => Sub
  } yield Expression(EBinOp(left, right, binop))

  /** '(' expr ')' */
  override def visitParens(ctx: ParensContext): Option[Statement] =
    for {
      e <- visitExp(ctx.expr())
    } yield Expression(e)
