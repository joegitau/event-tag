package com.joegitau.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationResponse.CheckAttendeeEventRelationRsp
import com.joegitau.services.AttendeeEventRelationService

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object AttendeeEventRelationActor {
  def apply(attendeeEventRelationService: AttendeeEventRelationService): Behavior[AttendeeEventRelationCommand] = Behaviors.receive { (ctx, msg) =>
    implicit val ec: ExecutionContextExecutor = ctx.system.executionContext

    ctx.log.info("::: AttendeeEventRelation actor started. :::")

    msg match {
      case AttendeeEventRelationCommand.CheckAttendeeEventRelation(attendeeId, eventId, replyTo) =>
        attendeeEventRelationService.attendeeEventRelationExists(attendeeId, eventId).onComplete {
          case Success(exists) =>
            replyTo ! CheckAttendeeEventRelationRsp(exists)
          case Failure(ex)     =>
            replyTo ! CheckAttendeeEventRelationRsp(false)

        }

        Behaviors.same
    }
  }

}
 /**
  * NOTES:
  * `ctx.system.executionContext` returns the execution context of the actor system which is the parent of the actor.
  *  This means that it may be shared with other actors in the same actor system, which can be a good thing as it allows
  *  for more efficient use of system resources.
  *
  *  `ctx.executionContext` returns the execution context of the actor itself.
  * This means that it is local to the actor and not shared with any other actors.
  * This can be beneficial in some cases where you want to ensure that the execution context is dedicated to a single
  * actor and not shared with any other actors.
  *
  * Long story short, it is recommended to use `ctx.system.executionContext` .
  */
