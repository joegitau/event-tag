package com.joegitau

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, BackoffSupervisorStrategy, Behavior, SupervisorStrategy}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.joegitau.actors.{AttendanceActor, AttendeeActor, AttendeeEventRelationActor, EventActor}
import com.joegitau.dao.attendance.AttendanceDaoImpl
import com.joegitau.dao.attendee.AttendeeDaoImpl
import com.joegitau.dao.event.EventDaoImpl
import com.joegitau.dao.relations.AttendeeEventRelationDaoImpl
import com.joegitau.http.CoreRouter
import com.joegitau.services._
import com.joegitau.slick.Connection.Db

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object server {
  lazy val interface = "localhost"
  lazy val port      = 8080

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    implicit val ec: ExecutionContextExecutor = system.executionContext

    val serverBinding = Http().newServerAt(interface, port).bind(routes)
    serverBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(s"Server running at http://${address.getHostString}:${address.getPort}/")
      case Failure(ex)      =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  val userGuardian: Behavior[NotUsed] = Behaviors.setup { context =>
    implicit val system: ActorSystem[Nothing] = context.system
    implicit val ec: ExecutionContextExecutor = context.system.executionContext

    // Define the supervisor strategy for the child actors
    val supervisorStrategy: BackoffSupervisorStrategy = SupervisorStrategy.restartWithBackoff(
      minBackoff   = 1.second,
      maxBackoff   = 30.seconds,
      randomFactor = 0.2
    )

    // DAOs
    val eventDao            = new EventDaoImpl(Db)
    val attendeeDao         = new AttendeeDaoImpl(Db)
    val attendanceDao       = new AttendanceDaoImpl(Db)
    val attendeeEventRelDao = new AttendeeEventRelationDaoImpl(Db)

    // services
    val eventService            = new EventServiceImpl(eventDao, attendeeDao, attendeeEventRelDao)
    val attendeeService         = new AttendeeServiceImpl(attendeeDao)
    val attendanceService       = new AttendanceServiceImpl(attendanceDao, eventDao, attendeeEventRelDao)
    val attendeeEventRelService = new AttendeeEventRelationServiceImpl(attendeeEventRelDao)

    // child actors
    val eventActor            = context.spawn(EventActor(eventService), "event-actor")
    val attendeeActor         = context.spawn(AttendeeActor(attendeeService), "attendee-actor")
    val attendeeEventRelActor = context.spawn(AttendeeEventRelationActor(attendeeEventRelService), "attendee-event-rel-actor")
    val attendanceActor       = context.spawn(AttendanceActor(attendanceService, attendeeEventRelActor), "attendance-actor")

    // routes
    val coreRoutes = new CoreRouter(eventActor, attendeeActor, attendanceActor).coreRoutes

    // start server
    startHttpServer(coreRoutes)

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem(userGuardian, "event-tag-system")
  }
}
