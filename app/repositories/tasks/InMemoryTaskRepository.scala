package repositories.tasks

import javax.inject._

import models.Task

@Singleton
class InMemoryTaskRepository extends TaskRepository {
  private var list: List[Task] = List()

  override def create(name: String, completed: Boolean): Task = {
    val newId = list.lastOption match {
      case Some(task) => task.id.get + 1
      case None => 1
    }
    list = Task(Some(newId), name, completed) :: list
    list.head
  }

  override def read(id: Int): Option[Task] = list.find(task => task.id == Some(id))

  override def readAll: List[Task] = list

  override def update(id: Int, name: String, completed: Boolean): Option[Task] = {
    list.indexWhere(existingTask => existingTask.id == id) match {
      case -1 => None
      case n => {
        list = list.updated(n, Task(Some(id), name, completed))
        Some(list(n))
      }
    }
  }

  override def delete(id: Int): Option[Task] = {
    val(removed, remaining) = list.partition(task => task.id == Some(id))

    removed match {
      case List() => None
      case _ => {
        list = remaining
        Some(removed.head)
      }
    }
  }

}
