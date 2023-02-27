package com.joegitau.protocol

import akka.actor.typed.ActorRef
import akka.pattern.StatusReply
import com.joegitau.model.Event
import com.joegitau.serialization.CborSerializable

object EventProtocol {
  sealed trait EventCommand extends CborSerializable
  object EventCommand {
    case class CreateEvent(event: Event, replyTo: ActorRef[StatusReply[EventResponse]])           extends EventCommand
    case class GetEvent(id: Long, replyTo: ActorRef[StatusReply[EventResponse]])                  extends EventCommand
    case class GetAllEvents(replyTo: ActorRef[StatusReply[EventResponse]])                        extends EventCommand
    case class UpdateEvent(id: Long, event: Event, replyTo: ActorRef[StatusReply[EventResponse]]) extends EventCommand
    case class DeleteEvent(id: Long, replyTo: ActorRef[StatusReply[EventResponse]])               extends EventCommand
  }

  sealed trait EventResponse extends CborSerializable
  object EventResponse {
    case class CreateEventRsp(event: Event)                          extends EventResponse
    case class GetEventRsp(maybeEvent: Option[Event])                extends EventResponse
    case class GetAllEvents(events: List[Event])                     extends EventResponse
    case class UpdateEventRsp(id: Long, updatedEvent: Option[Event]) extends EventResponse
    case class DeleteEventRsp(id: Long)                              extends EventResponse
  }
}
