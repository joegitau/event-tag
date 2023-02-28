package com.joegitau.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.joegitau.model.{Attendance, Attendee, AttendeeEventRelation, Event}
import spray.json.{DefaultJsonProtocol, JsNull, JsNumber, JsString, JsValue, RootJsonFormat, deserializationError}

import java.time.Instant

trait JsonMarshaller extends DefaultJsonProtocol with SprayJsonSupport {
  // instant implicit
  implicit val instantFormat: RootJsonFormat[Instant] = new RootJsonFormat[Instant] {
    override def read(json: JsValue): Instant = json match {
      case JsString(str) => Instant.parse(str)
      case _             => deserializationError("Was expecting some JsString, bro!")
    }

    override def write(inst: Instant): JsValue = JsNumber(inst.toEpochMilli)
  }

  implicit val optInstantFormat: RootJsonFormat[Option[Instant]] = new RootJsonFormat[Option[Instant]] {
    override def write(instOpt: Option[Instant]): JsValue = instOpt match {
      case Some(instant) => JsString(instant.toString)
      case None          => JsNull
    }

    override def read(json: JsValue): Option[Instant] = json match {
      case JsString(str) => Some(Instant.parse(str))
      case JsNull        => None
      case _             => deserializationError("Was expecting some JsString or at least JsNull, bro!")
    }
  }

  // entity implicits
  implicit val eventFormat: RootJsonFormat[Event]                            = jsonFormat9(Event)
  implicit val attendeeFormat: RootJsonFormat[Attendee]                      = jsonFormat7(Attendee)
  implicit val attendanceFormat: RootJsonFormat[Attendance]                  = jsonFormat4(Attendance)
  implicit val attEventRelationFormat: RootJsonFormat[AttendeeEventRelation] = jsonFormat6(AttendeeEventRelation)
}
