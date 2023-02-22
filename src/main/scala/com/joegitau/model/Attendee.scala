package com.joegitau.model

import java.time.Instant

case class Attendee(
  id:        Option[Long]   = None,
  firstName: String,
  lastName:  String,
  company:   String,
  email:     String,
  created:   Option[Instant],
  modified:  Option[Instant]
)

case class PatchAttendee(
  firstName: Option[String],
  lastName:  Option[String],
  company:   Option[String],
  email:     Option[String],
  modified:  Option[Instant]
)
