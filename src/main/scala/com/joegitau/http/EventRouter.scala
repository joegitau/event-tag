package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.joegitau.protocol.EventProtocol.EventCommand

import scala.concurrent.duration.DurationInt

class EventRouter(eventActor: ActorRef[EventCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler = system.scheduler
  implicit val timeout: Timeout     = 3.seconds

  def createEvent: Route = ???
  def getEventById(id: Long): Route = ???
  def getAllEvents: Route = ???
  def updateEvent(id: Long): Route = ???
  def deleteEvent(id: Long): Route = ???

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
