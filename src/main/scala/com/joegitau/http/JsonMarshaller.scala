package com.joegitau.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.joegitau.model.{Attendance, Attendee, AttendeeEventRelation, Event}
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat, deserializationError}

import java.time.Instant

trait JsonMarshaller extends DefaultJsonProtocol with SprayJsonSupport {
  // instant implicit
  implicit val instantFormat: RootJsonFormat[Instant] = new RootJsonFormat[Instant] {
    override def read(json: JsValue): Instant = json match {
      case JsNumber(value) => Instant.ofEpochMilli(value.bigDecimal.longValue)
      case _               => deserializationError("Was expecting some JsNumber, bro!")
    }

    override def write(inst: Instant): JsValue = JsNumber(inst.toEpochMilli)
  }

  // entity implicits
  implicit val eventFormat: RootJsonFormat[Event]                            = jsonFormat9(Event)
  implicit val attendeeFormat: RootJsonFormat[Attendee]                      = jsonFormat7(Attendee)
  implicit val attendanceFormat: RootJsonFormat[Attendance]                  = jsonFormat4(Attendance)
  implicit val attEventRelationFormat: RootJsonFormat[AttendeeEventRelation] = jsonFormat6(AttendeeEventRelation)
}
