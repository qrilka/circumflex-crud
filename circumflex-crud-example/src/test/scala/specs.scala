package ru.qrilka.circumflex.crud.example

import ru.circumflex._, core._, web._

import org.specs.runner.JUnit4
import org.specs.Specification

class SpecsTest extends JUnit4(MySpec)

object MySpec extends Specification {

  doBeforeSpec {
    cx("cx.router") = classOf[Main]
    MockApp.start
  }

  doAfterSpec {
    MockApp.stop
  }

  "Book application" should {
    "show new form" in {
      val result = MockApp.get("/new").execute()
      result.getStatus must_== 200
      result.getContent must notBeEmpty
    }
  }
}