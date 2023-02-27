package com.joegitau.dao.attendee

import com.joegitau.model.Attendee
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendeeTable.Attendees
import com.joegitau.utils.Helpers.OptionFns

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class AttendeeDaoImpl(db: Database)(implicit ec: ExecutionContext) extends AttendeeDao {
  private def queryById(id: Long) = Compiled(Attendees.filter(_.id === id))
  private def queryByName(name: String) = Compiled(Attendees.filter(_.lastName === name))

  override def createAttendee(attendee: Attendee): Future[Attendee] = {
    val insertQuery = (
        Attendees returning Attendees.map(_.id) into((attendee, projectedId) => attendee.copy(id = projectedId))
    ) += attendee

    db.run(insertQuery)
  }

  override def getAttendeeById(id: Long): Future[Option[Attendee]] =
    db.run(queryById(id).result.headOption)

  override def getAttendeeByLastName(lastName: String): Future[Option[Attendee]] =
    db.run(queryByName(lastName).result.headOption)

  override def getAllAttendees: Future[Seq[Attendee]] =
    db.run(Attendees.result)

  override def updateAttendee(id: Long, attendee: Attendee): Future[Option[Attendee]] = {
    val query = queryById(id)

    val updateAction = query.result.headOption.flatMap {
      case Some(existingAttendee) =>
        val updatedAttendee = existingAttendee.copy(
          firstName = attendee.firstName,
          lastName  = attendee.lastName,
          company   = attendee.company,
          email     = attendee.email,
          created   = existingAttendee.created,
          modified  = Instant.now().toOpt
        )

        query.update(updatedAttendee) >> query.result.headOption

      case None                   => DBIO.successful(None)
    }

    db.run(updateAction) // .flatMap(_ => getAttendeeById(id))
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
