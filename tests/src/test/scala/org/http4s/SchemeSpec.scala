package org.http4s

import cats.kernel.laws.OrderLaws
import org.http4s.internal.parboiled2.CharPredicate
import org.http4s.testing.HttpCodecTests
import org.http4s.util.Renderer

class SchemeSpec extends Http4sSpec {
  "equals" should {
    "be consistent with equalsIgnoreCase of the values" in {
      prop { (a: Scheme, b: Scheme) =>
        (a == b) must_== a.value.equalsIgnoreCase(b.value)
      }
    }
  }

  "compareTo" should {
    "be consistent with value.compareToIgnoreCase" in {
      prop { (a: Scheme, b: Scheme) =>
        a.value.compareToIgnoreCase(b.value) must_== a.compareTo(b)
      }
    }
  }

  "hashCode" should {
    "be consistent with equality" in {
      prop { (a: Scheme, b: Scheme) =>
        (a == b) ==> (a.## must_== b.##)
      }
    }
  }

  "render" should {
    "return value" in prop { s: Scheme =>
      Renderer.renderString(s) must_== s.value
    }
  }

  "fromString" should {
    "reject all invalid schemes" in { s: String =>
      (s.isEmpty ||
      !CharPredicate.Alpha(s.charAt(0)) ||
      !s.forall(CharPredicate.Alpha ++ CharPredicate(".-+"))) ==>
        (Scheme.parse(s) must beLeft)
    }
  }

  "literal syntax" should {
    "accept valid literals" in {
      scheme"https" must_== Scheme.https
    }

    "reject invalid literals" in {
      illTyped("""scheme"нет"""")
      true
    }
  }

  checkAll("order", OrderLaws[Scheme].order)
  checkAll("httpCodec", HttpCodecTests[Scheme].httpCodec)
}
