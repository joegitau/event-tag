package com.joegitau.protocol

import akka.actor.typed.ActorRef
import com.joegitau.model.{AttendeeWithEvents, EventWithAttendees}
import com.joegitau.serialization.CborSerializable

object AttendeeEventRelationProtocol {
  sealed trait AttendeeEventRelationCommand extends CborSerializable
  object AttendeeEventRelationCommand {
    case class AddAttendeeToEvent(
      eventId:    Long,
      attendeeId: Long,
      replyTo:    ActorRef[AttendeeEventRelationResponse]
    ) extends AttendeeEventRelationCommand

    case class GetEventWithAttendees(eventId: Long, replyTo: ActorRef[EventWithAttendees])    extends AttendeeEventRelationCommand
    case class GetAttendeeWithEvents(attendeeId: Long, replyTo: ActorRef[AttendeeWithEvents]) extends AttendeeEventRelationCommand

    case class CheckAttendeeEventRelation(
      attendeeId: Long,
      eventId:    Long,
      replyTo:    ActorRef[AttendeeEventRelationResponse]
    ) extends AttendeeEventRelationCommand
  }

  sealed trait AttendeeEventRelationResponse extends CborSerializable
  object AttendeeEventRelationResponse {
    case class AddAttendeeToEventRsp(eventId: Long, attendeeId: Long)                   extends AttendeeEventRelationResponse
    case class GetEventWithAttendeesRsp(eventWithAttendees: Option[EventWithAttendees]) extends AttendeeEventRelationResponse
    case class CheckAttendeeEventRelationRsp(exists: Boolean)                           extends AttendeeEventRelationResponse
  }

}
