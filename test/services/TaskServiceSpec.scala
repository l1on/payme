package services

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import scala.util.Random

import repositories.tasks.TaskRepository
import models.Task

class TaskServiceSpec extends PlaySpec with MockitoSugar{
  val taskRepository = mock[TaskRepository]
  val taskService = new TaskService(taskRepository)

  "TaskService#find" should {
    "return the Option(task) specified by the id" in {
      val taskId = 3
      val task = Task(Some(taskId), "my task", true)
      when(taskRepository.read(taskId)) thenReturn Some(task)

      taskService.find(taskId) mustEqual Some(task)
    }

    "return option none if the specified id does not exist" in {
      val taskId = 456
      when(taskRepository.read(taskId)) thenReturn None

      taskService.find(taskId) mustEqual None
    }
  }

  "TaskService#add" should {
    "return a task" in {
      val taskName = "task 1"
      val taskCompleted = false
      val savedTask = Task(Some(Random.nextInt), taskName, taskCompleted)
      when(taskRepository.create(taskName, taskCompleted)) thenReturn savedTask

      val actual = taskService.add(Task(None, taskName, taskCompleted))
      actual mustEqual savedTask
    }
  }

  "TaskService#update" should {
    "return an updated task when the task is in repo" in {
      val newTaskName = "new task"
      val newTaskComplete = false
      val taskId = 45
      val task = Task(Some(taskId), newTaskName, newTaskComplete)
      when(taskRepository.update(taskId, newTaskName, newTaskComplete)) thenReturn Some(task)

      val actual = taskService.update(taskId, Task(None, newTaskName, newTaskComplete))
      actual mustEqual Some(task)
    }

    "return None when the specified id does not exist" in {
      val taskId = 31
      val noExistantTask = Task(Some(taskId), "no exsit", false)
      when(taskRepository.update(taskId, noExistantTask.name, noExistantTask.completed)) thenReturn None

      val actual = taskService.update(taskId, noExistantTask)
      actual mustEqual None
    }
  }

  "TaskService#remove" should {
    "return the deleted task when the delete is successful" in {
      val taskId = 2
      val task = Task(Some(taskId), "task to be removed", true)
      when(taskRepository.delete(taskId)) thenReturn Some(task)

      taskService.remove(taskId) mustEqual Some(task)
    }

    "return None when the delete is not in repo" in {
      val nonExistantTaskId = 2
      val nonExistanttask = Task(Some(nonExistantTaskId), "task to be removed", true)
      when(taskRepository.delete(nonExistantTaskId)) thenReturn None

      taskService.remove(nonExistantTaskId) mustEqual None
    }
  }
}

