package repositories.tasks

import models.Task

trait TaskRepository {
  def create(name: String, completed: Boolean): Task
  def read(id: Int): Option[Task]
  def readAll: List[Task]
  def update(id: Int, name: String, completed: Boolean): Option[Task]
  def delete(id: Int):Option[Task]
}
