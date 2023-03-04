package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand

import scala.concurrent.duration.DurationInt

class AttendeeEventRelationRouter(attendeeEventRelActor: ActorRef[AttendeeEventRelationCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler = system.scheduler
  implicit val timeout: Timeout     = 3.seconds

  def createAttendeeEventRelation: Route = ???
}
