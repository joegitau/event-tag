package com.joegitau.dao.event

import com.joegitau.model.Event
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.EventTable.Events
import com.joegitau.utils.Helpers.OptionFns

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class EventDaoImpl(db: Database)(implicit ec: ExecutionContext) extends EventDao {
  private def queryById(id: Long) = Compiled(Events.filter(_.id === id))

  override def createEvent(event: Event): Future[Event] = {
    val insertEvent = (
        Events returning Events.map(e => (e.id, e.created)) into ((event, t) => event.copy(id = t._1, created = t._2))
      ) += event

    db.run(insertEvent)
  }

  override def getEventById(eventId: Long): Future[Option[Event]] =
    db.run(queryById(eventId).result.headOption)

  override def getAllEvents: Future[Seq[Event]] =
    db.run(Events.result)

  override def updateEvent(id: Long, event: Event): Future[Option[Event]] = {
    val query = queryById(id)

     val updateAction = query.result.headOption.flatMap {
      case Some(existingEvent) =>
        val updatedEvent = existingEvent.copy(
          title       = event.title,
          description = event.description,
          location    = event.location,
          startDate   = event.startDate,
          endDate     = event.endDate,
          organizer   = event.organizer,
          created     = existingEvent.created,
          modified    = Instant.now().toOpt
        )

        query.update(updatedEvent) >> query.result.headOption
      case None                => DBIO.successful(None)
    }

    db.run(updateAction.transactionally)
  }

  override def deleteEvent(id: Long): Future[String] =
    db.run(queryById(id).delete).map(_ => s"Successfully deleted Event with id: $id")

}

/**
 * NOTES:
 * Slick's `returning` method is used to retrieve values from the database after performing an insert, update, or delete operation.
 *   - It takes a projection of the table and returns the specified column(s) after the insert operation has been performed.
 */
