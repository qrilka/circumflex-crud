package ru.qrilka.circumflex.crud.example

import ru.circumflex._, core._, web._, freemarker._
import ru.qrilka.circumflex.crud.CRUDRouter

class Main extends RequestRouter {
  any("/*") = new CRUDRouter[Book](Book)
}