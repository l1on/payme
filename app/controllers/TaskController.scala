package controllers

import javax.inject._

import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.libs.json._
import services.TaskService
import models.Task

import scala.concurrent.ExecutionContext
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TaskController @Inject()(cc: ControllerComponents, task: TaskService) (implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").writeNullable[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "completed").write[Boolean]
    )(unlift(Task.unapply))

  implicit val taskReads: Reads[Task] = (
    (JsPath \ "id").readNullable[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "completed").read[Boolean]
    )(Task.apply _)

  def validateJson[A : Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )


  def list = Action {
    Ok(Json.toJson(task.all))
  }


  def create = Action(validateJson[Task]) { request =>
    Created(Json.toJson(task.add(request.body)))
  }

  def update(id: Int) = Action(validateJson[Task]) { request =>
    task.update(id, request.body) match {
      case None => NotFound(Json.obj("message" -> "no such task"))
      case Some(task) => NoContent.withHeaders(
        play.api.http.HeaderNames.LOCATION -> routes.TaskController.update(id).url
      )
    }
  }

  def get(id: Int) = Action {
    task.find(id) match {
      case Some(task) => Ok(Json.toJson(task))
      case None => NotFound(Json.obj("message" -> "no such task"))
    }
  }

  def delete(id: Int) = Action {
    task.remove(id) match {
      case Some(task) => NoContent
      case None => NotFound(Json.obj("message" -> "no such task"))
    }
  }

}