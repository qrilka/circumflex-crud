package ru.qrilka.circumflex.crud

import ru.circumflex._, core._, orm._, web._, freemarker._
import java.lang.reflect.Method
import scala.util.control.Exception._

trait CRUDTable[R <: CRUDRecord[R]] extends Table[Long, R] {this: R =>
  def countAll() : Long =
    (this AS "r").map(r => SELECT(COUNT(string2predicate("*"))).FROM(r).unique.get)

  def findAll() : Seq[R] =
    (this AS "r").map(r => SELECT(r.*).FROM(r).list)

  def findWithLimit(limit: Int, offset: Int) : Seq[R] =
    (this AS "r").map(r => (SELECT (r.*) FROM(r) LIMIT limit OFFSET offset).list)
}

abstract class CRUDRecord[R <: Record[Long, R]] extends Record[Long, R] with IdentityGenerator[Long, R]{ this: R => }

class CRUDRouter[R <: CRUDRecord[R]](val table: CRUDTable[R],
                                       override val prefix: String = "",
                                       val ftlDir: String = "",
                                       val itemsPerPage: Int = 10)
                                      (implicit manifest : Manifest[R]) extends RequestRouter(prefix) {

  def toIntOrElse(s: String, default: Int = 0) =
    handling(classOf[NumberFormatException]).by(_ => default) { s.toInt }

  get("/?") = {
    val totalCount = table.countAll()
    val offset = 1//toIntOrElse(param("offset")) min (totalCount - 1)
    'records := table.findWithLimit(itemsPerPage, offset)
    showFtl("list.ftl")
  }

  get("/new") = {
    'record := manifest.erasure.newInstance.asInstanceOf[R];
    showFtl("new.ftl")
  }

  post("/new") = {
    var record = this.fillRecord()
    try tx {
      record.INSERT()
      flash("message") = "Record created"
    } catch {
      case e: ValidationException =>
        // we did not pass validation: show messages to user
        'errors := e.errors.by { e =>
          e.param("field") match {
            case Some(f: Field[_, _]) => f.name
            case _ => ""
          }
        }
        'record := record
        showFtl("new.ftl")
      case _ =>
        sendError(404)
    }
    redirect(joinPath(prefix, param("id"), absolute = true))
  }

  get("/:id") = table.get(param("id").toLong) match {
    case Some(record) =>
      'record := record
      showFtl("edit.ftl")
    case _ => sendError(404)
  }

  post("/:id") = table.get(param("id").toLong) match {
    case Some(_) =>
      var record = this.fillRecord()
      try tx {
        record.save()
        flash("message") = "Record saved"
      } catch {
        case e: ValidationException =>
          // we did not pass validation: show messages to user
          'errors := e.errors.by { e =>
            e.param("field") match {
              case Some(f: Field[_, _]) => f.name
              case _ => ""
            }
          }
          'record := record
          showFtl("edit.ftl")
        case _ =>
          sendError(404)
      }
      redirect(joinPath(prefix, param("id"), absolute = true))
    case _ => sendError(404)
  }

  get("/:id/delete") = table.get(param("id").toLong) match {
    case Some(record) =>
      try tx {
        record.DELETE_!()
        flash("message") = "Record deleted"
      } catch {
        case _ =>
          sendError(404)
      }
      redirect("/" + prefix) //TODO remove paths magic
    case _ => sendError(404)
  }

  protected def fillRecord(): R = {
    var record: R = manifest.erasure.newInstance.asInstanceOf[R];
    record.relation.fields.foreach {
      f =>
        val m:Method = record.relation.methodsMap(f)
        var field = m.invoke(record)
        val value = field match {
          case bField: BooleanField[R] => {
            bField.set(param.contains(m.getName))
          }
          case oField: Field[Any, R] => {
            oField.set(convertValue(oField, param(oField.name)))
          }
        }
    }
    record;
  }

  private def joinPath(prefix: String, file: String, absolute: Boolean = false) = {
    val root: String = if (absolute) "/"; else ""
    if (prefix.isEmpty)
      root + file
    else
      root + prefix + "/" + file
  }

  private def showFtl(template: String): Nothing = {
    'prefix := prefix
    ftl(joinPath(ftlDir, template))
  }

  private def convertValue(field: Field[Any, _], v: String): Option[Any] = field match {
    case field: XmlSerializable[Any, _] => field.from(v)
    case _ => Some(v)
  }
}