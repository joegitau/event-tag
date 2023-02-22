package com.joegitau.slick.dao.attendee

import com.joegitau.model.{Attendee, PatchAttendee}

import scala.concurrent.Future

trait AttendeeDao {
  def createAttendee(attendee: Attendee): Future[Attendee]
  def getAttendeeById(id: Long): Future[Option[Attendee]]
  def getAttendeeByLastName(lastName: String): Future[Option[Attendee]]
  def getAllAttendees: Future[Seq[Attendee]]
  def updateAttendee(id: Long, attendee: PatchAttendee): Future[Option[Attendee]]
  def deleteAttendee(id: Long): Future[String]
}
