package com.joegitau.model

import java.time.Instant

case class Event(
  id:          Option[Long]   = None,
  title:       String,
  description: String,
  location:    String,
  startDate:   Instant,
  endDate:     Instant,
  organizer:   String,
  created:     Option[Instant],
  modified:    Option[Instant]
)

case class PatchEvent(
  title:       Option[String],
  description: Option[String],
  location:    Option[String],
  startDate:   Option[Instant],
  endDate:     Option[Instant],
  organizer:   Option[String]
)
