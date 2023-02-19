package com.joegitau.model

import java.sql.Timestamp
import java.time.Instant

case class AttendeeEventInfo(
  id:           Option[Long] = None,
  eventId:      Int,
  attendeeId:   Int,
  checkinTime:  Option[Timestamp] = None,
  checkoutTime: Option[Timestamp] = None,
  created:      Option[Instant]   = None,
  modified:     Option[Instant]   = None
)
