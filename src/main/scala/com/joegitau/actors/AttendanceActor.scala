package com.joegitau.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.joegitau.protocol.AttendanceProtocol.AttendanceCommand
import com.joegitau.protocol.AttendeeProtocol.AttendeeCommand
import com.joegitau.protocol.EventProtocol.EventCommand
import com.joegitau.services.AttendanceService

import scala.concurrent.ExecutionContext

object AttendanceActor {
  def apply(attendanceService: AttendanceService,
            eventActorRef: ActorRef[EventCommand],
            attendeeActorRef: ActorRef[AttendeeCommand]
           ): Behavior[AttendanceCommand] = Behaviors.receive { (ctx, msg) =>
    implicit val ec: ExecutionContext = ctx.executionContext

    msg match {
      case AttendanceCommand.MarkAttendance(eventId, attendeeId, checkinTime, checkoutTime, replyTo) => {
        ???
      }
    }
  }
}
