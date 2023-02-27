package com.joegitau.dao.attendance

import java.time.Instant
import scala.concurrent.Future

trait AttendanceDao {
  def markAttendance(eventId: Long,
                     attendeeId: Long,
                     checkinTime: Option[Instant],
                     checkoutTime: Option[Instant]
                    ): Future[String]
}
