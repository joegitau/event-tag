package com.joegitau.model

import java.time.Instant

case class Attendee(
  id:        Option[Long]   = None,
  firstName: String,
  lastName:  String,
  company:   String,
  email:     String,
  created:   Option[Instant] = None,
  modified:  Option[Instant] = None
)
