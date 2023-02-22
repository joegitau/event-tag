package com.joegitau.slick.dao.attendance

import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendanceTable.Attendances
import com.joegitau.slick.tables.AttendeeEventRelationTable.AttendeeEventRelations
import slick.jdbc.JdbcBackend.Database

import java.sql.Timestamp
import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class SlickAttendanceDao(db: Database)(implicit ec: ExecutionContext) extends AttendanceDao {
  val now: Instant = Instant.now()

  override def markAttendance(eventId: Long,
                              attendeeId: Long,
                              checkinTime: Option[Timestamp],
                              checkoutTime: Option[Timestamp]
                             ): Future[String] = {
    // assert existence of relation
    val relationExists = AttendeeEventRelations
      .filter(r => r.attendeeId === attendeeId && r.eventId === eventId)
      .exists
      .result

    val attendanceQuery = for {
      exists <- relationExists
      result <- if (exists) {
        // fetch the row in attendance table within which this attendance record is stored
        val query = for {
          row <- Attendances if row.eventId === eventId && row.attendeeId === attendeeId
        } yield (row.checkinTime, row.checkoutTime) // return checkin & checkout times

        query.result.headOption.flatMap {
          case Some((Some(_), None)) =>
            // checkin in
            query.update((checkinTime, None))
          case Some((None, Some(_))) =>
            // checkin out
            query.update((None, checkoutTime))
          case Some((None, None)) =>
            // neither checkin in nor checkin out
            DBIO.successful("Checkin and Checkout times not provided!")
          case None => DBIO.failed(new RuntimeException("Something unexpected that is related to the db's result!"))
        }
      } else {
        DBIO.failed(new RuntimeException("Most likely Attendee is not registered for this event!"))
      }
    } yield result

    db.run(attendanceQuery).map(_ => "Attendance successfully marked!")
  }

  /* override def markCheckin(attendeeId: Long, eventId: Long, checkinTime: Timestamp): Future[Int] = {
    val updateQuery = AttendeeEventRelations
      .filter(aer => aer.attendeeId === attendeeId && aer.eventId === eventId)
      .map(aer => (aer.checkinTime, aer.created))
      .update((checkinTime.toOpt, now.toOpt))

    db.run(updateQuery)
  }

  override def markCheckout(attendeeId: Long, eventId: Long, checkoutTime: Timestamp): Future[Int] = {
    val updateQuery = AttendeeEventRelations
      .filter(aer => aer.attendeeId === attendeeId && aer.eventId === eventId)
      .map(aer => (aer.checkoutTime, aer.modified))
      .update((checkoutTime.toOpt, now.toOpt))

    db.run(updateQuery)
  } */
}
