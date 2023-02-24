package com.joegitau.slick.tables

import com.joegitau.model.Event
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import java.time.Instant

class EventTable(tag: Tag) extends Table[Event](tag, "events") {
  def id          = column[Option[Long]]("id")
  def title       = column[String]("title")
  def description = column[String]("description")
  def location    = column[String]("location")
  def startDate   = column[Instant]("startDate")
  def endDate     = column[Instant]("endDate")
  def organizer   = column[String]("organizer")
  def created     = column[Option[Instant]]("created")
  def modified    = column[Option[Instant]]("modified")

  def * = (id, title, description, location, startDate, endDate, organizer, created, modified) <> (Event.tupled, Event.unapply)
}

object EventTable {
  lazy val Events: TableQuery[EventTable] = TableQuery[EventTable]
}
