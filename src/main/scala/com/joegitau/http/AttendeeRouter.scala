package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.joegitau.protocol.AttendeeProtocol.AttendeeCommand

import scala.concurrent.duration.DurationInt

class AttendeeRouter(attendeeActor: ActorRef[AttendeeCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler = system.scheduler
  implicit val timeout: Timeout     = 3.seconds

  def createAttendee: Route = ???
  def getAttendeeById(id: Long): Route = ???
  def getAllAttendees: Route = ???
  def updateAttendee(id: Long): Route = ???
  def deleteAttendee(id: Long): Route = ???

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
