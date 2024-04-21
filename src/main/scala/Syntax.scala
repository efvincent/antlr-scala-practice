package syntax

enum Statement:
  case Expression(expr: Expr)
  case Print(expr: Expr)
  case Assign(name: String, expr: Expr)
  case Block(statements: List[Statement])
  case Clear

enum Expr:
  case EBinOp(lhs: Expr, rhs: Expr, op: BinOp)
  case EInt(value: Int)
  case EId(name: String)

enum BinOp:
  case Mul, Div, Add, Sub
