package com.joegitau.services

import com.joegitau.dao.event.EventDao
import com.joegitau.dao.attendance.AttendanceDao
import com.joegitau.dao.relations.AttendeeEventRelationDao

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

trait AttendanceService {
  def markAttendance(eventId: Long,
                     attendeeId: Long,
                     checkinTime: Option[Instant],
                     checkoutTime: Option[Instant]
                    ): Future[String]
}

class AttendanceServiceImpl(attendanceDao: AttendanceDao,
                            eventDao: EventDao,
                            attendeeEventRelationDao: AttendeeEventRelationDao
                           )(implicit ec: ExecutionContext) extends AttendanceService {
  override def markAttendance(eventId: Long,
                              attendeeId: Long,
                              checkinTime: Option[Instant],
                              checkoutTime: Option[Instant]
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
