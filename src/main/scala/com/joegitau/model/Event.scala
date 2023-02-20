package com.joegitau.model

import java.sql.Timestamp
import java.time.Instant

case class Event(
  id:          Option[Long]   = None,
  title:       String,
  description: String,
  location:    String,
  startDate:   Timestamp,
  endDate:     Timestamp,
  organizer:   String,
  created:     Option[Instant],
  modified:    Option[Instant]
)

case class PatchEvent(
  title:       Option[String],
  description: Option[String],
  location:    Option[String],
  startDate:   Option[Timestamp],
  endDate:     Option[Timestamp],
  organizer:   Option[String]
)
