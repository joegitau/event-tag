package com.joegitau.protocol

import akka.actor.typed.ActorRef
import akka.pattern.StatusReply
import com.joegitau.model.Attendee
import com.joegitau.serialization.CborSerializable

object AttendeeProtocol {
  sealed trait AttendeeCommand extends CborSerializable
  object AttendeeCommand {
    case class CreateAttendee(attendee: Attendee, replyTo: ActorRef[StatusReply[AttendeeResponse]])           extends AttendeeCommand
    case class GetAttendee(id: Long, replyTo: ActorRef[StatusReply[AttendeeResponse]])                        extends AttendeeCommand
    case class GetAttendees(replyTo: ActorRef[StatusReply[AttendeeResponse]])                                 extends AttendeeCommand
    case class UpdateAttendee(id: Long, attendee: Attendee, replyTo: ActorRef[StatusReply[AttendeeResponse]]) extends AttendeeCommand
    case class DeleteAttendee(id: Long, replyTo: ActorRef[StatusReply[AttendeeResponse]])                     extends AttendeeCommand
  }

  sealed trait AttendeeResponse extends CborSerializable
  object AttendeeResponse {
    case class CreateAttendeeRsp(attendee: Attendee)                extends AttendeeResponse
    case class GetAttendeeRsp(maybeAttendee: Option[Attendee])      extends AttendeeResponse
    case class GetAttendeesRsp(attendees: List[Attendee])           extends AttendeeResponse
    case class UpdateAttendeeRsp(updatedAttendee: Option[Attendee]) extends AttendeeResponse
    case class DeleteAttendeeRsp(id: Long)                          extends AttendeeResponse
  }
}
