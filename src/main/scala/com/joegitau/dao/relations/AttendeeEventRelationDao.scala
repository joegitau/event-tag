package com.joegitau.dao.relations

import com.joegitau.model.{Attendee, AttendeeEventRelation, Event}

import scala.concurrent.Future

trait AttendeeEventRelationDao {
  def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int]
  def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int]
  def getEventsForAttendee(attendeeId: Long): Future[Seq[Event]]
  def getAttendeeByEventId(eventId: Long, attendeeId: Long): Future[Option[Attendee]]
  def getAllAttendeesByEventId(eventId: Long): Future[Seq[Attendee]]
  def attendeeEventRelationExists(attendeeId: Long, eventId: Long): Future[Boolean]
  def deleteAttendeeEventRelation(eventId: Long, attendeeId: Long): Future[Int]

  // probably not needed???
  def getAttendeeEventRelationsByEventId(eventId: Long): Future[Seq[AttendeeEventRelation]]
  def getAllAttendeeEventRelations: Future[Seq[AttendeeEventRelation]]
  def getAttendeeEventRelationsByAttendeeId(attendeeId: Long): Future[Seq[AttendeeEventRelation]]
  def getAttendeeEventRelationByAttendeeAndEvent(attendeeId: Long, eventId: Long): Future[Option[AttendeeEventRelation]]
}
