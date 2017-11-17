package services

import play.api.libs.json._
import play.api.libs.functional.syntax._
import repositories.tasks.TaskRepository
import models.Task
import javax.inject._

trait UpdateFailure
case class UpdateFailureJson() extends UpdateFailure
case class UpdateFailureNotFound() extends UpdateFailure
case class UpdateSuccess()

class TaskService @Inject()(taskRepository: TaskRepository) {

  def all: List[Task] = {
    taskRepository.readAll
  }

  def find(id: Int): Option[Task] = {
    taskRepository.read(id)
  }

  def add(task: Task): Task = {
    taskRepository.create(task.name, task.completed)
  }

  def update(id: Int, task: Task): Option[Task] = {
    taskRepository.update(id, task.name, task.completed)
  }

  def remove(id: Int): Option[Task] = {
    taskRepository.delete(id)
  }
}
