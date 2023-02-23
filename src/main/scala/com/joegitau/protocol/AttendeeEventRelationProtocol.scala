package com.joegitau.protocol

import akka.actor.typed.ActorRef
import akka.pattern.StatusReply
import com.joegitau.serialization.CborSerializable

object AttendeeEventRelationProtocol {
  sealed trait AttendeeEventRelationCommand extends CborSerializable
  object AttendeeEventRelationCommand {
    case class CheckAttendeeEventRelation(
      attendeeId: Long,
      eventId:    Long,
      replyTo:    ActorRef[StatusReply[AttendeeEventRelationResponse]]
    ) extends AttendeeEventRelationCommand
  }

  sealed trait AttendeeEventRelationResponse extends CborSerializable
  object AttendeeEventRelationResponse {
    case class CheckAttendeeEventRelationRsp(exists: Boolean) extends AttendeeEventRelationResponse
  }

}
