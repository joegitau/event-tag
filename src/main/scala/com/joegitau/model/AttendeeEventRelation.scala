package com.joegitau.model

import java.sql.Timestamp
import java.time.Instant

case class AttendeeEventRelation(
  eventId:      Long,
  attendeeId:   Long,
  checkinTime:  Option[Timestamp],
  checkoutTime: Option[Timestamp],
  created:      Option[Instant],
  modified:     Option[Instant]
)
