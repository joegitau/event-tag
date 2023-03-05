package com.joegitau.http

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.pattern.StatusReply
import akka.util.Timeout
import com.joegitau.model.Attendee
import com.joegitau.protocol.AttendeeProtocol.AttendeeCommand
import com.joegitau.protocol.AttendeeProtocol.AttendeeCommand._
import com.joegitau.protocol.AttendeeProtocol.AttendeeResponse._

import scala.concurrent.duration.DurationInt

class AttendeeRouter(attendeeActor: ActorRef[AttendeeCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler = system.scheduler
  implicit val timeout: Timeout     = 3.seconds

  def createAttendee: Route = {
    entity(as[Attendee]) { attendee: Attendee =>
      val attendeeToCreate = attendeeActor.ask(CreateAttendee(attendee, _))

      onSuccess(attendeeToCreate) {
        case StatusReply.Success(CreateAttendeeRsp(attendee)) =>
          respondWithHeader(Location(s"/api/attendees/${attendee.id.getOrElse(0L)}")) {
            complete(StatusCodes.Created, s"Successfully created attendee with id: ${attendee.id}")
          }
        case StatusReply.Error(reason)                        =>
          complete(StatusCodes.InternalServerError -> reason)
      }
    }
  }

  def getAttendeeById(id: Long): Route = {
    val probableAttendee = attendeeActor.ask(GetAttendee(id, _))

    onSuccess(probableAttendee) {
      case StatusReply.Success(GetAttendeeRsp(attendeeOpt)) =>
        attendeeOpt match {
          case Some(attendee) => complete(StatusCodes.OK -> attendee)
          case None           => complete(StatusCodes.NotFound -> s"Attendee with id: $id not found.")
        }
      case StatusReply.Error(reason)                        =>
        complete(StatusCodes.InternalServerError -> reason)
    }
  }

  def getAllAttendees: Route = {
    val probableAttendees = attendeeActor.ask(GetAttendees)

    onSuccess(probableAttendees) {
      case StatusReply.Success(GetAttendeesRsp(attendees)) =>
        if (attendees.nonEmpty) {
          complete(StatusCodes.OK -> attendees)
        } else {
          complete(StatusCodes.OK -> "No attendees found!")
        }
      case StatusReply.Error(reason)                       =>
        complete(StatusCodes.NotFound -> reason)
    }
  }

  def updateAttendee(id: Long): Route = {
    entity(as[Attendee]) { attendee: Attendee =>
      val toUpdate = attendeeActor.ask(UpdateAttendee(id, attendee, _))

      onSuccess(toUpdate) {
        case StatusReply.Success(UpdateAttendeeRsp(attendeeOpt)) =>
          attendeeOpt match {
            case Some(attendee) => complete(StatusCodes.OK -> attendee)
            case None           => complete(StatusCodes.NotFound -> s"Could not update as attendee with id: $id not found.")
          }
        case StatusReply.Error(reason)                           =>
          complete(StatusCodes.InternalServerError -> reason)
      }
    }
  }

  def deleteAttendee(id: Long): Route = {
    val attendeeToDelete = attendeeActor.ask(DeleteAttendee(id, _))

    onSuccess(attendeeToDelete) {
      case StatusReply.Success(DeleteAttendeeRsp(id)) => complete(StatusCodes.OK, s"Deleted attendee with id: $id")
      case StatusReply.Error(reason)                  => complete(StatusCodes.InternalServerError -> reason)
    }
  }

  val routes: Route = pathPrefix("attendees") {
    concat(
      pathEnd {
        get { getAllAttendees } ~
          post { createAttendee }
      },
      path(LongNumber) { attendeeId =>
        get { getAttendeeById(attendeeId) } ~
        patch { updateAttendee(attendeeId) } ~
        delete {deleteAttendee(attendeeId) }
      }
    )
  }
}
