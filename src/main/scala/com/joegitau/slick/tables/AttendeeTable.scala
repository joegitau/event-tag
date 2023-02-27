package com.joegitau.slick.tables

import com.joegitau.model.Attendee
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import java.time.Instant

class AttendeeTable(tag: Tag) extends Table[Attendee](tag, "attendees") {
  def id        = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName  = column[String]("last_name")
  def company   = column[Option[String]]("company")
  def email     = column[String]("email", O.Unique)
  def created   = column[Option[Instant]]("created")
  def modified  = column[Option[Instant]]("modified")

  def * = (id, firstName, lastName, company, email, created, modified) <> (Attendee.tupled, Attendee.unapply)
}

object AttendeeTable {
  lazy val Attendees: TableQuery[AttendeeTable] = TableQuery[AttendeeTable]
}
