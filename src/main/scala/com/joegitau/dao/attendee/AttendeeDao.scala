package com.joegitau.dao.attendee

import com.joegitau.model.Attendee

import scala.concurrent.Future

trait AttendeeDao {
  def createAttendee(attendee: Attendee): Future[Attendee]
  def getAttendeeById(id: Long): Future[Option[Attendee]]
  def getAttendeeByLastName(lastName: String): Future[Option[Attendee]]
  def getAllAttendees: Future[Seq[Attendee]]
  def updateAttendee(id: Long, attendee: Attendee): Future[Option[Attendee]]
  def deleteAttendee(id: Long): Future[String]
}
