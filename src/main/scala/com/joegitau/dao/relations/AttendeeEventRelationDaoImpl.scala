package com.joegitau.dao.relations

import com.joegitau.model.{Attendee, AttendeeEventRelation, Event}
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendeeEventRelationTable.AttendeeEventRelations
import com.joegitau.slick.tables.AttendeeTable.Attendees
import com.joegitau.slick.tables.EventTable.Events

import scala.concurrent.{ExecutionContext, Future}

class AttendeeEventRelationDaoImpl(db: Database)(implicit ec: ExecutionContext) extends AttendeeEventRelationDao {
  private def queryByEventId(eventId: Long)       = Compiled(AttendeeEventRelations.filter(_.eventId === eventId))
  private def queryByAttendeeId(attendeeId: Long) = Compiled(AttendeeEventRelations.filter(_.attendeeId === attendeeId))

  override def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    val action = AttendeeEventRelations += AttendeeEventRelation(eventId, attendeeId)

    db.run(action)
  }

  override def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    val removeQuery = AttendeeEventRelations
      .filter(aer => aer.eventId === eventId && aer.attendeeId === attendeeId)
      .delete

    db.run(removeQuery)
  }

  override def getEventsForAttendee(attendeeId: Long): Future[Seq[Event]] = {
    val query = for {
      relation <- AttendeeEventRelations if relation.attendeeId === attendeeId
      eventId  = relation.eventId
      event    <- Events.filter(_.id == eventId)
    } yield event

    db.run(query.result)
  }

  override def getAttendeeByEventId(eventId: Long, attendeeId: Long): Future[Option[Attendee]] = {
    val query = AttendeeEventRelations
      .filter(r => r.attendeeId === attendeeId && r.eventId === eventId)
      .join(Attendees) on(_.attendeeId === _.id)

    val result = query
      .map { case (_, attendeeTbl) => attendeeTbl }
      .result
      .headOption

    db.run(result)
  }

  override def getAllAttendeesByEventId(eventId: Long): Future[Seq[Attendee]] = {
    val joinQuery = AttendeeEventRelations
      .filter(_.eventId === eventId)
      .join(Attendees) on(_.attendeeId === _.id) // on((aer, att) => aer.attendeeId === att.id)

    val result = joinQuery
      .map { case (_, attendee) => attendee }
      .result

    db.run(result)
  }

  override def attendeeEventRelationExists(attendeeId: Long, eventId: Long): Future[Boolean] = {
    val query = AttendeeEventRelations
      .filter(r => r.attendeeId === attendeeId && r.eventId === eventId)
      .exists
      .result

    db.run(query)
  }

  // FIXME: should query by both eventId and attendeeId
  override def deleteAttendeeEventRelation(eventId: Long, attendeeId: Long): Future[Int] =
    db.run(queryByAttendeeId(attendeeId).delete)


  /////////////////////////////////////////////////////////////////////////////////////////////////
  // these guys are probably not useful???
  override def getAttendeeEventRelationsByEventId(eventId: Long): Future[Seq[AttendeeEventRelation]] =
    db.run(queryByEventId(eventId).result)

  override def getAttendeeEventRelationsByAttendeeId(attendeeId: Long): Future[Seq[AttendeeEventRelation]] =
    db.run(queryByAttendeeId(attendeeId).result)

  override def getAllAttendeeEventRelations: Future[Seq[AttendeeEventRelation]] =
    db.run(AttendeeEventRelations.result)

  override def getAttendeeEventRelationByAttendeeAndEvent(attendeeId: Long, eventId: Long): Future[Option[AttendeeEventRelation]] = {
    val query = AttendeeEventRelations
      .filter(aer => aer.attendeeId === attendeeId && aer.eventId === eventId)
      .result.headOption

    db.run(query)
  }

}
