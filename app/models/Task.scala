package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

trait UpdateFailure
case class JsonFailure() extends UpdateFailure
case class NotFoundFailure() extends UpdateFailure

case class Task(id: Int, name: String, completed: Boolean)

object Task {
  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "completed").write[Boolean]
    )(unlift(Task.unapply))

  implicit val taskReads: Reads[Task] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "completed").read[Boolean]
    )(Task.apply _)

  private var list: List[Task] = List()

  def find(id: Int): Option[Task] = list.find(task => task.id == id)

  def all: List[Task] = list

  def removeAll: Unit = {
    list = List()
  }

  def remove(id: Int): Option[Task] = {
    val(removed, remaining) = list.partition(task => task.id == id)

    removed match {
      case List() => None
      case _ => {
        list = remaining
        Some(removed.head)
      }
    }
  }

  def add(taskJson: JsObject): Either[JsError, Task] = {
    val newId = list.lastOption match {
      case Some(task) => task.id + 1
      case None => 1
    }

    (taskJson + ("id" -> JsNumber(newId))).validate[Task] match {
      case e: JsError => Left(e)
      case s: JsSuccess[Task] => {
        list = list ::: List(s.get)
        Right(list.last)
      }
    }
  }

  def update(id: Int, taskJson: JsObject): Either[UpdateFailure, Task] = {
    (taskJson + ("id" -> JsNumber(id))).validate[Task] match {
      case e: JsError => Left(JsonFailure())
      case s: JsSuccess[Task] => {
        val task = s.get
        list.indexWhere(task => task.id == id) match {
          case -1 => Left(NotFoundFailure())
          case n => {
            list = list.updated(n, task)
            Right(task)
          }
        }
      }
    }
  }




}


