package com.joegitau.slick.tables

import com.joegitau.model.AttendeeEventInfo
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import java.sql.Timestamp
import java.time.Instant

class AttendeeEventInfoTable(tag: Tag) extends Table[AttendeeEventInfo](tag, "attendeeEventInfos") {
  def id = column[Option[Long]]("id")
  def eventId      = column[Int]("event_id")
  def attendeeId   = column[Int]("attendee_id")
  def checkinTime  = column[Option[Timestamp]]("checkin_time")
  def checkoutTime = column[Option[Timestamp]]("checkout_time")
  def created      = column[Option[Instant]]("created")
  def modified     = column[Option[Instant]]("modified")


  def * = (id, eventId, attendeeId, checkinTime, checkoutTime, created, modified) <> (AttendeeEventInfo.tupled, AttendeeEventInfo.unapply)
}

object AttendeeEventInfoTable {
  val AttendeeEventInfos: TableQuery[AttendeeEventInfoTable] = TableQuery[AttendeeEventInfoTable]
}
