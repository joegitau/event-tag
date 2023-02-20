package com.joegitau.slick.dao.event

import com.joegitau.model.{AttendeeEventInfo, Event}
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.EventTable.Events
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}

class SlickEventDao(db: Database)(implicit ec: ExecutionContext) extends EventDao {
  override def createEvent(event: Event): Future[Event] = {
    val insertQuery = (Events returning Events.map(_.id) into ((event, id) => event.copy(id = id))) += event

    db.run(insertQuery)
  }

  override def getEventById(eventId: Long): Future[Option[Event]] = {
    val query = Events.filter(_.id === eventId).result.headOption

    db.run(query)
  }

  override def getAllEvents: Future[Seq[Event]] = {
    db.run(Events.result)
  }

  override def updateEvent(event: Event): Future[Option[Event]] = ???

  override def deleteEvent(id: Long): Future[Option[Unit]] = ???

  override def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Option[AttendeeEventInfo]] = ???

  override def markAttendance(attendeeEventInfo: AttendeeEventInfo): Future[Option[Unit]] = ???
}

/**
 * NOTES:
 * Slick's `returning` method is used to retrieve values from the database after performing an insert, update, or delete operation.
 *   - It takes a projection of the table and returns the specified column(s) after the insert operation has been performed.
 */
