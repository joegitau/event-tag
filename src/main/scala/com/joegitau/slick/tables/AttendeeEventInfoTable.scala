package com.joegitau.slick.tables

import com.joegitau.model.AttendeeEventInfo
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendeeTable.Attendees
import com.joegitau.slick.tables.EventTable.Events

import java.sql.Timestamp
import java.time.Instant

class AttendeeEventInfoTable(tag: Tag) extends Table[AttendeeEventInfo](tag, "attendeeEventInfos") {
  def id           = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
  def eventId      = column[Long]("event_id")
  def attendeeId   = column[Long]("attendee_id")
  def checkinTime  = column[Option[Timestamp]]("checkin_time")
  def checkoutTime = column[Option[Timestamp]]("checkout_time")
  def created      = column[Option[Instant]]("created")
  def modified     = column[Option[Instant]]("modified")


  def * = (id, eventId, attendeeId, checkinTime, checkoutTime, created, modified) <> (AttendeeEventInfo.tupled, AttendeeEventInfo.unapply)

  def attendeeFK = foreignKey("fk_attendee", attendeeId, Attendees)(_.id.getOrElse(0L))
  def eventFK    = foreignKey("fk_event", eventId, Events)(_.id.getOrElse(0L))
}

object AttendeeEventInfoTable {
  val AttendeeEventInfos: TableQuery[AttendeeEventInfoTable] = TableQuery[AttendeeEventInfoTable]
}
