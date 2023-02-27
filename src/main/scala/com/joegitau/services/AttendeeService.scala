package com.joegitau.services

import com.joegitau.dao.attendee.AttendeeDao
import com.joegitau.model.Attendee

import scala.concurrent.{ExecutionContext, Future}

trait AttendeeService {
  def createAttendee(attendee: Attendee): Future[Attendee]
  def getAttendeeById(id: Long): Future[Option[Attendee]]
  def getAttendeeByLastName(lastName: String): Future[Option[Attendee]]
  def getAllAttendees: Future[Seq[Attendee]]
  def updateAttendee(id: Long, attendee: Attendee): Future[Option[Attendee]]
  def deleteAttendee(id: Long): Future[String]
}

class AttendeeServiceImpl(attendeeDao: AttendeeDao)(implicit ec: ExecutionContext) extends AttendeeService {
  override def createAttendee(attendee: Attendee): Future[Attendee] =
    attendeeDao.createAttendee(attendee)

  override def getAttendeeById(id: Long): Future[Option[Attendee]] =
    attendeeDao.getAttendeeById(id)

  override def getAttendeeByLastName(lastName: String): Future[Option[Attendee]] =
    attendeeDao.getAttendeeByLastName(lastName)

  override def getAllAttendees: Future[Seq[Attendee]] =
    attendeeDao.getAllAttendees

  override def updateAttendee(id: Long, attendee: Attendee): Future[Option[Attendee]] =
    attendeeDao.updateAttendee(id, attendee)

  override def deleteAttendee(id: Long): Future[String] =
    attendeeDao.deleteAttendee(id)
}
