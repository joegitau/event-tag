package com.joegitau.model

import java.time.Instant

case class AttendeeEventRelation(
  eventId:      Long,
  attendeeId:   Long,
  checkinTime:  Option[Instant],
  checkoutTime: Option[Instant],
  created:      Option[Instant],
  modified:     Option[Instant]
)
