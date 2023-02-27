package com.joegitau.dao.event

import com.joegitau.model.Event

import scala.concurrent.Future

trait EventDao {
  def createEvent(event: Event): Future[Event]
  def getEventById(eventId: Long): Future[Option[Event]]
  def getAllEvents: Future[Seq[Event]]
  def updateEvent(id: Long, event: Event): Future[Option[Event]]
  def deleteEvent(id: Long): Future[String]
}
