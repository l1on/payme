package controllers

import javax.inject._

import models.{JsonFailure, NotFoundFailure, Task}
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TaskController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def list = Action {
    Ok(Json.toJson(Task.all))
  }

  def create = Action(parse.json){ request =>
    Task.add(request.body.as[JsObject]) match {
      case Left(e) => BadRequest(Json.obj("message" -> "invalid json for a task"))
      case Right(task) => Created(Json.toJson(task)).withHeaders(
        play.api.http.HeaderNames.LOCATION -> routes.TaskController.get(task.id).url
      )
    }
  }

  def deleteAll = Action {
    Task.removeAll
    NoContent
  }

  def delete(id: Int) = Action {
    Task.remove(id) match {
      case Some(task) => NoContent
      case None => NotFound(Json.obj("message" -> "no such task"))
    }
  }

  def get(id: Int) = Action {
    Task.find(id) match {
      case Some(task) => Ok(Json.toJson(task))
      case None => NotFound(Json.obj("message" -> "no such task"))
    }
  }

  def update(id: Int) = Action(parse.json) { request =>
    Task.update(id, request.body.as[JsObject]) match {
      case Left(e) => e match {
        case _: JsonFailure => BadRequest(Json.obj("message" -> "invalid json for a task"))
        case _: NotFoundFailure => NotFound(Json.obj("message" -> "no such task"))
      }
      case Right(task) => NoContent.withHeaders(
        play.api.http.HeaderNames.LOCATION -> routes.TaskController.update(task.id).url
      )
    }
  }

}