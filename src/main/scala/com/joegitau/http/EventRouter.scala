package com.joegitau.http

import akka.actor.typed.{ActorRef, ActorSystem}
import com.joegitau.protocol.EventProtocol.EventCommand

class EventRouter(eventActor: ActorRef[EventCommand])(implicit system: ActorSystem[_]) {

}
