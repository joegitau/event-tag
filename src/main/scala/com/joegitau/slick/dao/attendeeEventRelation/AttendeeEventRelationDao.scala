package com.joegitau.slick.dao.attendeeEventRelation

import com.joegitau.model.{Attendee, AttendeeEventRelation}

import scala.concurrent.Future

trait AttendeeEventRelationDao {
  def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[AttendeeEventRelation]
  def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int]
  def getAttendeeInEvent(eventId: Long, attendeeId: Long): Future[Option[Attendee]]
  def getAllAttendeesInEvent(eventId: Long): Future[Seq[Attendee]]
  def attendeeEventRelationExists(attendeeId: Long, eventId: Long): Future[Boolean]

  // probably not needed???
  def getAttendeeEventRelationById(id: Long): Future[Option[AttendeeEventRelation]]
  def getAttendeeEventRelationsByEventId(eventId: Long): Future[Seq[AttendeeEventRelation]]
  def getAllAttendeeEventRelations: Future[Seq[AttendeeEventRelation]]
  def getAttendeeEventRelationsByAttendeeId(attendeeId: Long): Future[Seq[AttendeeEventRelation]]
  def getAttendeeEventRelationByAttendeeAndEvent(attendeeId: Long, eventId: Long): Future[Option[AttendeeEventRelation]]
  def updateAttendeeEventRelation(attendeeEventInfo: AttendeeEventRelation): Future[Option[AttendeeEventRelation]]
  def deleteAttendeeEventRelation(id: Long): Future[String]
}
