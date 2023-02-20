package com.joegitau.slick.dao.attendeeEventInfo

import com.joegitau.model.AttendeeEventInfo

import scala.concurrent.Future

trait AttendeeEventInfoDao {
  def createAttendeeEventInfo(attendeeEventInfo: AttendeeEventInfo): Future[AttendeeEventInfo]

  def getAttendeeEventInfoById(id: Long): Future[Option[AttendeeEventInfo]]

  def getAttendeeEventInfosByEventId(eventId: Long): Future[Seq[AttendeeEventInfo]]

  def getAttendeeEventInfosByAttendeeId(attendeeId: Long): Future[Seq[AttendeeEventInfo]]

  def updateAttendeeEventInfo(attendeeEventInfo: AttendeeEventInfo): Future[Option[AttendeeEventInfo]]

  def deleteAttendeeEventInfo(id: Long): Future[String]
}
