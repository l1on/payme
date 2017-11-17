import com.google.inject.AbstractModule
import repositories.tasks.{InMemoryTaskRepository, TaskRepository}

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[TaskRepository]).to(classOf[InMemoryTaskRepository])
  }
}
