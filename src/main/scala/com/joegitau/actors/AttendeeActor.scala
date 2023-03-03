package com.joegitau.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.pattern.StatusReply
import akka.pattern.StatusReply.ErrorMessage
import com.joegitau.protocol.AttendeeProtocol.AttendeeCommand
import com.joegitau.protocol.AttendeeProtocol.AttendeeResponse._
import com.joegitau.services.AttendeeService

import scala.util.{Failure, Success}

object AttendeeActor {
  def apply(attendeeService: AttendeeService): Behavior[AttendeeCommand] = Behaviors.receive { (ctx, msg) =>
    implicit val ec = ctx.system.executionContext

    ctx.log.info("::: Attendee actor started. :::")

    msg match {
      case AttendeeCommand.CreateAttendee(attendee, replyTo)       =>
        attendeeService.createAttendee(attendee).onComplete {
          case Success(attendee) => replyTo ! StatusReply.success(CreateAttendeeRsp(attendee))
          case Failure(ex)       => replyTo ! StatusReply.error(ErrorMessage(s"Failed to create attendee. ${ex.getMessage}"))
        }
        Behaviors.same

      case AttendeeCommand.GetAttendee(id, replyTo)                =>
        attendeeService.getAttendeeById(id).onComplete {
          case Success(maybeAttendee) => replyTo ! StatusReply.success(GetAttendeeRsp(maybeAttendee))
          case Failure(ex)            => replyTo ! StatusReply.error(ErrorMessage(s"Failed to fetch attendee with id: $id. ${ex.getMessage}"))
        }
        Behaviors.same

      case AttendeeCommand.GetAttendees(replyTo)                   =>
        attendeeService.getAllAttendees.onComplete {
          case Success(attendees) => StatusReply.success(GetAttendeesRsp(attendees.toList))
          case Failure(ex)        => replyTo ! StatusReply.error(ErrorMessage(s"Failed to fetch attendees. ${ex.getMessage}"))
        }
        Behaviors.same

      case AttendeeCommand.UpdateAttendee(id, attendee, replyTo)   =>
        attendeeService.updateAttendee(id, attendee).onComplete {
          case Success(updatedAttendee) => replyTo ! StatusReply.success(UpdateAttendeeRsp(updatedAttendee))
          case Failure(ex)              => replyTo ! StatusReply.error(ErrorMessage(s"Failed to update attendee with id: $id. ${ex.getMessage}"))
        }
        Behaviors.same

      case AttendeeCommand.DeleteAttendee(id, replyTo)             =>
        attendeeService.deleteAttendee(id).onComplete {
          case Success(_)  => replyTo ! StatusReply.success(DeleteAttendeeRsp(id))
          case Failure(ex) => replyTo ! StatusReply.error(ErrorMessage(s"Failed to delete attendee with id: $id. ${ex.getMessage}"))
        }
        Behaviors.same
    }
  }
}
