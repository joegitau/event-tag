package com.joegitau.http

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.StatusReply
import akka.util.Timeout
import com.joegitau.protocol.AttendanceProtocol.AttendanceCommand
import com.joegitau.protocol.AttendanceProtocol.AttendanceCommand.MarkAttendance
import com.joegitau.protocol.AttendanceProtocol.AttendanceResponse.AttendanceMarkedRsp
import com.joegitau.utils.Helpers.OptionFns

import java.time.Instant
import scala.concurrent.duration.DurationInt

class AttendanceRouter(attendanceActor: ActorRef[AttendanceCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {
  implicit val scheduler: Scheduler = system.scheduler
  implicit val timeout: Timeout     = 3.seconds

  /**
   * URL: `/attendance/events/${eventId}/attendees/${attendeeId}`
   *
   * The `POST` method on this URL expects a JSON payload in the request body that can be converted to an `Option[Instant]`.
   * Depending on the content of the request body, the route will either assume that the "attendee" is checking out with the
   * provided Instant time, or checking in with a null or None value in the request body, and an Instant time specified as a query parameter checkinTime.
   */
  val routes: Route = path( "attendance" / "events" / LongNumber / "attendees" / LongNumber) { (eventId, attendeeId) =>
    post {
      entity(as[Option[Instant]]) { checkoutTimeOpt =>
        if (checkoutTimeOpt.isDefined) {
          // attendee is checking out
          val checkout = attendanceActor.ask(MarkAttendance(eventId, attendeeId, None, checkoutTimeOpt, _))

          onSuccess(checkout) {
            case StatusReply.Success(AttendanceMarkedRsp(_, attendeeId)) =>
              complete(StatusCodes.Created -> s"Attendee with id: $attendeeId successfully checked out at: ${checkoutTimeOpt.get}")
            case StatusReply.Error(reason) =>
              complete(StatusCodes.InternalServerError -> reason)
          }
        } else {
          // attendee is checking in
          parameter(Symbol("checkinTime").as[Instant]) { checkinTime =>
            val checkin = attendanceActor.ask(MarkAttendance(eventId, attendeeId, checkinTime.toOpt, None, _))

            onSuccess(checkin) {
              case StatusReply.Success(AttendanceMarkedRsp(_, attendeeId)) =>
                complete(StatusCodes.Created -> s"Attendee with id: $attendeeId successfully checked in at: $checkinTime")
              case StatusReply.Error(reason) =>
                complete(StatusCodes.InternalServerError -> reason)
            }
          }
        }
      }
    }
  }
}
