package com.joegitau.actors

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.pattern.StatusReply
import akka.pattern.StatusReply.ErrorMessage
import akka.util.Timeout
import com.joegitau.protocol.AttendanceProtocol.AttendanceCommand
import com.joegitau.protocol.AttendanceProtocol.AttendanceResponse.{AttendanceMarkedRsp, AttendanceNotMarkedRsp}
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand.CheckAttendeeEventRelation
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationResponse.CheckAttendeeEventRelationRsp
import com.joegitau.services.AttendanceService

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object AttendanceActor {
  def apply(attendanceService: AttendanceService,
            attendeeEventRelActorRef: ActorRef[AttendeeEventRelationCommand]
           ): Behavior[AttendanceCommand] = Behaviors.receive { (ctx, msg) =>

    implicit val ec: ExecutionContext = ctx.executionContext
    implicit val scheduler: Scheduler = ctx.system.scheduler
    implicit val timeout: Timeout     = 3.seconds

    msg match {
      case AttendanceCommand.MarkAttendance(eventId, attendeeId, checkinTime, checkoutTime, replyTo) =>
        attendeeEventRelActorRef.ask(CheckAttendeeEventRelation(attendeeId, eventId, _))
          .onComplete {
            case Success(resp) => resp match {
              case CheckAttendeeEventRelationRsp(exists) =>
                if (exists) {
                  attendanceService.markAttendance(eventId, attendeeId, checkinTime, checkoutTime)

                  replyTo ! StatusReply.success(AttendanceMarkedRsp(eventId, attendeeId))
                } else {
                  replyTo ! StatusReply.success(AttendanceNotMarkedRsp("Attendee has no relation to this event!"))
                }
            }
            case Failure(exception) =>
              replyTo ! StatusReply.error(ErrorMessage(s"Failed to check attendee event relation: ${exception.getMessage}"))
        }

        Behaviors.same
    }
  }
}

/* for {
    relationRsp <- attendeeEventRelActorRef.ask(CheckAttendeeEventRelation(attendeeId, eventId, _))
    result      = relationRsp match {
      case CheckAttendeeEventRelationRsp(exists) =>
        if (exists) {
          attendanceService.markAttendance(eventId, attendeeId, checkinTime, checkoutTime)
          replyTo ! StatusReply.success(AttendanceMarkedRsp(eventId, attendeeId))
        } else {
          replyTo ! StatusReply.success(AttendanceNotMarkedRsp("Attendee has no relation to this event!"))
        }
    }
  } yield result */
