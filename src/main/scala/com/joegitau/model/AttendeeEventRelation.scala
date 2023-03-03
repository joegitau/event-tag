package com.joegitau.model

import java.time.Instant

case class AttendeeEventRelation(
  eventId:      Long,
  attendeeId:   Long,
  checkinTime:  Option[Instant] = Some(Instant.now()),
  checkoutTime: Option[Instant] = None,
  created:      Option[Instant] = Some(Instant.now()),
  modified:     Option[Instant] = None
)
