package util

import scala.jdk.CollectionConverters.*

/** Given a possibly null `java.util.list[T]`, return the option
  * of a scalal list
  *
  * @param jlist the java list to turn into a scala list
  * @return the scala list
  */
def unJList[T](jlist: java.util.List[T] | Null): Option[List[T]] =
  for {
    jl <- jlist |> opt
  } yield jl.asScala.toList

/** Convert a possibly null java list of `T` into an `Option[V]`
  *
  * @param jlist possibly null `java.util.List<T>`
  * @param fmapper function to flat map from `T` to `Option[U]`
  * @param toAst function from `List[U]` to `V`
  * @return `Some[V]` if `jlist` is not null
  */
def flatMapJlist[T, U, V](
    jlist: java.util.List[T] | Null,
    fmapper: T => Option[U] | Null,
    toAst: List[U] => V): Option[V] =
  for {
    lst <- unJList(jlist)
  } yield toAst(lst.flatMap(fmapper <| opt))

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

implicit class PipeOps[A](val a: A):
  inline def |>[B](f: A => B): B = f(a)

implicit class ComposeOps[A, B](fn: A => B):
  inline def <|[C](f2: B => C): A => C = x => f2(fn(x))
