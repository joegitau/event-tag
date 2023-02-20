package com.joegitau.slick.dao.event

import com.joegitau.model.{Attendee, AttendeeEventRelation, Event, PatchEvent}

import scala.concurrent.Future

trait EventDao {
  def createEvent(event: Event): Future[Event]

  def getEventById(eventId: Long): Future[Option[Event]]

  def getAllEvents: Future[Seq[Event]]

  def updateEvent(id: Long, event: PatchEvent): Future[Option[Event]]

  def deleteEvent(id: Long): Future[String]

  def addAttendeeToEvent(eventId: Long, attendeeId: Long): Future[Int]

  def removeAttendeeFromEvent(eventId: Long, attendeeId: Long): Future[Int]

  def markAttendance(attendeeEventRelation: AttendeeEventRelation): Future[Option[String]]

  def getAttendees(eventId: Long): Future[Seq[Attendee]]
}
