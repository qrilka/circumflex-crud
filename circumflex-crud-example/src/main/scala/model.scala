package ru.qrilka.circumflex.crud.example

import ru.qrilka.circumflex.crud._

class Book extends CRUDRecord[Book] {
  def relation = Book
  def PRIMARY_KEY = id

  val id = "id".BIGINT.NOT_NULL.AUTO_INCREMENT
  val name = "name".VARCHAR(255).NOT_NULL
  val authors = "authors".VARCHAR(255).NOT_NULL
  val description = "description".TEXT.NOT_NULL
}

object Book extends Book with CRUDTable[Book] {
  validation.notNull(_.name)
          .notNull(_.authors)
          .notNull(_.description)
}