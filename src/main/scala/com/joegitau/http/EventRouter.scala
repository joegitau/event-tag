package com.joegitau.http

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.pattern.StatusReply
import akka.util.Timeout
import com.joegitau.model.Event
import com.joegitau.protocol.EventProtocol._
import com.joegitau.protocol.EventProtocol.EventCommand._
import com.joegitau.protocol.EventProtocol.EventResponse._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class EventRouter(eventActor: ActorRef[EventCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler = system.scheduler
  implicit val timeout: Timeout     = 3.seconds

  def createEvent: Route = {
    entity(as[Event]) {event: Event =>
      val eventToCreate: Future[StatusReply[EventResponse]] = eventActor.ask(CreateEvent(event, _))

      onSuccess(eventToCreate) {
        case StatusReply.Success(CreateEventRsp(event)) =>
          respondWithHeader(Location(s"/api/events/${event.id.getOrElse(0L)}")) {
            complete(StatusCodes.Created -> s"Successfully created an event with id: ${event.id}")
          }
        case StatusReply.Error(reason)                  =>
          complete(StatusCodes.InternalServerError -> reason)
      }
    }
  }

  def getEventById(id: Long): Route = {
    val probableEvent: Future[StatusReply[EventResponse]] = eventActor.ask(GetEvent(id, _))

    onSuccess(probableEvent) {
      case StatusReply.Success(GetEventRsp(eventOpt)) => complete(StatusCodes.OK -> eventOpt)
      case StatusReply.Error(reason)                  => complete(StatusCodes.NotFound -> reason)
    }
  }

  def getAllEvents: Route = {
    val probableEvents = eventActor.ask(GetAllEvents)

    onSuccess(probableEvents) {
      case StatusReply.Success(GetAllEventsRsp(events)) =>
        if (events.nonEmpty) {
          complete(StatusCodes.OK -> events)
        } else {
          complete(StatusCodes.OK -> "No events found at the moment!")
        }
      case StatusReply.Error(reason)                    => complete(StatusCodes.NotFound -> reason)
    }
  }

  def updateEvent(id: Long): Route = {
    entity(as[Event]) { event =>
      val toUpdate: Future[StatusReply[EventResponse]] = eventActor.ask(UpdateEvent(id, event, _))

      onSuccess(toUpdate) {
        case StatusReply.Success(UpdateEventRsp(_, updatedEvent)) => complete(StatusCodes.OK -> updatedEvent)
        case StatusReply.Error(reason)                            => complete(StatusCodes.InternalServerError -> reason)
      }
    }
  }

  def deleteEvent(id: Long): Route = {
    val eventToDelete = eventActor.ask(DeleteEvent(id, _))

    onSuccess(eventToDelete) {
      case StatusReply.Success(DeleteEventRsp(id)) => complete(StatusCodes.OK -> s"Event with id: $id, successfully deleted!")
      case StatusReply.Error(reason)               => complete(s"Event with id: $id not deleted: $reason")
    }
  }

  val routes: Route = pathPrefix("events") {
    concat(
      pathEnd {
        get { getAllEvents } ~
        post { createEvent }
      },
      path(LongNumber) { id =>
        get { getEventById(id) } ~
        patch { updateEvent(id) } ~
        delete { deleteEvent(id) }
      }
    )
  }
}
