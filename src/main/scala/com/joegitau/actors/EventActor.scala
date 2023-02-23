package com.joegitau.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.pattern.StatusReply
import akka.pattern.StatusReply.ErrorMessage
import com.joegitau.protocol.EventProtocol.EventCommand
import com.joegitau.protocol.EventProtocol.EventResponse._
import com.joegitau.services.EventService

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object EventActor {
  def apply(eventService: EventService): Behavior[EventCommand] = Behaviors.receive { (ctx, msg) =>
    implicit val ec: ExecutionContextExecutor = ctx.executionContext

    ctx.log.info("::: Event actor started. :::")

    msg match {
      case EventCommand.CreateEvent(event, replyTo)     =>
        eventService.createEvent(event).onComplete {
          case Success(event) => replyTo ! StatusReply.success(CreateEventRsp(event))
          case Failure(ex)    => replyTo ! StatusReply.error(ErrorMessage(s"Failed to create event! ${ex.getMessage}"))
        }
        Behaviors.same

      case EventCommand.GetEvent(id, replyTo)           =>
        eventService.getEventById(id).onComplete {
          case Success(event) => replyTo ! StatusReply.success(GetEventRsp(event))
          case Failure(ex)    => replyTo ! StatusReply.error(ErrorMessage(s"Failed to fetch event with id: $id, ${ex.getMessage}"))
        }
        Behaviors.same

      case EventCommand.GetAllEvents(replyTo)           =>
        eventService.getAllEvents.onComplete {
          case Success(events) => replyTo ! StatusReply.success(GetAllEvents(events.toList))
          case Failure(ex)     => replyTo ! StatusReply.error(ErrorMessage(s"Failed to fetch events. ${ex.getMessage}"))
        }
        Behaviors.same

      case EventCommand.UpdateEvent(id, event, replyTo) =>
        eventService.updateEvent(id, event).onComplete {
          case Success(updatedEvent) => replyTo ! StatusReply.success(UpdateEventRsp(id, updatedEvent))
          case Failure(ex)           => replyTo ! StatusReply.error(ErrorMessage(s"Failed to update event with id: $id. ${ex.getMessage}"))
        }
        Behaviors.same

      case EventCommand.DeleteEvent(id, replyTo)        =>
        eventService.deleteEvent(id).onComplete {
          case Success(_)  => replyTo ! StatusReply.success(DeleteEventRsp(id))
          case Failure(ex) => replyTo ! StatusReply.error(ErrorMessage(s"Failed to delete event with id: $id. ${ex.getMessage}"))
        }
        Behaviors.same
    }
  }

}
