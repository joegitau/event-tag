package com.joegitau.slick.tables

import com.joegitau.model.Event
import com.joegitau.slick.profile.CustomPostgresProfile.api._

import java.time.Instant

class EventTable(tag: Tag) extends Table[Event](tag, "events") {
  def id        = column[Option[Long]]("id")
  def name      = column[String]("name")
  def address   = column[String]("address")
  def organizer = column[String]("organizer")
  def created   = column[Option[Instant]]("created")
  def modified  = column[Option[Instant]]("modified")

  def * = (id, name, address, organizer, created, modified) <> (Event.tupled, Event.unapply)
}

object EventTable {
  val Events: TableQuery[EventTable] = TableQuery[EventTable]
}
