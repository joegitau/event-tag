package com.joegitau._playground

object Test {
/*
  import akka.actor.typed.scaladsl.Behaviors
  import akka.actor.typed.{ActorRef, Behavior}
  import models.{MyModel, MyTable}
  import slick.jdbc.PostgresProfile.api._

  object MyActor {
    sealed trait Command
    final case class Get(id: Int, replyTo: ActorRef[Option[MyModel]]) extends Command
    final case class Add(name: String, replyTo: ActorRef[Int]) extends Command
    private final case class Reply[T](result: T)

    def apply(db: Database): Behavior[Command] = {
      val myTable = TableQuery[MyTable]

      Behaviors.receive { (context, message) =>
        message match {
          case Get(id, replyTo) =>
            val result = db.run(myTable.filter(_.id === id).result.headOption)
            result.onComplete { result =>
              context.self ! Reply(result.toOption.flatten)
            }
            Behaviors.same

          case Add(name, replyTo) =>
            val model = MyModel(0, name)
            val insertAction = (myTable returning myTable.map(_.id)) += model
            val result = db.run(insertAction)
            result.onComplete { result =>
              context.self ! Reply(result.getOrElse(0))
            }
            Behaviors.same

          case Reply(result) =>
            result match {
              case Some(model) => context.log.info(s"Replying with $model")
              case id: Int => context.log.info(s"Replying with id $id")
              case _ => context.log.info("Replying with None")
            }
            Behaviors.same
        }
      }
    }
  }
*/

}
