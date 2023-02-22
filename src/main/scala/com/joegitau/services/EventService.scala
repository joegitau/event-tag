package com.joegitau.services

import com.joegitau.model.{Attendee, Event, PatchEvent}
import com.joegitau.slick.dao.attendance.AttendanceDao
import com.joegitau.slick.dao.attendee.AttendeeDao
import com.joegitau.slick.dao.attendeeEventRelation.AttendeeEventRelationDao
import com.joegitau.slick.dao.event.EventDao

import scala.concurrent.{ExecutionContext, Future}

trait EventService {
  def createEvent(event: Event): Future[Event]
  def getEventById(eventId: Long): Future[Option[Event]]
  def getAllEvents: Future[Seq[Event]]
  def updateEvent(id: Long, event: PatchEvent): Future[Option[Event]]
  def deleteEvent(id: Long): Future[String]
  def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int]
  def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int]
  def getAttendees(eventId: Long): Future[Seq[Attendee]]
}

class EventServiceImpl(eventDao: EventDao,
                       attendeeDao: AttendeeDao,
                       attendeeEventRelationDao: AttendeeEventRelationDao,
                       attendanceDao: AttendanceDao
                      )(implicit ec: ExecutionContext) extends EventService {
  override def createEvent(event: Event): Future[Event] =
    eventDao.createEvent(event)

  override def getEventById(eventId: Long): Future[Option[Event]] =
    eventDao.getEventById(eventId)

  override def getAllEvents: Future[Seq[Event]] =
    eventDao.getAllEvents

  override def updateEvent(id: Long, event: PatchEvent): Future[Option[Event]] =
    eventDao.updateEvent(id, event)

  override def deleteEvent(id: Long): Future[String] =
    eventDao.deleteEvent(id)

  override def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    for {
      eventOpt       <- eventDao.getEventById(eventId)
      attendeeOpt    <- attendeeDao.getAttendeeById(attendeeId)
      relationExists <- attendeeEventRelationDao.attendeeEventRelationExists(attendeeId, eventId)
      result         <- if (eventOpt.isDefined && attendeeOpt.isDefined && !relationExists) {
        attendeeEventRelationDao.addAttendeeToEvent(eventId, attendeeId).map(_ => 1) // we don't need to return the relation
      } else Future.successful(0)
    } yield result
  }

  override def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int] = {
    // 1. get the attendee
    // 2. get the event
    // 3. validate relation
    // remove the attendee
    for {
      eventOpt       <- eventDao.getEventById(eventId)
      attendeeOpt    <- attendeeDao.getAttendeeById(attendeeId)
      relationExists <- attendeeEventRelationDao.attendeeEventRelationExists(attendeeId, eventId)
      result         <- if (attendeeOpt.isDefined && eventOpt.isDefined && relationExists) {
        attendeeEventRelationDao.removeAttendeeFromEvent(eventId, attendeeId)
      } else Future.successful(0)
    } yield result
  }

  override def getAttendees(eventId: Long): Future[Seq[Attendee]] =
    attendeeEventRelationDao.getAllAttendeesInEvent(eventId)
}
