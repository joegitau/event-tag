package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.joegitau.protocol.AttendanceProtocol.AttendanceCommand
import com.joegitau.protocol.AttendeeProtocol.AttendeeCommand
import com.joegitau.protocol.EventProtocol.EventCommand

class CoreRouter(eventActor: ActorRef[EventCommand],
                 attendeeActor: ActorRef[AttendeeCommand],
                 attendanceActor: ActorRef[AttendanceCommand]
                )(implicit system: ActorSystem[_]) {
  // join all the routes
  val coreRoutes: Route = pathPrefix("api") {
    concat(
      new EventRouter(eventActor).routes,
      new AttendeeRouter(attendeeActor).routes,
      new AttendanceRouter(attendanceActor).routes
    )
  }
}
