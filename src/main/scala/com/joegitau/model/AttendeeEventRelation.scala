package com.joegitau.model

case class AttendeeEventRelation(
  eventId:    Long,
  attendeeId: Long
)

case class EventWithAttendees(event: Event, attendees: Seq[Attendee])

case class AttendeeWithEvents(attendee: Attendee, events: Seq[Event])
