package com.joegitau.slick.dao.event

import com.joegitau.model.{Event, PatchEvent}
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.EventTable.Events
import com.joegitau.utils.Helpers.OptionFns
// import slick.jdbc.JdbcBackend.Database

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class SlickEventDao(db: Database)(implicit ec: ExecutionContext) extends EventDao {
  private def queryById(id: Long) = Compiled(Events.filter(_.id === id))

  override def createEvent(event: Event): Future[Event] = {
    val insertEvent = (
        Events returning Events.map(_.id) into ((event, projectedId) => event.copy(id = projectedId))
      ) += event

    db.run(insertEvent)
  }

  override def getEventById(eventId: Long): Future[Option[Event]] =
    db.run(queryById(eventId).result.headOption)

  override def getAllEvents: Future[Seq[Event]] =
    db.run(Events.result)

  override def updateEvent(id: Long, event: PatchEvent): Future[Option[Event]] = {
    val query = queryById(id)

     val updateAction = query.result.headOption.flatMap {
      case Some(existingEvent) =>
        val updatedEvent = existingEvent.copy(
          title       = event.title.getOrElse(existingEvent.title),
          description = event.description.getOrElse(existingEvent.description),
          location    = event.location.getOrElse(existingEvent.location),
          startDate   = event.startDate.getOrElse(existingEvent.startDate),
          endDate     = event.endDate.getOrElse(existingEvent.endDate),
          organizer   = event.organizer.getOrElse(existingEvent.organizer),
          created     = existingEvent.created, // shouldn't change
          modified    = Instant.now().toOpt
        )

        query.update(updatedEvent) >> query.result.headOption
      case None                => DBIO.successful(None)
    }

    db.run(updateAction.transactionally)
  }

  override def deleteEvent(id: Long): Future[String] =
    db.run(queryById(id).delete).map(_ => s"Successfully deleted Event with id: $id")

  /* override def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    val existingRecord = attendeeEventDao.getAttendeeEventRelationByAttendeeAndEvent(attendeeId, eventId)
    existingRecord.flatMap {
      case Some(_) =>
        Future.successful(0) // record already exists
      case None    =>
        val currentTime = new Timestamp(System.currentTimeMillis())

        val attendeeEventRel = AttendeeEventRelation(
          id           = None,
          eventId      = eventId,
          attendeeId   = attendeeId,
          checkinTime  = currentTime.toOpt,
          checkoutTime = None,
          created      = Instant.now().toOpt,
          modified     = None
        )

        val insertQuery = AttendeeEventRelations += attendeeEventRel
        db.run(insertQuery)
    }
  } */

  /* override def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    val deleteQuery = AttendeeEventRelations
      .filter(aer => aer.eventId === eventId && aer.attendeeId === attendeeId)
      .delete

    db.run(deleteQuery)
  } */

  /* override def getAttendees(eventId: Long): Future[Seq[Attendee]] = {
    val joinQuery = AttendeeEventRelations
      .filter(_.eventId === eventId)
      .join(Attendees) on (_.attendeeId === _.id)

    val result = joinQuery
      .map { case (_, attendee) => attendee }
      .result

    db.run(result)
  } */
}

/**
 * NOTES:
 * Slick's `returning` method is used to retrieve values from the database after performing an insert, update, or delete operation.
 *   - It takes a projection of the table and returns the specified column(s) after the insert operation has been performed.
 */
