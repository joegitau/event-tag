package com.joegitau.slick.dao.attendance

import java.sql.Timestamp
import scala.concurrent.Future

trait AttendanceDao {
  def markAttendance(eventId: Long,
                     attendeeId: Long,
                     checkinTime: Option[Timestamp],
                     checkoutTime: Option[Timestamp]
                    ): Future[String]
}
