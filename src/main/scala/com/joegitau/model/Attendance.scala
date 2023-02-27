package com.joegitau.model

import java.time.Instant

case class Attendance(
  eventId:      Long,
  attendeeId:   Long,
  checkinTime:  Option[Instant],
  checkoutTime: Option[Instant],
)
