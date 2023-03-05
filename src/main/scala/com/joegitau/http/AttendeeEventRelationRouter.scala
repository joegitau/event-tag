package com.joegitau.http

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.StatusReply
import akka.util.Timeout
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand._
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationResponse._
import com.joegitau.protocol.AttendeeEventRelationProtocol._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}

class AttendeeEventRelationRouter(attendeeEventRelActor: ActorRef[AttendeeEventRelationCommand])
                                 (implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler         = system.scheduler
  implicit val ec: ExecutionContextExecutor = system.executionContext
  implicit val timeout: Timeout             = 3.seconds

  // api/relations/attendee/${attendeeId}/event/${eventId}
  def addAttendeeToEvent(attendeeId: Long, eventId: Long): Route = {
    val relationExists: Future[Boolean] = attendeeEventRelActor.ask(CheckAttendeeEventRelation(attendeeId, eventId, _))
    val relationToCreate                = attendeeEventRelActor.ask(AddAttendeeToEvent(eventId, attendeeId, _))

    val combined: Future[(Boolean, StatusReply[AttendeeEventRelationResponse])] = relationExists.zip(relationToCreate)

    onSuccess(combined) { (exists, relation) =>
      if (!exists) {
        relation match {
          case StatusReply.Success(AddAttendeeToEventRsp(eventId, attendeeId)) =>
            complete(StatusCodes.Created -> s"Attendee with id: $attendeeId successfully added to event with id: $eventId")
          case _                                                               =>
            complete(StatusCodes.BadRequest -> "Relation can't be processed!")
        }
      } else {
        complete(StatusCodes.Conflict -> s"Attendee with id: $attendeeId already belongs to event with id: $eventId")
      }
    }
  }

  def getAttendeeWithEvents(attendeeId: Long): Route = {
    val probableAttendee = attendeeEventRelActor.ask(GetAttendeeWithEvents(attendeeId, _))

    onSuccess(probableAttendee) {
      case StatusReply.Success(GetAttendeeWithEventsRsp(attendeeWithEventsOpt)) =>
        attendeeWithEventsOpt match {
          case Some(attendeeWithEvents) => complete(StatusCodes.OK -> attendeeWithEvents)
          case None                     => complete(StatusCodes.NotFound -> s"Attendee with id: $attendeeId not found!")
        }
      case StatusReply.Error(reason)                                            =>
        complete(StatusCodes.InternalServerError -> reason)
    }
  }

  def getEventWithAttendees(eventId: Long): Route = {
    val probableEvents = attendeeEventRelActor.ask(GetEventWithAttendees(eventId, _))

    onSuccess(probableEvents) {
      case StatusReply.Success(GetEventWithAttendeesRsp(eventWithAttendeesOpt)) =>
        eventWithAttendeesOpt match {
          case Some(eventWithAttendees) => complete(StatusCodes.OK -> eventWithAttendees)
          case None                     => complete(StatusCodes.NotFound -> s"Event with id: $eventId not found!")
        }
      case StatusReply.Error(reason)                                            =>
        complete(StatusCodes.InternalServerError -> reason)
    }
  }

  val routes: Route = pathPrefix("relations") {
    concat(
      path("attendee" / LongNumber / "event" / LongNumber) { (attendeeId, eventId) =>
        post { addAttendeeToEvent(attendeeId, eventId) }
      },
      path("attendees" / LongNumber) { attendeeId => // /api/relations/attendees/1
        get { getAttendeeWithEvents(attendeeId) }
      },
      path("events" / LongNumber) { eventId =>       // /api/relations/events/1
        get { getEventWithAttendees(eventId) }
      }
    )
  }
}
