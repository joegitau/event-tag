package com.joegitau.slick.tables

import com.joegitau.model.Attendance
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import java.time.Instant

class AttendanceTable(tag: Tag) extends Table[Attendance](tag, "attendances") {
  def eventId      = column[Long]("event_id")
  def attendeeId   = column[Long]("attendee_id")
  def checkinTime  = column[Option[Instant]]("checkin_time")
  def checkoutTime = column[Option[Instant]]("checkout_time")

  def * = (eventId, attendeeId, checkinTime, checkoutTime) <> (Attendance.tupled, Attendance.unapply)

  def pk = primaryKey("pk_attendances", (eventId, attendeeId))
}

object AttendanceTable {
  lazy val Attendances: TableQuery[AttendanceTable] = TableQuery[AttendanceTable]
}
