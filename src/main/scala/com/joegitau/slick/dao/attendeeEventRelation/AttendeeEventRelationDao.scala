package com.joegitau.slick.dao.attendeeEventRelation

import com.joegitau.model.AttendeeEventRelation

import scala.concurrent.Future

trait AttendeeEventRelationDao {
  def createAttendeeEventRelation(attendeeEventInfo: AttendeeEventRelation): Future[AttendeeEventRelation]

  def getAttendeeEventRelationById(id: Long): Future[Option[AttendeeEventRelation]]

  def getAttendeeEventRelationsByEventId(eventId: Long): Future[Seq[AttendeeEventRelation]]

  def getAllAttendeeEventRelations: Future[Seq[AttendeeEventRelation]]

  def getAttendeeEventRelationsByAttendeeId(attendeeId: Long): Future[Seq[AttendeeEventRelation]]

  def getAttendeeEventRelationByAttendeeAndEvent(attendeeId: Long, eventId: Long): Future[Option[AttendeeEventRelation]]

  def updateAttendeeEventRelation(attendeeEventInfo: AttendeeEventRelation): Future[Option[AttendeeEventRelation]]

  def deleteAttendeeEventRelation(id: Long): Future[String]
}
