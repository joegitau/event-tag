package com.joegitau.model

import java.sql.Timestamp

case class Attendance(
  eventId:      Long,
  attendeeId:   Long,
  checkinTime:  Option[Timestamp],
  checkoutTime: Option[Timestamp],
)
