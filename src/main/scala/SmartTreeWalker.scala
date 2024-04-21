package visitors

import scala.annotation.targetName
import org.antlr.v4.runtime.tree.*
import syntax.BinOp.*
import syntax.Expr.*
import syntax.Statement.*
import syntax.*
import util.*

import com.aetion.acal.sl.parser.DslParser.*
import com.aetion.acal.sl.parser.*
import org.antlr.v4.runtime.Token

object SmartTreeWalker:

  def walkTree(tree: DslParser.ProgContext): Statement =
    val stmts = tree.stat() |> unJList
    Block(stmts.flatMap(walkStat))

  def walkStat(stat: StatContext): Option[Statement] =
    stat match
      case ctx: PrintExprContext =>
        walkExpr(ctx.expr.nn).map(Print.apply)

      case ctx: AssignContext =>
        for {
          id   <- ctx.ID.nn.getText |> opt
          eCtx <- ctx.expr |> opt
          expr <- walkExpr(eCtx)
        } yield Assign(id, expr)

      case ctx: ClearContext => Some(Clear)

      case ctx: BlankContext => None

  private def walkExpr[T <: ExprContext](expr: T): Option[Expr] =
    expr match
      case ctx: IntContext    => intFrom(ctx, INT).map(EInt.apply)
      case ctx: IdContext     => strFrom(ctx, ID).map(EId.apply)
      case ctx: ParensContext => walkExpr(ctx.expr.nn)

      case ctx: AddSubContext =>
        for {
          op  <- ctx.op |> opt
          lhs <- walkExpr(ctx.expr(0).nn)
          rhs <- walkExpr(ctx.expr(1).nn)
        } yield parseBinOp(op, lhs, rhs)

      case ctx: MulDivContext =>
        for {
          op  <- ctx.op |> opt
          lhs <- walkExpr(ctx.expr(0).nn)
          rhs <- walkExpr(ctx.expr(1).nn)
        } yield parseBinOp(op, lhs, rhs)

  def parseBinOp(op: Token, lhs: Expr, rhs: Expr): EBinOp =
    val operator = op.getType |> opt match
      case Some(MUL) => Mul
      case Some(DIV) => Div
      case Some(ADD) => Add
      case Some(SUB) => Sub
      case _         => ??? // should never happen
    EBinOp(lhs, rhs, operator)
