package com.joegitau.model

case class AttendeeEventRelation(
  eventId:    Long,
  attendeeId: Long
)

case class EventWithAttendees(eventId: Long, attendees: Seq[Attendee])

case class AttendeeWithEvents(attendeeId: Long, events: Seq[Event])
