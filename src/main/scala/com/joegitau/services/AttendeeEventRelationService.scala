package com.joegitau.services

import com.joegitau.dao.relations.AttendeeEventRelationDao
import com.joegitau.model.{Attendee, Event}

import scala.concurrent.{ExecutionContext, Future}

trait AttendeeEventRelationService {
  def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int]
  def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int]
  def getEventsForAttendee(attendeeId: Long): Future[Seq[Event]]
  def getAttendeeByEventId(eventId: Long, attendeeId: Long): Future[Option[Attendee]]
  def getAllAttendeesByEventId(eventId: Long): Future[Seq[Attendee]]
  def deleteAttendeeEventRelation(eventId: Long, attendeeId: Long): Future[Int]
  def attendeeEventRelationExists(attendeeId: Long, eventId: Long): Future[Boolean]
}

class AttendeeEventRelationServiceImpl(attendeeEventRelationDao: AttendeeEventRelationDao)
                                      (implicit ec: ExecutionContext) extends AttendeeEventRelationService {
  override def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int] =
    attendeeEventRelationDao.addAttendeeToEvent(eventId, attendeeId)

  override def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int] =
    attendeeEventRelationDao.removeAttendeeFromEvent(eventId, attendeeId)

  override def getEventsForAttendee(attendeeId: Long): Future[Seq[Event]] =
    attendeeEventRelationDao.getEventsForAttendee(attendeeId)

  override def getAttendeeByEventId(eventId: Long, attendeeId: Long): Future[Option[Attendee]] =
    attendeeEventRelationDao.getAttendeeByEventId(eventId, attendeeId)

  override def getAllAttendeesByEventId(eventId: Long): Future[Seq[Attendee]] =
    attendeeEventRelationDao.getAllAttendeesByEventId(eventId)

  override def attendeeEventRelationExists(attendeeId: Long, eventId: Long): Future[Boolean] =
    attendeeEventRelationExists(attendeeId, eventId)

  override def deleteAttendeeEventRelation(eventId: Long, attendeeId: Long): Future[Int] =
    attendeeEventRelationDao.deleteAttendeeEventRelation(eventId, attendeeId)
}
