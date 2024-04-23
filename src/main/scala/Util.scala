package util

import scala.jdk.CollectionConverters.*

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.TerminalNode
import zio.prelude.*

// scalafix:off DisableSyntax.null
// scalafix:off DisableSyntax.==
// scalafix:off DisableSyntax.defaultArgs
// scalafix:off DisableSyntax.asInstanceOf
// scalafix:off DisableSyntax.isInstanceOf

implicit class PipeOps[A](val a: A):
  inline def |>[B](f: A => B): B = f(a)

implicit class ComposeOps[A, B](fn: A => B):
  inline def <|[C](f2: B => C): A => C = x => f2(fn(x))

/** Given a possibly null `java.util.list[T]`, return the option
  * of a scalal list
  *
  * @param jlist the java list to turn into a scala list
  * @return the scala list
  */
def unJList[T](jlist: java.util.List[T] | Null): List[T] =
  jlist |> opt match
    case None     => List[T]()
    case Some(jl) => jl.asScala.toList

/** Convert a possibly null java list of `T` into a `List[V]`
  *
  * @param jlist possibly null `java.util.List<T>`
  * @param fmapper function to flat map from `T` to `Option[U]`
  * @return `List[U]` which is empty if `jlist` is null
  */
def flatMapJlist[T, U](
    jlist: java.util.List[T] | Null,
    fmapper: T => Option[U] | Null): List[U] =
  val lst = unJList(jlist)
  lst.flatMap(fmapper <| opt)

/** Convert a possibly null java list of `T` into a `List[V]`
  *
  * @param jlist possibly null `java.util.List<T>`
  * @param fmapper function to flat map from `T` to `Option[U]`
  * @return `List[U]` which is empty if `jlist` is null
  */
def flatMapJlist2[T, U](
    jlist: java.util.List[T] | Null,
    fmapper: T => Option[U]): List[U] =
  val lst = unJList(jlist)
  lst.flatMap(fmapper)

def opt[A](a: Option[A] | Null): Option[A] =
  a match
    case None | null => None
    case Some(value) => Some(value)

/** Converts a java style nullable value of type `A` into an `Option[A]`
  *
  * @param a the nullable value
  * @return if the value is `null` then `None` else `Some(a)`
  */
def opt[A](a: A | Null): Option[A] =
  if a == null then None else Some(a)

def getTerminal[T <: ParserRuleContext](ctx: T, tokenId: Int, idx: Int = 0)
    : Option[TerminalNode] =
  ctx.getToken(tokenId, idx) |> opt

def strFrom[T <: ParserRuleContext](ctx: T, tokenId: Int, idx: Int = 0)
    : Option[String] =
  for {
    tkn <- getTerminal(ctx, tokenId, idx) |> opt
    txt <- tkn.getText() |> opt
  } yield txt

def intFrom[T <: ParserRuleContext](ctx: T, tokenId: Int, idx: Int = 0)
    : Option[Int] =
  for {
    txt <- strFrom(ctx, tokenId, idx)
    n   <- txt.toIntOption
  } yield n

def rulesFrom[T <: ParserRuleContext](
    ctx: T,
    ruleId: Int): List[ParserRuleContext] =
  val subs = ctx.children |> unJList
  subs
    .filter(r =>
      r.isInstanceOf[ParserRuleContext] && r
        .asInstanceOf[ParserRuleContext]
        .getRuleIndex === ruleId
    )
    .map(_.asInstanceOf[ParserRuleContext])

def ruleFrom[T <: ParserRuleContext](
    ctx: T,
    ruleId: Int,
    idx: Int = 0): Option[ParserRuleContext] =
  val subs = ctx.children |> unJList
  val rules =
    subs.filter(sub =>
      if sub.isInstanceOf[RuleContext] then
        val rule: RuleContext = sub.asInstanceOf[T]
        rule.getRuleIndex === ruleId
      else false
    )
  if rules.isEmpty || idx >= rules.size
  then None
  else Some(rules(idx).asInstanceOf[T])

def ruleFromOpt[T <: ParserRuleContext](
    ctx: Option[T],
    ruleId: Int,
    idx: Int = 0): Option[ParserRuleContext] =
  ctx match
    case Some(ctx1) => ruleFrom(ctx1, ruleId, idx)
    case None       => None
