package org.example.webcrawler.process

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Using a sealed trait and case class/objects to represent multiple messages an actor can receive. Handle sessions by
  * using child actors. Handling state by changing behavior. Using multiple actors to represent different parts of a
  * protocol in a type safe way
  */

object ChatRoom {
  sealed trait RoomCommand
  final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand

  sealed trait SessionEvent
  final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
  final case class SessionDenied(reason: String)                      extends SessionEvent
  final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  sealed trait SessionCommand
  final case class PostMessage(message: String)                 extends SessionCommand
  final private case class NotifyClient(message: MessagePosted) extends SessionCommand

  final private case class PublishSessionMessage(screenName: String, message: String) extends RoomCommand

  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { (context, message) =>
      message match {
        case GetSession(screenName, replyTo) =>
          val ses = context.spawn(
            behavior = session(context.self, screenName, replyTo),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name)
          )
          replyTo ! SessionGranted(ses)
          chatRoom(ses :: sessions)
        case PublishSessionMessage(screenName, message) =>
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions.foreach(_ ! notification)
          Behaviors.same
      }
    }

  private def session(
    room: ActorRef[PublishSessionMessage],
    screenName: String,
    client: ActorRef[SessionEvent]
  ): Behavior[SessionCommand] =
    Behaviors.receiveMessage {
      case PostMessage(message) =>
        room ! PublishSessionMessage(screenName, message)
        Behaviors.same
      case NotifyClient(message) =>
        client ! message
        Behaviors.same
    }

}
