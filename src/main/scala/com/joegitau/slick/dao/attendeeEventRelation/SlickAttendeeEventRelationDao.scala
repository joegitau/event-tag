package com.joegitau.slick.dao.attendeeEventRelation

import com.joegitau.model.AttendeeEventRelation
import com.joegitau.slick.tables.AttendeeEventRelationTable.AttendeeEventRelations
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.utils.Helpers.OptionFns
import slick.jdbc.JdbcBackend.Database

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class SlickAttendeeEventRelationDao(db: Database)(implicit ec: ExecutionContext) extends AttendeeEventRelationDao {
  private def queryById(id: Long)                 = Compiled(AttendeeEventRelations.filter(_.id === id))
  private def queryByEventId(eventId: Long)       = Compiled(AttendeeEventRelations.filter(_.eventId === eventId))
  private def queryByAttendeeId(attendeeId: Long) = Compiled(AttendeeEventRelations.filter(_.attendeeId === attendeeId))

  override def createAttendeeEventRelation(attendeeEventInfo: AttendeeEventRelation): Future[AttendeeEventRelation] = {
    val insertQuery = (
      AttendeeEventRelations returning AttendeeEventRelations.map(_.id) into((aei, projectedId) => aei.copy(id = projectedId))
      ) += attendeeEventInfo

    db.run(insertQuery)
  }

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
