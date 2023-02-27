package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem}
import com.joegitau.protocol.AttendeeEventRelationProtocol.AttendeeEventRelationCommand

class AttendeeEventRelationRouter(relationsActor: ActorRef[AttendeeEventRelationCommand])(implicit system: ActorSystem[_]) extends JsonMarshaller {

}
