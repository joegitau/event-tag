package com.joegitau

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, BackoffSupervisorStrategy, Behavior, SupervisorStrategy}
import com.joegitau.actors.{AttendanceActor, AttendeeActor, AttendeeEventRelationActor, EventActor}
import com.joegitau.services._
import com.joegitau.slick.dao.attendance.SlickAttendanceDao
import com.joegitau.slick.dao.attendee.SlickAttendeeDao
import com.joegitau.slick.dao.attendeeEventRelation.SlickAttendeeEventRelationDao
import com.joegitau.slick.dao.event.SlickEventDao
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import scala.concurrent.duration.DurationInt

object server {
  val userGuardian: Behavior[NotUsed] = Behaviors.setup { context =>
    implicit val ec = context.system.executionContext

    // Define the supervisor strategy for the child actors
    val supervisorStrategy: BackoffSupervisorStrategy = SupervisorStrategy.restartWithBackoff(
      minBackoff   = 1.second,
      maxBackoff   = 30.seconds,
      randomFactor = 0.2
    )

    val db = Database.forConfig("postgress")

    // DAOs
    val eventDao            = new SlickEventDao(db)
    val attendeeDao         = new SlickAttendeeDao(db)
    val attendanceDao       = new SlickAttendanceDao(db)
    val attendeeEventRelDao = new SlickAttendeeEventRelationDao(db)

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

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem(userGuardian, "event-tag-system")
  }
}
