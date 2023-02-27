package com.joegitau.protocol

import akka.actor.typed.ActorRef
import akka.pattern.StatusReply
import com.joegitau.serialization.CborSerializable

import java.time.Instant

object AttendanceProtocol {
  sealed trait AttendanceCommand extends CborSerializable
  object AttendanceCommand {
    case class MarkAttendance(
      eventId:      Long,
      attendeeId:   Long,
      checkinTime:  Option[Instant],
      checkoutTime: Option[Instant],
      replyTo:      ActorRef[StatusReply[AttendanceResponse]]
    ) extends AttendanceCommand
  }

  sealed trait AttendanceResponse extends CborSerializable
  object AttendanceResponse {
    case class AttendanceMarkedRsp(eventId: Long, attendeeId: Long) extends AttendanceResponse
    case class AttendanceNotMarkedRsp(reason: String)               extends AttendanceResponse
  }
}
