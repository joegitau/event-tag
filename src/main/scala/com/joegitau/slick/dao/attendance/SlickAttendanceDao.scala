package com.joegitau.slick.dao.attendance

import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendeeEventRelationTable.AttendeeEventRelations
import com.joegitau.utils.Helpers.OptionFns
import slick.jdbc.JdbcBackend.Database

import java.sql.Timestamp
import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class SlickAttendanceDao(db: Database)(implicit ec: ExecutionContext) extends AttendanceDao {
  val now: Instant = Instant.now()

  override def markCheckin(attendeeId: Long, eventId: Long, checkinTime: Timestamp): Future[Int] = {
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
  }
}
