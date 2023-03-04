package com.joegitau.slick.tables

import com.joegitau.model.AttendeeEventRelation
import com.joegitau.slick.profile.CustomPostgresProfile.api._
import com.joegitau.slick.tables.AttendeeTable.Attendees
import com.joegitau.slick.tables.EventTable.Events

class AttendeeEventRelationTable(tag: Tag) extends Table[AttendeeEventRelation](tag, "attendee_event_relations") {
  def eventId      = column[Long]("event_id")
  def attendeeId   = column[Long]("attendee_id")

  def * = (eventId, attendeeId) <> (AttendeeEventRelation.tupled, AttendeeEventRelation.unapply)

  // composite primary key
  def pk = primaryKey("pk_attendee_event_relation", (attendeeId, eventId))

  // validate that no two records can have the same attendee_id and event_id values
  def attendeeEventIndex = index("idx_attendee_event_relation", (attendeeId, eventId), unique = true)

  // attendee & events foreignKeys - with onDelete
  def attendeeFK = foreignKey("fk_attendee", attendeeId, Attendees)(_.id.getOrElse(0L), onDelete = ForeignKeyAction.Cascade)
  def eventFK    = foreignKey("fk_event", eventId, Events)(_.id.getOrElse(0L), onDelete = ForeignKeyAction.Cascade)
}

object AttendeeEventRelationTable {
  lazy val AttendeeEventRelations: TableQuery[AttendeeEventRelationTable] = TableQuery[AttendeeEventRelationTable]
}
