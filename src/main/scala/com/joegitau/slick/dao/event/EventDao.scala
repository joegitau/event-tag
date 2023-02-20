package com.joegitau.slick.dao.event

import com.joegitau.model.{AttendeeEventInfo, Event}

import scala.concurrent.Future

trait EventDao {
  def createEvent(event: Event): Future[Event]

  def getEventById(eventId: Long): Future[Option[Event]]

  def getAllEvents: Future[Seq[Event]]

  def updateEvent(event: Event): Future[Option[Event]]

  def deleteEvent(id: Long): Future[Option[Unit]]

  def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Option[AttendeeEventInfo]]

  def markAttendance(attendeeEventInfo: AttendeeEventInfo): Future[Option[Unit]]
}
