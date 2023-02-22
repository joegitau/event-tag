package com.joegitau.slick.dao.attendeeEventRelation

import com.joegitau.model.{Attendee, AttendeeEventRelation}
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendeeEventRelationTable.AttendeeEventRelations
import com.joegitau.slick.tables.AttendeeTable.Attendees
import com.joegitau.utils.Helpers.OptionFns
import slick.jdbc.JdbcBackend.Database

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class SlickAttendeeEventRelationDao(db: Database)(implicit ec: ExecutionContext) extends AttendeeEventRelationDao {
  private def queryById(id: Long)                 = Compiled(AttendeeEventRelations.filter(_.id === id))
  private def queryByEventId(eventId: Long)       = Compiled(AttendeeEventRelations.filter(_.eventId === eventId))
  private def queryByAttendeeId(attendeeId: Long) = Compiled(AttendeeEventRelations.filter(_.attendeeId === attendeeId))

  override def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[AttendeeEventRelation] = {
    val AER = AttendeeEventRelation(
      None, eventId, attendeeId, None, None, Instant.now().toOpt, None
    )

    val action = (
      AttendeeEventRelations returning AttendeeEventRelations.map(_.id) into ((aer, id) => aer.copy(id = id))
      ) += AER

    db.run(action)
  }

  override def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    val removeQuery = AttendeeEventRelations
      .filter(aer => aer.eventId === eventId && aer.attendeeId === attendeeId)
      .delete

    db.run(removeQuery)
  }

  override def getAttendeeInEvent(eventId: Long, attendeeId: Long): Future[Option[Attendee]] = {
    val query = AttendeeEventRelations
      .filter(r => r.attendeeId === attendeeId && r.eventId === eventId)
      .join(Attendees) on(_.attendeeId === _.id)

    val result = query
      .map { case (_, attendeeTbl) => attendeeTbl }
      .result
      .headOption

    db.run(result)
  }

  override def getAllAttendeesInEvent(eventId: Long): Future[Seq[Attendee]] = {
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

  /////////////////////////////////////////////////////////////////////////////////////////////////
  // these guys are probably not useful???
  override def getAttendeeEventRelationById(id: Long): Future[Option[AttendeeEventRelation]] =
    db.run(queryById(id).result.headOption)

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

  override def updateAttendeeEventRelation(attendeeEventInfo: AttendeeEventRelation): Future[Option[AttendeeEventRelation]] = {
    val updateAction = for {
      existingAEI <- queryById(attendeeEventInfo.id.get).result.headOption
      _           <- existingAEI
                     .map(_ => queryById(attendeeEventInfo.id.get).update(attendeeEventInfo.copy(modified = Instant.now().toOpt)))
                     .getOrElse(DBIO.successful(0L))
    } yield existingAEI

    db.run(updateAction)
  }

  override def deleteAttendeeEventRelation(id: Long): Future[String] = {
    val deleteAction = for {
      existingAEI <- queryById(id).result.headOption
      _           <- existingAEI
                      .map(_ => queryById(id).delete)
                      .getOrElse(DBIO.successful(0L))
    } yield existingAEI

    db.run(deleteAction).map {
      case Some(_) => s"Successfully deleted AttendeeEventInfo with id: $id"
      case None    => s"AttendeeEventInfo with id: $id not found!"
    }
  }
}
