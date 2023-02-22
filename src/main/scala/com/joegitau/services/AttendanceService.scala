package com.joegitau.services

import com.joegitau.slick.dao.attendance.AttendanceDao
import com.joegitau.slick.dao.attendeeEventRelation.AttendeeEventRelationDao
import com.joegitau.slick.dao.event.EventDao

import java.sql.Timestamp
import scala.concurrent.{ExecutionContext, Future}

trait AttendanceService {
  def markAttendance(eventId: Long,
                     attendeeId: Long,
                     checkinTime: Option[Timestamp],
                     checkoutTime: Option[Timestamp]
                    ): Future[String]
}

class AttendanceServiceImpl(attendanceDao: AttendanceDao,
                            eventDao: EventDao,
                            attendeeEventRelationDao: AttendeeEventRelationDao
                           )(implicit ec: ExecutionContext) extends AttendanceService {
  override def markAttendance(eventId: Long,
                              attendeeId: Long,
                              checkinTime: Option[Timestamp],
                              checkoutTime: Option[Timestamp]
                             ): Future[String] = {
    for {
      eventOpt       <- eventDao.getEventById(eventId)
      attendeeOpt    <- attendeeEventRelationDao.getAttendeeByEventId(eventId, eventId)
      relationExists <- attendeeEventRelationDao.attendeeEventRelationExists(attendeeId, eventId)
      result         <- if (eventOpt.isDefined && attendeeOpt.isDefined && relationExists) {
        attendanceDao.markAttendance(eventId, attendeeId, checkinTime, checkoutTime)
      } else Future.successful("Either event, attendee or relation between the two doesn't exist!")
    } yield result
  }
}
