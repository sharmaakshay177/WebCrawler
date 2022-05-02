package org.example.webcrawler.process

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.DurationInt

object CookieMain{
  object CookieFabric {
    sealed trait Command
    case class GiveMeCookies(count: Int, replyTo: ActorRef[Reply]) extends Command

    sealed trait Reply
    case class Cookies(count: Int)            extends Reply
    case class InvalidRequest(reason: String) extends Reply

    def apply(): Behaviors.Receive[CookieFabric.GiveMeCookies] =
      Behaviors.receiveMessage { message =>
        if (message.count >= 5) message.replyTo ! InvalidRequest("Too Many Cookies")
        else message.replyTo ! Cookies(message.count)
        Behaviors.same
      }
  }

  import akka.actor.typed.scaladsl.AskPattern._
  import akka.util.Timeout

  // implicit time out required when waiting for future
  implicit val timeout: Timeout = 3.seconds
  // getting actor in scope
  implicit val system: ActorSystem[_] = ???

}

