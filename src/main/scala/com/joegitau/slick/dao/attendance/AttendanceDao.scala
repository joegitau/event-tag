package com.joegitau.slick.dao.attendance

import java.sql.Timestamp
import scala.concurrent.Future

trait AttendanceDao {
  def markCheckin(attendeeId: Long, eventId: Long, checkinTime: Timestamp): Future[Int]

  def markCheckout(attendeeId: Long, eventId: Long, checkoutTime: Timestamp): Future[Int]
}
