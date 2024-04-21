import sbt.*

object Dependencies {

  /** Utility function that makes a function for defining module IDs to be used
    * in the dependencies section of the sbt build
    *
    * @param groupId group ID of the library to import
    * @param version version number for the group
    * @return function that defines the dependency as a ModuleId
    */
  private def mkModIdFn(groupId: String, version: String): String => ModuleID =
    (artifact: String) => groupId %% artifact % version

  object com {
    object eed3si9n {
      val expecty =
        "com.eed3si9n.expecty" %% "expecty" % "0.16.0"
    }

    object lihaoyi {
      val modId     = mkModIdFn("com.lihaoyi", "3.0.2")
      val fastparse = modId("fastparse")
    }
  }

  object dev {
    object zio {
      private val modId       = mkModIdFn("dev.zio", "2.1-RC1")
      val zio                 = modId("zio")
      val `zio-streams`       = modId("zio-streams")
      val `zio-test`          = modId("zio-test")
      val `zio-test-sbt`      = modId("zio-test-sbt")
      val `zio-test-magnolia` = modId("zio-test-magnolia")
      val `zio-prelude`       = "dev.zio" %% "zio-prelude" % "1.0.0-RC22"
    }
  }

  object org {
    object typelevel {
      val modId         = mkModIdFn("org.typelevel", "2.10.0")
      val `cats-kernel` = modId("cats-kernel")
      val `cats-core`   = modId("cats-core")
      val `discipline-munit` =
        "org.typelevel" %% "discipline-munit" % "1.0.9"
    }

    object scalacheck {
      val modId      = mkModIdFn("org.scalacheck", "1.17.0")
      val scalacheck = modId("scalacheck")
    }

    object scalameta {
      private val modId      = mkModIdFn("org.scalameta", "1.0.0-M10")
      val munit              = modId("munit")
      val `munit-scalacheck` = modId("munit-scalacheck")
    }
  }
}
