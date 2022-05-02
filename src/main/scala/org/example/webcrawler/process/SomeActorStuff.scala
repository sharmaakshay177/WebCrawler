package org.example.webcrawler.process

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

// https://doc.akka.io/docs/akka/current/typed/actors.html

// hello world actor
object HelloWorld {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
//  final case class SayHello(whom: String, replyTo: ActorRef[Greet])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info(s"Hello ${message.whom}!!")
    message.replyTo ! Greeted(message.whom, context.self)
    Behaviors.same
  }

  // check on message as well
}

object HelloWorldBot {
  def apply(max: Int): Behavior[HelloWorld.Greeted] = bot(0, max)

  private def bot(greetingCounter: Int, max: Int): Behavior[HelloWorld.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info(s"Greeting $n for ${message.whom}")
      if (n == max) Behaviors.stopped
      else {
        message.from ! HelloWorld.Greet(message.whom, context.self)
        bot(n, max)
      }
    }
}

object HelloWorldMain {
  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      val greeter = context.spawn(HelloWorld(), "greeter")
      Behaviors.receiveMessage{ message =>
        val replyTo = context.spawn(HelloWorldBot(max = 3), message.name)
        greeter ! HelloWorld.Greet(message.name, replyTo)
        Behaviors.same
      }
    }

  def main(args: Array[String]): Unit = {
    val system: ActorSystem[HelloWorldMain.SayHello] =
      ActorSystem(HelloWorldMain(), "hello")

    system ! HelloWorldMain.SayHello("World")
    system ! HelloWorldMain.SayHello("Akka")
  }
}
