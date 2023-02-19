package com.joegitau.model

import java.time.Instant

case class Event(
  id:        Option[Long]   = None,
  name:      String,
  address:   String,
  organizer: String,
  created:   Option[Instant] = None,
  modified:  Option[Instant] = None
)
