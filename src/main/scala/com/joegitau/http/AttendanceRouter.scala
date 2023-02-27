package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem}
import com.joegitau.protocol.AttendanceProtocol.AttendanceCommand

class AttendanceRouter(attendanceActor: ActorRef[AttendanceCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {

}
