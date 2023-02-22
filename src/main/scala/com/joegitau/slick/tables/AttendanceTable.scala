package com.joegitau.slick.tables

import com.joegitau.model.Attendance
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import java.sql.Timestamp

class AttendanceTable(tag: Tag) extends Table[Attendance](tag, "attendances") {
  def eventId      = column[Long]("event_id")
  def attendeeId   = column[Long]("attendee_id")
  def checkinTime  = column[Option[Timestamp]]("checkin_time")
  def checkoutTime = column[Option[Timestamp]]("checkout_time")

  def * = (eventId, attendeeId, checkinTime, checkoutTime) <> (Attendance.tupled, Attendance.unapply)

  def pk = primaryKey("pk_attendances", (eventId, attendeeId))
}

object AttendanceTable {
  lazy val Attendances: TableQuery[AttendanceTable] = TableQuery[AttendanceTable]
}
