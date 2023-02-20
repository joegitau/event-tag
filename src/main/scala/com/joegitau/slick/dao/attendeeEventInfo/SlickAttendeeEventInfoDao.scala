package com.joegitau.slick.dao.attendeeEventInfo

import com.joegitau.model.AttendeeEventInfo
import com.joegitau.slick.tables.AttendeeEventInfoTable.AttendeeEventInfos
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.utils.Helpers.OptionFns
import slick.jdbc.JdbcBackend.Database

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class SlickAttendeeEventInfoDao(db: Database)(implicit ec: ExecutionContext) extends AttendeeEventInfoDao {
  private def queryById(id: Long)                 = Compiled(AttendeeEventInfos.filter(_.id === id))
  private def queryByEventId(eventId: Long)       = Compiled(AttendeeEventInfos.filter(_.eventId === eventId))
  private def queryByAttendeeId(attendeeId: Long) = Compiled(AttendeeEventInfos.filter(_.attendeeId === attendeeId))

  override def createAttendeeEventInfo(attendeeEventInfo: AttendeeEventInfo): Future[AttendeeEventInfo] = {
    val insertQuery = (
        AttendeeEventInfos returning AttendeeEventInfos.map(_.id) into((aei, projectedId) => aei.copy(id = projectedId))
      ) += attendeeEventInfo

    db.run(insertQuery)
  }

  override def getAttendeeEventInfoById(id: Long): Future[Option[AttendeeEventInfo]] =
    db.run(queryById(id).result.headOption)

  override def getAttendeeEventInfosByEventId(eventId: Long): Future[Seq[AttendeeEventInfo]] =
    db.run(queryByEventId(eventId).result)

  override def getAttendeeEventInfosByAttendeeId(attendeeId: Long): Future[Seq[AttendeeEventInfo]] =
    db.run(queryByAttendeeId(attendeeId).result)

  override def updateAttendeeEventInfo(attendeeEventInfo: AttendeeEventInfo): Future[Option[AttendeeEventInfo]] = {
    val updateAction = for {
      existingAEI <- queryById(attendeeEventInfo.id.get).result.headOption
      _           <- existingAEI
                     .map(_ => queryById(attendeeEventInfo.id.get).update(attendeeEventInfo.copy(modified = Instant.now().toOpt)))
                     .getOrElse(DBIO.successful(0L))
    } yield existingAEI

    db.run(updateAction)
  }

  override def deleteAttendeeEventInfo(id: Long): Future[String] = {
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
