package com.joegitau.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.pattern.StatusReply
import akka.pattern.StatusReply.ErrorMessage
import com.joegitau.model.{AttendeeWithEvents, EventWithAttendees}
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationResponse._
import com.joegitau.services.AttendeeEventRelationService
import com.joegitau.utils.Helpers.OptionFns

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object AttendeeEventRelationActor {
  def apply(attendeeEventRelationService: AttendeeEventRelationService): Behavior[AttendeeEventRelationCommand] = Behaviors.receive { (ctx, msg) =>
    implicit val ec: ExecutionContextExecutor = ctx.system.executionContext

    ctx.log.info("::: AttendeeEventRelation actor started. :::")

    msg match {
      case AttendeeEventRelationCommand.AddAttendeeToEvent(eventId, attendeeId, replyTo)         =>
        attendeeEventRelationService.addAttendeeToEvent(eventId, attendeeId).onComplete {
          case Success(_)  =>
            replyTo ! StatusReply.success(AddAttendeeToEventRsp(eventId, attendeeId))
          case Failure(ex) =>
            replyTo ! StatusReply.error(ErrorMessage(s"Could not add attendee: $attendeeId to event: $eventId : ${ex.getMessage}"))
        }

        Behaviors.same

      case AttendeeEventRelationCommand.GetEventWithAttendees(eventId, replyTo)                  =>
        attendeeEventRelationService.getAllAttendeesByEventId(eventId).onComplete {
          case Success(attendees) =>
            if (attendees.nonEmpty) {
              replyTo ! StatusReply.success(GetEventWithAttendeesRsp(EventWithAttendees(eventId, attendees).toOpt))
            } else {
              replyTo ! StatusReply.success(GetEventWithAttendeesRsp(EventWithAttendees(eventId, Seq.empty).toOpt))
            }
          case Failure(ex)        =>
            replyTo ! StatusReply.error(ErrorMessage(s"Event with id: $eventId not found. : ${ex.getMessage}"))
        }

        Behaviors.same

      case AttendeeEventRelationCommand.GetAttendeeWithEvents(attendeeId, replyTo)               =>
        attendeeEventRelationService.getEventsForAttendee(attendeeId).onComplete {
          case Success(events) =>
            if (events.nonEmpty) {
              replyTo ! StatusReply.success(GetAttendeeWithEventsRsp(AttendeeWithEvents(attendeeId, events).toOpt))
            } else {
              replyTo ! StatusReply.success(GetAttendeeWithEventsRsp(AttendeeWithEvents(attendeeId, Seq.empty).toOpt))
            }
          case Failure(ex)     =>
            replyTo ! StatusReply.error(ErrorMessage(s"Attendee with id: $attendeeId not found. : ${ex.getMessage}"))
        }

        Behaviors.same

      case AttendeeEventRelationCommand.CheckAttendeeEventRelation(attendeeId, eventId, replyTo) =>
        attendeeEventRelationService.attendeeEventRelationExists(attendeeId, eventId).onComplete {
          case Success(exists) =>
            replyTo ! CheckAttendeeEventRelationRsp(exists)
          case Failure(_)      =>
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
