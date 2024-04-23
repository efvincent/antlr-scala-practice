package visitors

import org.antlr.v4.runtime.*
import syntax.BinOp.*
import syntax.Expr.*
import syntax.Statement.*
import syntax.*
import util.*

import com.aetion.acal.sl.parser.DslParser.*
import com.aetion.acal.sl.parser.*

object SmartTreeWalker:

  def walkTree(tree: ParserRuleContext): Statement =
    val stmts = rulesFrom(tree, RULE_stat)
    Block(stmts.flatMap(walkStat))

  def walkStat(stat: ParserRuleContext): Option[Statement] =
    stat match
      case ctx: PrintExprContext =>
        for {
          exprCtx <- ruleFrom(ctx, RULE_expr)
          expr    <- walkExpr(exprCtx)
        } yield Print(expr)

      case ctx: AssignContext =>
        for {
          varName <- strFrom(ctx, ID)
          eCtx    <- ruleFrom(ctx, RULE_expr)
          expr    <- walkExpr(eCtx)
        } yield Assign(varName, expr)

      case ctx: ClearContext => Some(Clear)

      case ctx: BlockStmtContext =>
        for {
          blk <- ruleFrom(ctx, RULE_block)
          stmts = rulesFrom(blk, RULE_stat)
        } yield Block(stmts.flatMap(walkStat))

      case ctx: BlankContext => None

  private def walkExpr(expr: ParserRuleContext): Option[Expr] =
    expr match
      case ctx: IntContext =>
        intFrom(ctx, INT).map(EInt.apply)

      case ctx: IdContext =>
        strFrom(ctx, ID).map(EId.apply)

      case ctx: ParensContext =>
        ruleFrom(ctx, RULE_expr).flatMap(walkExpr)

      case ctx: AddSubContext =>
        (ctx.op |> opt).flatMap(parseBinOp(ctx))

      case ctx: MulDivContext =>
        (ctx.op |> opt).flatMap(parseBinOp(ctx))

      case ctx: ExpContext =>
        for {
          termNode <- getTerminal(ctx, CARET, 0)
          token    <- termNode.getSymbol |> opt
          binop    <- parseBinOp(ctx)(token)
        } yield binop

  def parseBinOp[T <: ParserRuleContext](ctx: T)(op: Token): Option[EBinOp] =
    for {
      lhs  <- ruleFrom(ctx, RULE_expr, 0).flatMap(walkExpr)
      rhs  <- ruleFrom(ctx, RULE_expr, 1).flatMap(walkExpr)
      opId <- op.getType |> opt
      operation =
        opId match
          case MUL   => Mul
          case DIV   => Div
          case ADD   => Add
          case SUB   => Sub
          case CARET => Exponent
          case _     => ??? // should never happen
    } yield EBinOp(lhs, rhs, operation)
