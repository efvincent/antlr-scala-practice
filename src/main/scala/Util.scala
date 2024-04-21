package util

import scala.jdk.CollectionConverters.*
import org.antlr.v4.runtime.ParserRuleContext

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
  if a == null
  then None
  else a

/** Converts a java style nullable value of type `A` into an `Option[A]`
  *
  * @param a the nullable value
  * @return if the value is `null` then `None` else `Some(a)`
  */
def opt[A](a: A | Null): Option[A] =
  if a == null then None else Some(a)

def strFrom[T <: ParserRuleContext](ctx: T, tokenId: Int, idx: Int = 0)
    : Option[String] =
  for {
    tkn <- ctx.getToken(tokenId, idx) |> opt
    txt <- tkn.getText() |> opt
  } yield txt

def intFrom[T <: ParserRuleContext](ctx: T, tokenId: Int, idx: Int = 0)
    : Option[Int] =
  for {
    txt <- strFrom(ctx, tokenId, idx)
    n   <- txt.toIntOption
  } yield n
