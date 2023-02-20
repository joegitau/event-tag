package com.joegitau.slick.dao.attendee

import com.joegitau.model.Attendee
import com.joegitau.slick.tables.AttendeeTable.Attendees
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}

class SlickAttendeeDao(db: Database)(implicit ec: ExecutionContext) extends AttendeeDao {
  private def queryById(id: Long) = Compiled(Attendees.filter(_.id === id))
  private def queryByName(name: String) = Compiled(Attendees.filter(_.lastName === name))

  override def createAttendee(attendee: Attendee): Future[Attendee] = {
    val insertQuery = (
        Attendees returning Attendees.map(_.id) into((attendee, projectedId) => attendee.copy(id = projectedId))
    ) += attendee

    db.run(insertQuery)
  }

  override def getAttendeeById(id: Long): Future[Option[Attendee]] = {
    val query = queryById(id).result.headOption

    db.run(query)
  }

  override def getAttendeeByLastName(lastName: String): Future[Option[Attendee]] = {
    val query = queryByName(lastName).result.headOption

    db.run(query)
  }


  override def getAllAttendees: Future[Seq[Attendee]] = {
    db.run(Attendees.result)
  }

  override def updateAttendee(attendee: Attendee): Future[Option[Attendee]] = {
    val updateAction = for {
      existingAttendee <- queryById(attendee.id.get).result.headOption
      _                <- existingAttendee
                           .map(_ => queryById(attendee.id.get).update(attendee))
                           .getOrElse(DBIO.successful(0L))
    } yield existingAttendee

    db.run(updateAction)
  }

  override def deleteAttendee(id: Long): Future[String] = {
    val deleteAction = for {
      existingAttendee <- queryById(id).result.headOption
      _                <- existingAttendee
                           .map(_ => queryById(id).delete)
                           .getOrElse(DBIO.successful(0L))
    } yield existingAttendee

    db.run(deleteAction).map {
      case Some(_) => s"Successfully deleted attendee with id: $id"
      case None    => s"Attendee with id: $id not found!"
    }
  }
}
